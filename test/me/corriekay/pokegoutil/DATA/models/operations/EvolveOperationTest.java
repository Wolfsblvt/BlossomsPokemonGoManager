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

public class EvolveOperationTest {

    private PokemonModel pokemon;
    private EvolveOperation operation;

    @Before
    public void beforeTest() {
        pokemon = mock(PokemonModel.class);
        operation = spy(EvolveOperation.class);
        operation.pokemon = pokemon;
    }

    @Test
    public void pokemonIsInGym() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(true).when(pokemon).isInGym();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat("Result should fail", false, is(result.isSuccess()));
        Assert.assertThat("Pokemon in gym", result.getOperationError(), is(OperationError.IN_GYM));
    }

    @Test
    public void pokemonIsNotEvolvable() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(0).when(pokemon).getCandies2Evlv();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat("Result should fail", false, is(result.isSuccess()));
        Assert.assertThat("Pokemon cannot evolve", result.getOperationError(), is(OperationError.NOT_EVOLVABLE));
    }

    @Test
    public void notEnoughCandies() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(24).when(pokemon).getCandies();
        doReturn(25).when(pokemon).getCandies2Evlv();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat("Result should fail", false, is(result.isSuccess()));
        Assert.assertThat("Not enough candies", result.getOperationError(),
                is(OperationError.INSUFFICENT_CANDIES));
    }

    @Test
    public void sucessfullyEvolve() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        doReturn(25).when(pokemon).getCandies();
        doReturn(25).when(pokemon).getCandies2Evlv();
        doReturn(new BpmOperationResult()).when(operation).doOperation();

        final BpmOperationResult result = operation.execute();

        Assert.assertThat("Evolve should be successful", true, is(result.isSuccess()));
    }
}
