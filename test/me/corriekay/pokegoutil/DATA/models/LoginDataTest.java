package me.corriekay.pokegoutil.DATA.models;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import me.corriekay.pokegoutil.DATA.enums.LoginType;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


@RunWith(value = Parameterized.class)
public class LoginDataTest {

    @Parameters(name="")
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
    
    private LoginData loginData;
    private LoginType expectedLoginType;
    private boolean expectedPTCIsValid;

    private boolean expectedGoogleIsValid;

    public LoginDataTest(LoginData loginData, LoginType expectedLoginType,
            boolean expectedPTCIsValid, boolean expectedGoogleIsValid) {
        this.loginData = loginData;
        this.expectedLoginType = expectedLoginType;
        this.expectedPTCIsValid = expectedPTCIsValid;
        this.expectedGoogleIsValid = expectedGoogleIsValid;
    }
    
    @Test
    public void TestGoogleIsValid(){
        assertThat(loginData.isValidGoogleLogin(), is(expectedGoogleIsValid));
    }
    @Test
    public void TestLoginType(){
        assertThat(loginData.getLoginType(), is(expectedLoginType));
    }
    @Test
    public void TestPTCIsValid(){
        assertThat(loginData.isValidPTCLogin(), is(expectedPTCIsValid));
    }
    
    

}
