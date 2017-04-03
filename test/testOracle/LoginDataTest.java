package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.corriekay.pokegoutil.data.enums.LoginType;
import me.corriekay.pokegoutil.data.models.LoginData;
/**
 * TODO. Comment required.
 *
 */
public class LoginDataTest {
    private LoginData loginData;

    @Before
    public void setUp() throws Exception {
        loginData = new LoginData();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConstructor() {
        loginData = null;
        loginData = new LoginData();
        assertEquals(LoginType.NONE, loginData.getLoginType());
    }

    @Test
    public void testConstructorWithOneString() {
        loginData = null;
        loginData = new LoginData("Token!");
        assertEquals(LoginType.GOOGLE_AUTH, loginData.getLoginType());
    }

    @Test
    public void testConstructorWithTwoStrings() {
        loginData = null;
        loginData = new LoginData("Username!", "Password!");
        assertEquals(LoginType.PTC, loginData.getLoginType());
    }

    @Test
    public void testConstructorWithThreeStrings() {
        loginData = null;
        loginData = new LoginData("Username!", "Password!", "Token!");
        assertEquals(LoginType.ALL, loginData.getLoginType());
    }

    @Test
    public void testGetLoginType() {
        loginData.setLoginType(LoginType.GOOGLE_AUTH);
        assertEquals(LoginType.GOOGLE_AUTH, loginData.getLoginType());
    }

    @Test
    public void testGetPassword() {
        loginData.setPassword("Password!");
        assertEquals("Password!", loginData.getPassword());
    }

    @Test
    public void testGetToken() {
        loginData.setToken("Token!");
        assertEquals("Token!", loginData.getToken());
    }

    @Test
    public void testGetUsername() {
        loginData.setUsername("Username!");
        assertEquals("Username!", loginData.getUsername());
    }

    @Test
    public void testHasPassword() {
        loginData.setPassword(null);
        assertEquals(false, loginData.hasPassword());
        loginData.setPassword("");
        assertEquals(false, loginData.hasPassword());
        loginData.setPassword("Password!");
        assertEquals(true, loginData.hasPassword());
    }

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

    @Test
    public void testHasToken() {
        loginData.setToken(null);
        assertEquals(false, loginData.hasToken());
        loginData.setToken("");
        assertEquals(false, loginData.hasToken());
        loginData.setToken("Token!");
        assertEquals(true, loginData.hasToken());
    }

    @Test
    public void testHasUsername() {
        loginData.setUsername(null);
        assertEquals(false, loginData.hasUsername());
        loginData.setUsername("");
        assertEquals(false, loginData.hasUsername());
        loginData.setUsername("Username!");
        assertEquals(true, loginData.hasUsername());
    }

    @Test
    public void testIsSavedToken() {
        loginData.setSavedToken(false);
        assertEquals(false, loginData.isSavedToken());
        loginData.setSavedToken(true);
        assertEquals(true, loginData.isSavedToken());
    }

    @Test
    public void testIsValidGoogleLogin() {
        loginData.setToken(null);
        assertEquals(false, loginData.isValidGoogleLogin());
        loginData.setToken("");
        assertEquals(false, loginData.isValidGoogleLogin());
        loginData.setToken("Token!");
        assertEquals(true, loginData.isValidGoogleLogin());
    }

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

    @Test
    public void testSetLoginType() {
        loginData.setLoginType(LoginType.GOOGLE_AUTH);
        assertEquals(LoginType.GOOGLE_AUTH, loginData.getLoginType());
    }

    @Test
    public void testSetPassword() {
        loginData.setPassword("Password!");
        assertEquals("Password!", loginData.getPassword());
    }

    @Test
    public void testSetSavedToken() {
        loginData.setSavedToken(false);
        assertEquals(false, loginData.isSavedToken());
        loginData.setSavedToken(true);
        assertEquals(true, loginData.isSavedToken());
    }

    @Test
    public void testSetToken() {
        loginData.setToken("Token!");
        assertEquals("Token!", loginData.getToken());
    }

    @Test
    public void testSetUsername() {
        loginData.setUsername("Username!");
        assertEquals("Username!", loginData.getUsername());
    }

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
