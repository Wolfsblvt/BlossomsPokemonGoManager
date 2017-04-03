package testOracle;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.data.models.operations.EvolveOperation;
import me.corriekay.pokegoutil.data.models.operations.FavoriteOperation;
import me.corriekay.pokegoutil.data.models.operations.Operation;
import me.corriekay.pokegoutil.data.models.operations.PowerupOperation;
import me.corriekay.pokegoutil.data.models.operations.RenameOperation;
import me.corriekay.pokegoutil.data.models.operations.TransferOperation;
import me.corriekay.pokegoutil.gui.enums.OperationId;

public class testOperation {
    
    private static final OperationId EVOLVE = null;
    private static final OperationId FAVORIE = null;
    private static final OperationId POWERUP = null;
    private static final OperationId RENAME = null;
    private static final OperationId TRANSFER = null;
    
    public PokemonModel pokemon;
   
    final OperationId operationId = EVOLVE;
    
    ObservableList<PokemonModel> list;
    final ObservableList<PokemonModel> pokemonList = list;
    
    Operation operate;
    /**
     * Purpose : Test the class Operation, function generateOperations
     * Input : AutoIncrementer()
     * Expected : 
     *      return 0
     */
    @Test
    public void testGenerateOperations() {
        assertNull(operate.generateOperations(EVOLVE, pokemonList));
    }
    
    /**
     * Purpose : Test the class Operation, function generateOperation
     * Input : OperationId(EVOLVE, FAVORIE, POWERUP, RENAME, TRANSFER) , PokemonModel
     * Expected : 
     *      new (OperationId)Operation(PokemonModel)
     */
    @Test
    public void testGenerateOperation() {
        assertEquals(new EvolveOperation(pokemon), operate.generateOperation(EVOLVE, pokemon));
        assertEquals(new FavoriteOperation(pokemon), operate.generateOperation(FAVORIE, pokemon));
        assertEquals(new PowerupOperation(pokemon), operate.generateOperation(POWERUP, pokemon));
        assertEquals(new RenameOperation(pokemon), operate.generateOperation(RENAME, pokemon));
        assertEquals(new TransferOperation(pokemon), operate.generateOperation(TRANSFER, pokemon));
    }
    
    @Test
    public void testExecute() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        operate.execute();
        operate.doDelay();
    }
    
    @Test
    public void testForGetDelay() {
        assertNotNull(operate.getDelay());
    }
}
