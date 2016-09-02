package me.corriekay.pokegoutil.DATA.models.operations;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BPMOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

public class EvolveOperationTest {

    private PokemonModel pokemon;
    private EvolveOperation operation;

    @Before
    public void beforeTest() {
        pokemon = mock(PokemonModel.class);
        // operation = new EvolveOperation(pokemon);
        operation = spy(EvolveOperation.class);
        operation.pokemon = pokemon;
    }

    @Test
    public void PokemonIsInGym() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        when(pokemon.isInGym()).thenReturn(true);

        BPMOperationResult result = operation.execute();

        Assert.assertThat("Result should fail", false, is(result.isSuccess()));
        Assert.assertThat("Pokemon in gym", result.getOperationError(), is(OperationError.IN_GYM));
    }

    @Test
    public void PokemonIsNotEvolvable() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        when(pokemon.getCandies2Evlv()).thenReturn(0);

        BPMOperationResult result = operation.execute();

        Assert.assertThat("Result should fail", false, is(result.isSuccess()));
        Assert.assertThat("Pokemon cannot evolve", result.getOperationError(), is(OperationError.NOT_EVOLVABLE));
    }

    @Test
    public void NotEnoughCandies() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        when(pokemon.getCandies()).thenReturn(24);
        when(pokemon.getCandies2Evlv()).thenReturn(25);

        BPMOperationResult result = operation.execute();
        Assert.assertThat("Result should fail", false, is(result.isSuccess()));
        Assert.assertThat("Not enough candies", result.getOperationError(),
                is(OperationError.INSUFFICENT_CANDIES));
    }

    @Test
    public void SucessfullyEvolve() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        when(pokemon.getCandies()).thenReturn(25);
        when(pokemon.getCandies2Evlv()).thenReturn(25);
        when(operation.doOperation()).thenReturn(new BPMOperationResult());

        BPMOperationResult result = operation.execute();

        Assert.assertThat("Result should pass", true, is(result.isSuccess()));
    }
}
