package me.corriekay.pokegoutil.utils.helpers;

import java.util.function.Consumer;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.device.DeviceInfo;
import com.pokegoapi.api.listener.LoginListener;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.exceptions.hash.HashException;
import com.pokegoapi.util.hash.legacy.LegacyHashProvider;
import com.pokegoapi.util.hash.pokehash.PokeHashKey;
import com.pokegoapi.util.hash.pokehash.PokeHashProvider;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.CustomDeviceInfo;
import me.corriekay.pokegoutil.utils.SolveCaptcha;

/**
 * Class to concentrate login method to be used in both interfaces: Swing and JavaFX.
 */
public final class LoginHelper {

    /** Prevent initializing this class. */
    private LoginHelper() {
    }
    
    /**
     * Main login method.
     * @param go PokemonGo api
     * @param credentialProvider CredentialProvider (Google or PTC)
     * @param loginFunction Consumer function that will receive API after successful login
     * @throws LoginFailedException if Login fail
     * @throws CaptchaActiveException if a Captcha is active
     * @throws HashException 
     */
    public static void login(final PokemonGo go, final CredentialProvider credentialProvider, final Consumer<PokemonGo> loginFunction) 
            throws LoginFailedException, CaptchaActiveException, HashException {
        //Add listener to listen for the captcha URL
        go.addListener(new LoginListener() {
            @Override
            public void onLogin(final PokemonGo api) {
                loginFunction.accept(api);
            }

            @Override
            public void onChallenge(final PokemonGo api, final String challengeURL) {
                System.out.println("Captcha received! URL: " + challengeURL);
                final SolveCaptcha captcha = new SolveCaptcha(api, challengeURL);
                captcha.setVisible(true);
            }
        });
        if (ConfigNew.getConfig().getBool(ConfigKey.DEVICE_INFO_USE_CUSTOM)) {
            go.setDeviceInfo(new DeviceInfo(new CustomDeviceInfo()));
        }
        final String pokeHashKey = ConfigNew.getConfig().getString(ConfigKey.LOGIN_POKEHASHKEY);
        try {
            if (pokeHashKey != null) {
                final PokeHashProvider pokeHashProvider = new PokeHashProvider(PokeHashKey.from(pokeHashKey), true);
                pokeHashProvider.setEndpoint("http://pokehash.buddyauth.com/api/v127_2/hash");
                go.login(credentialProvider, pokeHashProvider);
            } else {
                go.login(credentialProvider, new LegacyHashProvider());
            }
        } catch (RemoteServerException e) {
            e.printStackTrace();
        }
    }
}
