package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.corriekay.pokegoutil.data.enums.LoginType;
import me.corriekay.pokegoutil.data.models.LoginData;
public class LoginDataTest {
    private LoginData loginData;

    @Before
    public void setUp() throws Exception {
        loginData = new LoginData();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Purpose: Construct LoginData class with no parameter.
     * Input: void
     * Expected:
     *          loginData.getLoginType() is LoginType.NONE.
     */
    @Test
    public void testConstructor() {
        loginData = null;
        loginData = new LoginData();
        assertEquals(LoginType.NONE, loginData.getLoginType());
    }

    /**
     * Purpose: Construct LoginData class with 1 string parameter.
     * Input: string "Token!"
     * Expected:
     *          loginData.getLoginType() is LoginType.GOOGLE_AUTH.
     */
    @Test
    public void testConstructorWithOneString() {
        loginData = null;
        loginData = new LoginData("Token!");
        assertEquals(LoginType.GOOGLE_AUTH, loginData.getLoginType());
    }

    /**
     * Purpose: Construct LoginData class with 2 string parameters.
     * Input: string "Username!", "Password!"
     * Expected:
     *          loginData.getLoginType() is LoginType.PTC.
     */
    @Test
    public void testConstructorWithTwoStrings() {
        loginData = null;
        loginData = new LoginData("Username!", "Password!");
        assertEquals(LoginType.PTC, loginData.getLoginType());
    }

    /**
     * Purpose: Construct LoginData class with 3 string parameters.
     * Input: string "Username!", "Password!", "Token!"
     * Expected:
     *          loginData.getLoginType() is LoginType.ALL.
     */
    @Test
    public void testConstructorWithThreeStrings() {
        loginData = null;
        loginData = new LoginData("Username!", "Password!", "Token!");
        assertEquals(LoginType.ALL, loginData.getLoginType());
    }

    /**
     * Purpose: Get login type.
     * Input: setLoginType LoginType.GOOGLE_AUTH
     * Expected:
     *          getLoginType() is LoginType.GOOGLE_AUTH.
     */
    @Test
    public void testGetLoginType() {
        loginData.setLoginType(LoginType.GOOGLE_AUTH);
        assertEquals(LoginType.GOOGLE_AUTH, loginData.getLoginType());
    }

    /**
     * Purpose: Get password.
     * Input: setPassword string "Password!"
     * Expected:
     *          getPassword() is "Password!".
     */
    @Test
    public void testGetPassword() {
        loginData.setPassword("Password!");
        assertEquals("Password!", loginData.getPassword());
    }

    /**
     * Purpose: Get token.
     * Input: setToken string "Token!"
     * Expected:
     *          getToken() is "Token!".
     */
    @Test
    public void testGetToken() {
        loginData.setToken("Token!");
        assertEquals("Token!", loginData.getToken());
    }

    /**
     * Purpose: Get username.
     * Input: setUsername string "Username!"
     * Expected:
     *          getUsername() is "Username!".
     */
    @Test
    public void testGetUsername() {
        loginData.setUsername("Username!");
        assertEquals("Username!", loginData.getUsername());
    }

    /**
     * Purpose: Get state that has password.
     * Input: setPassword null -> string "" -> string "Password!"
     * Expected:
     *          hasPassword() is false -> false -> true.
     */
    @Test
    public void testHasPassword() {
        loginData.setPassword(null);
        assertEquals(false, loginData.hasPassword());
        loginData.setPassword("");
        assertEquals(false, loginData.hasPassword());
        loginData.setPassword("Password!");
        assertEquals(true, loginData.hasPassword());
    }

    /**
     * Purpose: Get state that has saved credentials.
     * Input: setUsername, setPassword, setToken null, null, null ->
     *        setToken string "Token!" ->
     *        setToken, setUsername, setPassword null, string "Username!", "Password!" ->
     *        setToken string "Token!".
     * Expected:
     *          hasNextOperation() is false -> true -> true -> true.
     */
    @Test
    public void testHasSavedCredentials() {
        loginData.setUsername(null);
        loginData.setPassword(null);
        loginData.setToken(null);
        assertEquals(false, loginData.hasSavedCredentials());
        loginData.setToken("Token!");
        assertEquals(true, loginData.hasSavedCredentials());
        loginData.setToken(null);
        loginData.setUsername("Username!");
        loginData.setPassword("Password!");
        assertEquals(true, loginData.hasSavedCredentials());
        loginData.setToken("Token!");
        assertEquals(true, loginData.hasSavedCredentials());
    }

    /**
     * Purpose: Get state that has token.
     * Input: setToken null -> string "" -> string "Token!"
     * Expected:
     *          hasToken() is false -> false -> true.
     */
    @Test
    public void testHasToken() {
        loginData.setToken(null);
        assertEquals(false, loginData.hasToken());
        loginData.setToken("");
        assertEquals(false, loginData.hasToken());
        loginData.setToken("Token!");
        assertEquals(true, loginData.hasToken());
    }

    /**
     * Purpose: Get state that has username.
     * Input: setUsername null -> string "" -> string "Username!"
     * Expected:
     *          hasUsername() is false -> false -> true.
     */
    @Test
    public void testHasUsername() {
        loginData.setUsername(null);
        assertEquals(false, loginData.hasUsername());
        loginData.setUsername("");
        assertEquals(false, loginData.hasUsername());
        loginData.setUsername("Username!");
        assertEquals(true, loginData.hasUsername());
    }

    /**
     * Purpose: Get is saved token.
     * Input: setSavedToken boolean false -> true
     * Expected:
     *          isSavedToken() is false -> true.
     */
    @Test
    public void testIsSavedToken() {
        loginData.setSavedToken(false);
        assertEquals(false, loginData.isSavedToken());
        loginData.setSavedToken(true);
        assertEquals(true, loginData.isSavedToken());
    }

    /**
     * Purpose: Get is valid google login.
     * Input: setToken null -> string "" -> string "Token!"
     * Expected:
     *          isValidGoogleLogin() is false -> false -> true.
     */
    @Test
    public void testIsValidGoogleLogin() {
        loginData.setToken(null);
        assertEquals(false, loginData.isValidGoogleLogin());
        loginData.setToken("");
        assertEquals(false, loginData.isValidGoogleLogin());
        loginData.setToken("Token!");
        assertEquals(true, loginData.isValidGoogleLogin());
    }

    /**
     * Purpose: Get is valid PTC login.
     * Input: setUsername, setPassword null, null ->
     *        setUsername string "Username!" ->
     *        setUsername, setPassword null, "Password!" ->
     *        setUsername string "Username!".
     * Expected:
     *          isValidPtcLogin() is false -> false -> false -> true.
     */
    @Test
    public void testIsValidPtcLogin() {
        loginData.setUsername(null);
        loginData.setPassword(null);
        assertEquals(false, loginData.isValidPtcLogin());
        loginData.setUsername("Username!");
        assertEquals(false, loginData.isValidPtcLogin());
        loginData.setUsername(null);
        loginData.setPassword("Password!");
        assertEquals(false, loginData.isValidPtcLogin());
        loginData.setUsername("Username!");
        assertEquals(true, loginData.isValidPtcLogin());
    }

    /**
     * Purpose: Set login type.
     * Input: setLoginType LoginType.GOOGLE_AUTH
     * Expected:
     *          getLoginType() is LoginType.GOOGLE_AUTH.
     */
    @Test
    public void testSetLoginType() {
        loginData.setLoginType(LoginType.GOOGLE_AUTH);
        assertEquals(LoginType.GOOGLE_AUTH, loginData.getLoginType());
    }

    /**
     * Purpose: Set password.
     * Input: setPassword string "Password!"
     * Expected:
     *          getPassword() is "Password!".
     */
    @Test
    public void testSetPassword() {
        loginData.setPassword("Password!");
        assertEquals("Password!", loginData.getPassword());
    }

    /**
     * Purpose: Set saved token.
     * Input: setSavedToken boolean false -> true
     * Expected:
     *          isSavedToken() is false -> true.
     */
    @Test
    public void testSetSavedToken() {
        loginData.setSavedToken(false);
        assertEquals(false, loginData.isSavedToken());
        loginData.setSavedToken(true);
        assertEquals(true, loginData.isSavedToken());
    }

    /**
     * Purpose: Set token.
     * Input: setToken string "Token!"
     * Expected:
     *          getToken() is "Token!".
     */
    @Test
    public void testSetToken() {
        loginData.setToken("Token!");
        assertEquals("Token!", loginData.getToken());
    }

    /**
     * Purpose: Set username.
     * Input: setUsername string "Username!"
     * Expected:
     *          getUsername() is "Username!".
     */
    @Test
    public void testSetUsername() {
        loginData.setUsername("Username!");
        assertEquals("Username!", loginData.getUsername());
    }

    /**
     * Purpose: to string this class.
     * Input: setUsername, setPassword, setToken setLoginType setSavedToken null, null, null, LoginType.NONE, boolean false ->
     *        setUsername, setPassword, setToken setLoginType setSavedToken string "Username!", string "Password!", string "Token!", LoginType.ALL, boolean true
     * Expected:
     *          toString() is "Username: null | Password: null | Token: null | LoginType: NONE | isSavedToken false" ->
     *                        "Username: Username! | Password: Password! | Token: Token! | LoginType: ALL | isSavedToken true"
     */
    @Test
    public void testToString() {
        loginData.setUsername(null);
        loginData.setPassword(null);
        loginData.setToken(null);
        loginData.setLoginType(LoginType.NONE);
        loginData.setSavedToken(false);
        assertEquals("Username: null | Password: null | Token: null | LoginType: NONE | isSavedToken false", loginData.toString());
        loginData.setUsername("Username!");
        loginData.setPassword("Password!");
        loginData.setToken("Token!");
        loginData.setLoginType(LoginType.ALL);
        loginData.setSavedToken(true);
        assertEquals("Username: Username! | Password: Password! | Token: Token! | LoginType: ALL | isSavedToken true", loginData.toString());
    }
}
