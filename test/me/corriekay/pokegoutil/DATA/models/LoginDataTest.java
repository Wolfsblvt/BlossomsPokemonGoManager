package me.corriekay.pokegoutil.DATA.models;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import me.corriekay.pokegoutil.DATA.enums.LoginType;

/**
 * Test for LoginData model.
 */
@RunWith(value = Parameterized.class)
public class LoginDataTest {

    private final LoginData loginData;
    private final LoginType expectedLoginType;
    private final boolean expectedPtcIsValid;
    private final boolean expectedGoogleIsValid;

    @Parameters(name="{index}: {0}")
    public static Collection<Object[]> data() {
        // LoginData, Expected LoginType, Expected PTC isValid,Expected Google isValid
        return Arrays.asList(new Object[][] {
            { new LoginData("User", "Pass"), LoginType.PTC, true, false },
            { new LoginData("User", ""), LoginType.PTC, false, false },
            { new LoginData("", "Pass"), LoginType.PTC, false, false },
            { new LoginData("", ""), LoginType.PTC, false, false },
            { new LoginData("User", null), LoginType.PTC, false, false },
            { new LoginData(null, "Pass"), LoginType.PTC, false, false },
            { new LoginData(null, null), LoginType.PTC, false, false },

            { new LoginData("Token"), LoginType.GOOGLE, false, true },
            { new LoginData(null), LoginType.GOOGLE, false, false },
            { new LoginData(""), LoginType.GOOGLE, false, false },

            { new LoginData("User", "Pass", "Token"), LoginType.BOTH, true, true },
            { new LoginData("", "", ""), LoginType.BOTH, false, false },
            { new LoginData(null, null, null), LoginType.BOTH, false, false },
            { new LoginData("User", "Pass", ""), LoginType.BOTH, true, false },
            { new LoginData("User", "Pass", null), LoginType.BOTH, true, false },
            { new LoginData("", "", "token"), LoginType.BOTH, false, true },
            { new LoginData(null, null, "token"), LoginType.BOTH, false, true },
        });
    }

    /**
     * Instantiate a LoginDataTest using the parameters from data().
     *
     * @param loginData login data to test
     * @param expectedLoginType login type expected
     * @param expectedPtcIsValid expected ptc is valid
     * @param expectedGoogleIsValid expected google is valid
     */
    public LoginDataTest(final LoginData loginData, final LoginType expectedLoginType,
            final boolean expectedPtcIsValid, final boolean expectedGoogleIsValid) {
        this.loginData = loginData;
        this.expectedLoginType = expectedLoginType;
        this.expectedPtcIsValid = expectedPtcIsValid;
        this.expectedGoogleIsValid = expectedGoogleIsValid;
    }

    @Test
    public void testGoogleIsValid(){
        assertThat("Validate google login", loginData.isValidGoogleLogin(), is(expectedGoogleIsValid));
    }

    @Test
    public void testLoginType(){
        assertThat("Correct login type", loginData.getLoginType(), is(expectedLoginType));
    }

    @Test
    public void testPtcIsValid(){
        assertThat("Valid ptc login", loginData.isValidPtcLogin(), is(expectedPtcIsValid));
    }
}
