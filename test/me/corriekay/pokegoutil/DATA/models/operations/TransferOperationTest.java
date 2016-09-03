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

public class TransferOperationTest {
    private PokemonModel pokemon;
    private TransferOperation operation;

    @Before
    public void beforeTest() {
        pokemon = mock(PokemonModel.class);
        operation = spy(TransferOperation.class);
        operation.pokemon = pokemon;
    }

    @Test
    public void pokemonIsFavorite() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(true).when(pokemon).isIsFavorite();

        BpmOperationResult result = operation.execute();

        Assert.assertThat("Result should fail", false, is(result.isSuccess()));
        Assert.assertThat("Pokemon is favorite", result.getOperationError(), is(OperationError.IS_FAVORITE));
    }

    @Test
    public void pokemonIsInGym() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(true).when(pokemon).isInGym();

        BpmOperationResult result = operation.execute();

        Assert.assertThat("Result should fail", false, is(result.isSuccess()));
        Assert.assertThat("Pokemon in gym", result.getOperationError(), is(OperationError.IN_GYM));
    }

    @Test
    public void sucessfullyTransfer() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(new BpmOperationResult()).when(operation).doOperation();

        BpmOperationResult result = operation.execute();

        Assert.assertThat("Transfer should be successful", true, is(result.isSuccess()));
    }
}
