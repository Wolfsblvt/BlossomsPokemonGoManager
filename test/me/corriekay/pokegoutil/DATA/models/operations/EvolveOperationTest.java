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
 * Test for EvolveOperation.
 */
public class EvolveOperationTest {

    private static final String RESULT_SHOULD_FAIL = "Result should fail";
    private PokemonModel pokemon;
    private EvolveOperation operation;

    /**
     * Before every test.
     */
    @Before
    public void beforeTest() {
        pokemon = mock(PokemonModel.class);
        operation = spy(EvolveOperation.class);
        operation.pokemon = pokemon;
    }

    /**
     * Evolve a pokemon that is in gym.
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
     * Evolve a pokemon that is not evolvable.
     *
     * @throws InvalidCurrencyException invalid currency
     * @throws LoginFailedException login fail
     * @throws RemoteServerException sever error
     */
    @Test
    public void pokemonIsNotEvolvable() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        final int noCandiesToEvolve = 0;
        doReturn(noCandiesToEvolve).when(pokemon).getCandies2Evlv();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat(RESULT_SHOULD_FAIL, false, is(result.isSuccess()));
        Assert.assertThat("Pokemon cannot evolve", result.getOperationError(), is(OperationError.NOT_EVOLVABLE));
    }

    /**
     * Evolve a pokemon when there is not enough candies.
     *
     * @throws InvalidCurrencyException invalid currency
     * @throws LoginFailedException login fail
     * @throws RemoteServerException sever error
     */
    @Test
    public void notEnoughCandies() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        final int insufficentCandies = 24;
        final int candiesToEvolve = 25;
        doReturn(insufficentCandies).when(pokemon).getCandies();
        doReturn(candiesToEvolve).when(pokemon).getCandies2Evlv();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat(RESULT_SHOULD_FAIL, false, is(result.isSuccess()));
        Assert.assertThat("Not enough candies", result.getOperationError(),
                is(OperationError.INSUFFICENT_CANDIES));
    }

    /**
     * Evolve a pokemon successfully.
     *
     * @throws InvalidCurrencyException invalid currency
     * @throws LoginFailedException login fail
     * @throws RemoteServerException sever error
     */
    @Test
    public void sucessfullyEvolve() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        final int sufficentCandies = 25;
        final int candiesToEvolve = 25;
        doReturn(sufficentCandies).when(pokemon).getCandies();
        doReturn(candiesToEvolve).when(pokemon).getCandies2Evlv();
        doReturn(new BpmOperationResult()).when(operation).doOperation();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat("Evolve should be successful", true, is(result.isSuccess()));
    }
}
