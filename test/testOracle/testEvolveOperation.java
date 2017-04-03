package testOracle;
import static org.junit.Assert.*;

import org.junit.Test;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.data.models.operations.EvolveOperation;
import me.corriekay.pokegoutil.data.models.operations.FavoriteOperation;

public class testEvolveOperation {

    private PokemonModel pokemons;
    final PokemonModel pokemon = pokemons;
    
    EvolveOperation evolve;
    
    @Test
    public void testFordoOperation() {
        assertNotNull(evolve.validateOperation());
    }
    
    @Test
    public void testForgetOperationId() {
        assertTrue(3000<=evolve.getDelay()&&12000>=evolve.getDelay());
    }
}
