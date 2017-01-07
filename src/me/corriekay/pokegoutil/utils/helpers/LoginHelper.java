package me.corriekay.pokegoutil.utils.helpers;

import java.util.function.Consumer;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.device.DeviceInfo;
import com.pokegoapi.api.listener.LoginListener;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.hash.legacy.LegacyHashProvider;
import com.pokegoapi.util.hash.pokehash.PokeHashProvider;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.CustomDeviceInfo;
import me.corriekay.pokegoutil.utils.SolveCaptcha;

public class LoginHelper {

    public static void login(final PokemonGo go, final CredentialProvider credentialProvider, Consumer<PokemonGo> loginFunction) throws LoginFailedException, CaptchaActiveException, RemoteServerException
    {
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
        if (pokeHashKey != null) {
            PokeHashProvider.HASH_ENDPOINT = "http://pokehash.buddyauth.com/api/v121_2/hash";
            go.login(credentialProvider, new PokeHashProvider(pokeHashKey));
        } else {
            go.login(credentialProvider, new LegacyHashProvider());
        }
    }
}
