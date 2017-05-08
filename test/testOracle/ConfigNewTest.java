package testOracle;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;

public class ConfigNewTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        File file = new File(System.getProperty("user.dir"), "config.json");
        file.delete();
    }

    @After
    public void tearDown() throws Exception {
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
