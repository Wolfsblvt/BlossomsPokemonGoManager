package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.corriekay.pokegoutil.data.enums.OperationError;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.BpmResult;

public class BpmResultTest {

    public BpmResult bpmResult;
    
    @Before
    public void setUp() throws Exception {
        bpmResult = new BpmResult();
    }
    
    /**
     * Purpose: Construct BpmResult class.
     * Input: void -> string "Error!"
     * Expected:
     *          isSuccess() is true. ->
     *          isSuccess() is false.
     */
    @Test
    public void testConstructor() {
        assertEquals(true, bpmResult.isSuccess());
        bpmResult = null;
        bpmResult = new BpmResult("Error!");
        assertEquals(false, bpmResult.isSuccess());
    }
    
    /**
     * Purpose: Get error message.
     * Input: setErrorMessage string void -> "Error!"
     * Expected:
     *          getErrorMessage() is null. ->
     *          getErrorMessage() is "Error!".
     */
    @Test
    public void tesGetErrorMessage() {
        assertEquals(null, bpmResult.getErrorMessage());
        bpmResult.setErrorMessage("Error!");
        assertEquals("Error!", bpmResult.getErrorMessage());
    }
    
    /**
     * Purpose: Get is success.
     * Input: setSuccess boolean true -> false
     * Expected:
     *          isSuccess() is true. ->
     *          isSuccess() is false.
     */
    @Test
    public void testIsSuccess() {
        bpmResult.setSuccess(true);
        assertEquals(true, bpmResult.isSuccess());
        bpmResult.setSuccess(false);
        assertEquals(false, bpmResult.isSuccess());
    }
    
    /**
     * Purpose: Set error message.
     * Input: setErrorMessage string "Error!"
     * Expected:
     *          getErrorMessage() is "Error!".
     */
    @Test
    public void testSetErrorMessage() {
        bpmResult.setErrorMessage("Error!");
        assertEquals("Error!", bpmResult.getErrorMessage());
    }
    
    /**
     * Purpose: Set success state.
     * Input: setSuccess boolean true -> false
     * Expected:
     *          isSuccess() is true. ->
     *          isSuccess() is false.
     */
    @Test
    public void testSetSuccess() {
        bpmResult.setSuccess(true);
        assertEquals(true, bpmResult.isSuccess());
        bpmResult.setSuccess(false);
        assertEquals(false, bpmResult.isSuccess());
    }
    
    /**
     * Purpose: Destroy BpmResult class.
     * Input: void
     * Expected:
     *          bpmResult is null.
     */
    @After
    public void tearDown() throws Exception {
        bpmResult = null;
        assertNull(bpmResult);
    }
}
