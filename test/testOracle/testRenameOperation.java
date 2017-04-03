package testOracle;
import static org.junit.Assert.*;

import org.junit.Test;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.data.models.operations.FavoriteOperation;
import me.corriekay.pokegoutil.data.models.operations.RenameOperation;
import me.corriekay.pokegoutil.gui.enums.OperationId;

public class testRenameOperation {

    private PokemonModel pokemons;
    final PokemonModel pokemon = pokemons;
    
    RenameOperation rename;
    
    @Test
    public void testForGetDelay() {
        assertTrue(1000<=rename.getDelay()&&5000>=rename.getDelay());
    }
    
    @Test
    public void testForGetOperationId() {
        assertEquals(OperationId.RENAME, rename.getOperationId());
    }
    
    @Test
    public void testForValidateOperation() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        assertNotNull(rename.validateOperation());
    }

}
