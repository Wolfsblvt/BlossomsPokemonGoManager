package testOracle;
import static org.junit.Assert.*;

import org.junit.Test;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.data.models.operations.FavoriteOperation;
import me.corriekay.pokegoutil.data.models.operations.PowerupOperation;
import me.corriekay.pokegoutil.gui.enums.OperationId;

public class testPowerupOperation {

    private PokemonModel pokemons;
    final PokemonModel pokemon = pokemons;
    
    ObservableList<PokemonModel> list;
    final ObservableList<PokemonModel> pokemonList = list;
    PowerupOperation powerup;
    
    @Test
    public void testGetOperationId() {
        assertEquals(OperationId.POWERUP, powerup.getOperationId());
    }
    
    @Test
    public void testValidateOperation() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        assertNotNull(powerup.validateOperation());
    }

}
