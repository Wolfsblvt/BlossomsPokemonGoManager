package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pokegoapi.api.PokemonGo;

import me.corriekay.pokegoutil.data.managers.AccountController;
import me.corriekay.pokegoutil.data.managers.AccountManager;
import me.corriekay.pokegoutil.data.managers.GlobalSettingsController;
import me.corriekay.pokegoutil.data.managers.InventoryManager;
import me.corriekay.pokegoutil.data.managers.PokemonBagManager;
import me.corriekay.pokegoutil.data.managers.ProfileManager;
import me.corriekay.pokegoutil.data.models.LoginData;
import okhttp3.OkHttpClient;

public class managerstest {
    AccountController Acontroller;
    AccountManager Amanager;
    GlobalSettingsController gController;
    InventoryManager Imanager;
    PokemonBagManager Bmanager;
    ProfileManager Pmanager;
    PokemonGo go;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        go = new PokemonGo(new OkHttpClient());
    }

    @After
    public void tearDown() throws Exception {
    }

    
    /*
    Purpose : AccountController LogOn Attempt
    Input :
    1. PTC Login
    2. Saved PTC Login
    3. Google Login(Token)
    4. Saved Google Login
    5. Google Login(App Password)
    6. alertBox Cancel
    */
    @Test                       
    public void logOntest() {
        Acontroller = AccountController.getInstance();
        Acontroller.initialize();
        Acontroller.logOn();
        Acontroller.getPlayerProfile();
    }//LoginHelper Hashkey Error

    
    /*
    Purpose : AccountManager login Attempt
    Input : LoginData("login_man","Password123!")
    */
    @Test
    public void logintest() {
        Amanager = AccountManager.getInstance();
        LoginData data = new LoginData("login_man","Password123!");
        Amanager.login(data);
        assertEquals("login_man",Amanager.getLoginData().getUsername());
        assertNotNull(Amanager.getPlayerAccount());
        assertNotNull(Amanager.getPlayerProfile());
    }//LoginHelper Hashkey Error
    
    @Test
    public void GlobalSettingInitializingtest() {
        gController = GlobalSettingsController.getGlobalSettingsController();
        gController.setup();
        GlobalSettingsController.getGlobalSettingsController();
        gController.getLogController();
    }
    
    @Test
    public void InventoryInitializingtest() {
        Imanager.initialize(go);
        try{
            Imanager.getInventories();
        }catch(Exception e){
            
        }
    }
    
    @Test
    public void BagInitializingtest() {
        Bmanager.initialize(go);
        Bmanager.getNbPokemon();
        Bmanager.getAllPokemon();
    }
    
    @Test
    public void Profiletest() {
        Pmanager.initialize(go);
        Pmanager.getProfile();
    }
}
