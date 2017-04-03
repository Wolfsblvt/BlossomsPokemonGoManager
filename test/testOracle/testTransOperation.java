package testOracle;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.data.models.operations.TransferOperation;
import me.corriekay.pokegoutil.gui.enums.OperationId;

public class testTransOperation {

    private PokemonModel pokemons;
    final PokemonModel pokemon = pokemons;
    private static final OperationId EVOLVE = null;
    TransferOperation transfer = null;
    
    @Before
    public void setUp() {
        transfer.generateOperation(EVOLVE, pokemon);
    }
    
    @Test//
    public void testForValidateOperation() {
        assertEquals(new BpmOperationResult(), transfer.validateOperation());
    }
    
    @Test
    public void testForGetDelay() {
        assertTrue(1000<=transfer.getDelay()&&5000>=transfer.getDelay());
    }
    
    @Test
    public void testForvalidateOperation() {
        assertEquals(new BpmOperationResult(), transfer.validateOperation());
    }

}
