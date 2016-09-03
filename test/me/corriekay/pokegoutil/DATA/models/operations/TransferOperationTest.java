package me.corriekay.pokegoutil.DATA.models.operations;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BpmOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;

/**
 * Test for TransferOperation.
 */
public class TransferOperationTest {
    private static final String RESULT_SHOULD_FAIL = "Result should fail";
    private PokemonModel pokemon;
    private TransferOperation operation;

    /**
     * Before every test.
     */
    @Before
    public void beforeTest() {
        pokemon = mock(PokemonModel.class);
        operation = spy(TransferOperation.class);
        operation.pokemon = pokemon;
    }

    /**
     * Transfer a pokemon that is favorite.
     *
     * @throws InvalidCurrencyException invalid currency
     * @throws LoginFailedException login fail
     * @throws RemoteServerException sever error
     */
    @Test
    public void pokemonIsFavorite() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(true).when(pokemon).isIsFavorite();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat(RESULT_SHOULD_FAIL, false, is(result.isSuccess()));
        Assert.assertThat("Pokemon is favorite", result.getOperationError(), is(OperationError.IS_FAVORITE));
    }

    /**
     * Transfer a pokemon that is in gym.
     *
     * @throws InvalidCurrencyException invalid currency
     * @throws LoginFailedException login fail
     * @throws RemoteServerException sever error
     */
    @Test
    public void pokemonIsInGym() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(true).when(pokemon).isInGym();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat(RESULT_SHOULD_FAIL, false, is(result.isSuccess()));
        Assert.assertThat("Pokemon in gym", result.getOperationError(), is(OperationError.IN_GYM));
    }

    /**
     * Transfer a pokemon successfully.
     *
     * @throws InvalidCurrencyException invalid currency
     * @throws LoginFailedException login failed
     * @throws RemoteServerException server error
     */
    @Test
    public void sucessfullyTransfer() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(new BpmOperationResult()).when(operation).doOperation();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat("Transfer should be successful", true, is(result.isSuccess()));
    }
}
