package testOracle;

import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.utils.*;
import me.corriekay.pokegoutil.utils.ConfigKey;


public class s201224434 {
    
    
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
     * Purpose : Test AutoIncrementer class default constructor
     * Input : AutoIncrementer()
     * Expected : 
     *      return 0
     */
    @Test
    public void testAutoIncrementerConstructor() {
        AutoIncrementer autoIncrementer = new AutoIncrementer();
        assertEquals(0, autoIncrementer.get());
    }
    
    /**
     * Purpose : Test AutoIncrementer class constructor
     * Input : AutoIncrementer(Integer.MAX_VALUE)
     * Expected : 
     *      return Integer.MAX_VALUE
     */
    @Test
    public void testAutoIncrementerConstructor2() {
        AutoIncrementer autoIncrementer = new AutoIncrementer(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, autoIncrementer.get());
    }
    
    /**
     * Purpose : Test AutoIncrementer class get method
     * Input : AutoIncrementer() , get() , get(), get(), get()
     * Expected : 
     *      return 
     */
    @Test
    public void testAutoIncrementerGet() {
        AutoIncrementer autoIncrementer = new AutoIncrementer();
        assertEquals(0, autoIncrementer.get());
        assertEquals(1, autoIncrementer.get());
        assertEquals(2, autoIncrementer.get());
        assertEquals(3, autoIncrementer.get());
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
    public void testUtilitiesIsEven() {
        
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
    public void testUtilitiesLimit() {
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
    public void testUtilitiesPercentage() {
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
    //@Test
    public void testUtilitiesSleep() {
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
    //@Test
    public void testUtilitiesSleepRandom() {
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
    public void testUtilitiesConcatString() {
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
    
    
    
    /**
     * Purpose : test ConfigNew class getBool method
     * Input : getBool(DEVELOPFLAG)
     * Expected : 
     *      return false
     */
    @Test
    public void testConfigNewGetBool() {
        File file = new File(System.getProperty("user.dir"), "config.json");
        file.delete();
        ConfigNew myCFG = ConfigNew.getConfig();
        ConfigKey myConfigKey = ConfigKey.values()[0];
            
        assertEquals(false, myCFG.getBool(myConfigKey));
    }
    
    /**
     * Purpose : test ConfigNew class getString method
     * Input : getString(LOGIN_GOOGLE_AUTH_TOKEN)
     * Expected : 
     *      return null
     */
    @Test
    public void testConfigNewGetString() {

        ConfigNew myCFG = ConfigNew.getConfig();
        ConfigKey myConfigKey = ConfigKey.values()[2];
        
        assertEquals(null, myCFG.getString(myConfigKey));
    }
    
    /**
     * Purpose : test ConfigNew class getInt method
     * Input : getInt(WINDOW_WIDTH)
     * Expected : 
     *      return 800
     */
    @Test
    public void testConfigNewGetInt() {

        ConfigNew myCFG = ConfigNew.getConfig();
        ConfigKey myConfigKey = ConfigKey.values()[8];
            
        assertEquals(800, myCFG.getInt(myConfigKey));
    }
    
    
    /**
     * Purpose : test ConfigNew class setBool method
     * Input : setBool(DEVELOPFLAG, true), getBool(DEVELOPFLAG)
     * Expected : 
     *      return true
     */
    @Test
    public void testConfigNewSetBool() {

        ConfigNew myCFG = ConfigNew.getConfig();
        ConfigKey myConfigKey = ConfigKey.values()[0];
        myCFG.setBool(myConfigKey, true);
        assertEquals(true, myCFG.getBool(myConfigKey));
    }
    
    /**
     * Purpose : test ConfigNew class setString method
     * Input : setString(LOGIN_GOOGLE_APP_USERNAME, "kbbn2001"), 
     *          getString(LOGIN_GOOGLE_APP_USERNAME)
     * Expected : 
     *      return "kbbn2001"
     */
    @Test
    public void testConfigNewSetString() {

        ConfigNew myCFG = ConfigNew.getConfig();
        ConfigKey myConfigKey = ConfigKey.values()[3];
        String myString = "kbbn2001";
        myCFG.setString(myConfigKey, myString);
        assertEquals(myString, myCFG.getString(myConfigKey));
    }
    
    /**
     * Purpose : test ConfigNew class setInt method
     * Input : setInt(WINDOW_WIDTH , 1000)
     *          getInt(WINDOW_WIDTH)
     * Expected : 
     *      return 1000
     */
    @Test
    public void testConfigNewSetInt() {

        ConfigNew myCFG = ConfigNew.getConfig();
        ConfigKey myConfigKey = ConfigKey.values()[8];
        int value = 1000;
        myCFG.setInt(myConfigKey, value);
        assertEquals(value, myCFG.getInt(myConfigKey));
    }
    
    /**
     * Purpose : test ConfigNew class delete method
     * Input : setInt(WINDOW_WIDTH , 1900), delete(WINDOW_WIDTH),
     *          getInt(WINDOW_WIDTH)
     * Expected : 
     *      return 800
     */
    @Test
    public void testConfigNewDelete() {

        ConfigNew myCFG = ConfigNew.getConfig();
        ConfigKey myConfigKey = ConfigKey.values()[8];
        int value = 1900;
        myCFG.setInt(myConfigKey, value);
        myCFG.delete(myConfigKey);
        assertEquals(800, myCFG.getInt(myConfigKey));
    }
    
    
    
    
    
}
