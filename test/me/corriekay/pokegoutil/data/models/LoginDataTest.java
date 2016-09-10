package me.corriekay.pokegoutil.data.models;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import me.corriekay.pokegoutil.data.enums.LoginType;

/**
 * Test for LoginData model.
 */
@RunWith(value = Parameterized.class)
public class LoginDataTest {

    private final LoginData loginData;
    private final LoginType expectedLoginType;
    private final boolean expectedPtcIsValid;
    private final boolean expectedGoogleIsValid;

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        // LoginData, Expected LoginType, Expected PTC isValid,Expected Google isValid
        final List<Object[]> list = new ArrayList<Object[]>();

        list.add(new Object[]{new LoginData("User", "Pass"), LoginType.PTC, true, false});
        list.add(new Object[]{new LoginData("User", ""), LoginType.PTC, false, false});
        list.add(new Object[]{new LoginData("", "Pass"), LoginType.PTC, false, false});
        list.add(new Object[]{new LoginData("", ""), LoginType.PTC, false, false});
        list.add(new Object[]{new LoginData("User", null), LoginType.PTC, false, false});
        list.add(new Object[]{new LoginData(null, "Pass"), LoginType.PTC, false, false});
        list.add(new Object[]{new LoginData(null, null), LoginType.PTC, false, false});

        list.add(new Object[]{new LoginData("Token"), LoginType.GOOGLE, false, true});
        list.add(new Object[]{new LoginData(null), LoginType.GOOGLE, false, false});
        list.add(new Object[]{new LoginData(""), LoginType.GOOGLE, false, false});

        list.add(new Object[]{new LoginData("User", "Pass", "Token"), LoginType.BOTH, true, true});
        list.add(new Object[]{new LoginData("", "", ""), LoginType.BOTH, false, false});
        list.add(new Object[]{new LoginData(null, null, null), LoginType.BOTH, false, false});
        list.add(new Object[]{new LoginData("User", "Pass", ""), LoginType.BOTH, true, false});
        list.add(new Object[]{new LoginData("User", "Pass", null), LoginType.BOTH, true, false});
        list.add(new Object[]{new LoginData("", "", "token"), LoginType.BOTH, false, true});
        list.add(new Object[]{new LoginData(null, null, "token"), LoginType.BOTH, false, true});

        return list;
    }

    /**
     * Instantiate a LoginDataTest using the parameters from data().
     *
     * @param loginData login data to test
     * @param expectedLoginType login type expected
     * @param expectedPtcIsValid expected ptc is valid
     * @param expectedGoogleIsValid expected google is valid
     */
    public LoginDataTest(final LoginData loginData, final LoginType expectedLoginType, final boolean expectedPtcIsValid, final boolean expectedGoogleIsValid) {
        this.loginData = loginData;
        this.expectedLoginType = expectedLoginType;
        this.expectedPtcIsValid = expectedPtcIsValid;
        this.expectedGoogleIsValid = expectedGoogleIsValid;
    }

    @Test
    public void testGoogleIsValid() {
        assertThat("Validate google login", loginData.isValidGoogleLogin(), is(expectedGoogleIsValid));
    }

    @Test
    public void testLoginType() {
        assertThat("Correct login type", loginData.getLoginType(), is(expectedLoginType));
    }

    @Test
    public void testPtcIsValid() {
        assertThat("Valid ptc login", loginData.isValidPtcLogin(), is(expectedPtcIsValid));
    }
}
