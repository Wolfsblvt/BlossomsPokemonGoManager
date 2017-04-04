package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import me.corriekay.pokegoutil.utils.Utilities;

public class UtilitiesTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    /**
     * Purpose : Test Utilities class isEven method
     * Input : isEven(4), isEven(Integer.MAX_VALUE), 
     *          isEven(Integer.MAX_VALUE - 1), 
     *          isEven(Integer.MIN_VALUE), 
     *          isEven(Integer.MIN_VALUE + 1)
     * Expected : 
     *      return true, false, true, true, false
     */
    @Test
    public void testIsEven() {
        
        assertTrue(Utilities.isEven(4));
        
        assertFalse(Utilities.isEven(Integer.MAX_VALUE));
        assertTrue(Utilities.isEven(Integer.MAX_VALUE - 1));
        
        assertTrue(Utilities.isEven(Integer.MIN_VALUE));
        assertFalse(Utilities.isEven(Integer.MIN_VALUE + 1));
    }
    
    
    
    /**
     * Purpose : Test Utilities class limit method
     * Input : limit(0.0, 1.0, 100.0), limit(0.0, 0.0, 100.0), 
     *          limit(0.0, -0.001, 100.0), limit(0.0, 100.0, 100.0)
     *          limit(0.0, 100.1, 100.0)
     * Expected : 
     *      return 0.1, 0.0, 0.0, 100.0, 100.0
     */
    @Test
    public void testLimit() {
        float min = 0.0f;
        float max = 100.0f;
        float value = 1.0f;
        assertEquals(value, Utilities.limit(min, value, max), 0.0000001);
        
        value = 0.0f;
        assertEquals(value, Utilities.limit(min, value, max), 0.0000001);
        
        value = -0.001f;
        assertEquals(min, Utilities.limit(min, value, max), 0.0000001);
        
        value = 15.0f;
        assertEquals(value, Utilities.limit(min, value, max), 0.0000001);
        
        value = 100.0f;
        assertEquals(value, Utilities.limit(min, value, max), 0.0000001);
        
        value = 100.1f;
        assertEquals(max, Utilities.limit(min, value, max), 0.0000001);
    }
    
    /**
     * Purpose : Test Utilities class percentage method
     * Input : percentage(1.0, 100.0), percentage(1.0, 0.0)
     * Expected : 
     *      return 0.01, 1.0
     */
    @Test
    public void testPercentage() {
        float num1 = 1.0f;
        float num2 = 100.0f;
        assertEquals(0.01f, Utilities.percentage(num1, num2), 0.000001);
        
        num2 = 0.0f;
        assertEquals(1.0f, Utilities.percentage(num1, num2), 0.000001);
    }
    
    /**
     * Purpose : Test Utilities class sleep method
     * Input : sleep(10)
     * Expected : 
     *      return 10 + a,   ( 0 <= a < 1)
     */
    @Test
    public void testSleep() {
        int sleepTime = 10;
        long milis = System.currentTimeMillis();
        Utilities.sleep(sleepTime);
        long milis2 = System.currentTimeMillis();
        System.out.println(milis2 - milis);
        assertEquals(milis2 - milis, sleepTime, 1);
    }
    
    /**
     * Purpose : Test Utilities class sleepRandom method
     * Input : sleep(10, 100)
     * Expected : 
     *      return 10 ~ 100
     */
    @Test
    public void testSleepRandom() {
        int sleepTimeMin = 10;
        int sleepTimeMax = 100;
        long milis = System.currentTimeMillis();
        Utilities.sleepRandom(sleepTimeMin, sleepTimeMax);
        long milis2 = System.currentTimeMillis();
        boolean result = false;
        if ( milis2 - milis >= sleepTimeMin && milis2 - milis <= sleepTimeMax )
            result = true;
        assertTrue(result);
    }
    
    /**
     * Purpose : Test Utilities class concatString method
     * Input : concatString('!', {"my name is", "kjw"}), 
     *          concatString('!', {"one"}), 
     *          concatString('!', {})
     * Expected : 
     *      return "my name is!kjw", "one", ""
     */
    @Test
    public void testConcatString() {
        String myString[] = {"my name is", "kjw"};
        String result = Utilities.concatString('!', myString);
        assertEquals("my name is!kjw", result);
        
        String myString2[] = {"one"};
        result = Utilities.concatString('!', myString2);
        assertEquals("one", result);
        
        String myString3[] = {};
        result = Utilities.concatString('!', myString3);
        assertEquals("", result);
    }

}
