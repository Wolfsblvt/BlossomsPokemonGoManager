package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.corriekay.pokegoutil.data.enums.OperationError;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.gui.enums.OperationId;

public class BpmOperationResultTest {
    
    private BpmOperationResult bpmOperationResult;

    @Before
    public void setUp() throws Exception {
        bpmOperationResult = new BpmOperationResult();
    }
    
    /**
     * Purpose: Destroy BpmOperationResult class.
     * Input: void
     * Expected:
     *          bpmOperationResult is null.
     */
    @After
    public void tearDown() throws Exception {
        bpmOperationResult = null;
        assertNull(bpmOperationResult);
    }
    
    /**
     * Purpose: Construct BpmOperationResult class.
     * Input: void -> string "Error!", OperationError.EVOLVE_FAIL
     * Expected:
     *          getSuccessMessageList().isEmpty() is true. ->
     *          getOperationError() is OperationError.EVOLVE_FAIL.
     */
    @Test
    public void testConstructor() {
        assertEquals(true, bpmOperationResult.getSuccessMessageList().isEmpty());
        bpmOperationResult = null;
        bpmOperationResult = new BpmOperationResult("Error!", OperationError.EVOLVE_FAIL);
        assertEquals(OperationError.EVOLVE_FAIL, bpmOperationResult.getOperationError());
    }
    
    /**
     * Purpose: Add success message into success message list.
     * Input: add string "Success!"
     * Expected:
     *          getSuccessMessageList().size() is 1.
     */
    @Test
    public void testAddSuccessMessage() {
        bpmOperationResult.addSuccessMessage("Success!");
        assertEquals(1, bpmOperationResult.getSuccessMessageList().size());
    }
    
    /**
     * Purpose: Get next operation.
     * Input: setNextOperation void -> OperationId.EVOLVE
     * Expected:
     *          getNextOperation() is null -> OperationId.EVOLVE.
     */
    @Test
    public void testGetNextOperation() {
        assertEquals(null, bpmOperationResult.getNextOperation());
        bpmOperationResult.setNextOperation(OperationId.EVOLVE);
        assertEquals(OperationId.EVOLVE, bpmOperationResult.getNextOperation());
    }
    
    /**
     * Purpose: Get operation error.
     * Input: setOperationError void -> OperationError.EVOLVE_FAIL
     * Expected:
     *          getOperationError() is null. -> OperationError.EVOLVE_FAIL.
     */
    @Test
    public void testGetOperationError() {
        assertEquals(null, bpmOperationResult.getOperationError());
        bpmOperationResult.setOperationError(OperationError.EVOLVE_FAIL);
        assertEquals(OperationError.EVOLVE_FAIL, bpmOperationResult.getOperationError());
    }
    
    /**
     * Purpose: Get success message list.
     * Input: add string void -> "Success!"
     * Expected:
     *          getSuccessMessageList().isEmpty() is true. ->
     *          getSuccessMessageList().size() is 1.
     */
    @Test
    public void testGetSuccessMessageList() {
        assertEquals(true, bpmOperationResult.getSuccessMessageList().isEmpty());
        bpmOperationResult.getSuccessMessageList().add("Success!");
        assertEquals(1, bpmOperationResult.getSuccessMessageList().size());
    }
    
    /**
     * Purpose: Get state that has next operation.
     * Input: setNextOperation void -> OperationId.EVOLVE
     * Expected:
     *          hasNextOperation() is false -> true.
     */
    @Test
    public void testHasNextOperation() {
        assertEquals(false, bpmOperationResult.hasNextOperation());
        bpmOperationResult.setNextOperation(OperationId.EVOLVE);
        assertEquals(true, bpmOperationResult.hasNextOperation());
    }
    
    /**
     * Purpose: Set next operation.
     * Input: setNextOperation OperationId.EVOLVE
     * Expected:
     *          getNextOperation() is OperationId.EVOLVE.  
     */
    @Test
    public void testSetNextOperation() {
        bpmOperationResult.setNextOperation(OperationId.EVOLVE);
        assertEquals(OperationId.EVOLVE, bpmOperationResult.getNextOperation());
    }
    
    /**
     * Purpose: Set operation error.
     * Input: setOperationError OperationError.EVOLVE_FAIL
     * Expected:
     *          getOperationError() is OperationError.EVOLVE_FAIL.
     */
    @Test
    public void testSetOperationError() {
        bpmOperationResult.setOperationError(OperationError.EVOLVE_FAIL);
        assertEquals(OperationError.EVOLVE_FAIL, bpmOperationResult.getOperationError());
    }

}
