package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.corriekay.pokegoutil.data.enums.LoginType;
import me.corriekay.pokegoutil.data.models.LoginData;

public class LoginDataTest {
    LoginData loginData;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoginData() {
        loginData = new LoginData();
        assertEquals(LoginType.NONE, loginData.getLoginType());
    }

    //TODO.
    @Test
    public void testLoginDataString() {
        fail("Not yet implemented");
    }

    @Test
    public void testLoginDataStringString() {
        fail("Not yet implemented");
    }

    @Test
    public void testLoginDataStringStringString() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetLoginType() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPassword() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetToken() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetUsername() {
        fail("Not yet implemented");
    }

    @Test
    public void testHasPassword() {
        fail("Not yet implemented");
    }

    @Test
    public void testHasSavedCredentials() {
        fail("Not yet implemented");
    }

    @Test
    public void testHasToken() {
        fail("Not yet implemented");
    }

    @Test
    public void testHasUsername() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsSavedToken() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsValidGoogleLogin() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsValidPtcLogin() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetLoginType() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetPassword() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetSavedToken() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetToken() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetUsername() {
        fail("Not yet implemented");
    }

    @Test
    public void testToString() {
        fail("Not yet implemented");
    }

}
