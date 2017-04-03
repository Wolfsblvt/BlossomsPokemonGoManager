package testOracle;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.data.models.operations.FavoriteOperation;
import me.corriekay.pokegoutil.data.models.operations.Operation;
import me.corriekay.pokegoutil.gui.enums.OperationId;

public class testFavoriteOperation {

    private PokemonModel pokemons;
    final PokemonModel pokemon = pokemons;
    private static final OperationId EVOLVE = null;
    final OperationId operationId = EVOLVE;
    FavoriteOperation favorite;
    
    @Before
    public void setUp() {
        favorite.generateOperation(EVOLVE, pokemon);
    }
    
    @Test
    public void testForGetDelay() {
        assertTrue(1000<=favorite.getDelay()&&5000>=favorite.getDelay());
    }
    
    @Test
    public void testForGetOpertionId() {
        assertEquals(OperationId.FAVORITE, favorite.getOperationId());
    }
    
    @Test
    public void testForValidateOperation() {
        assertNotNull(favorite.validateOperation());
    }
}
