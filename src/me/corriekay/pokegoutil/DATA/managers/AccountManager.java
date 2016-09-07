package me.corriekay.pokegoutil.DATA.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import me.corriekay.pokegoutil.DATA.enums.LoginType;
import me.corriekay.pokegoutil.DATA.models.LoginData;
import me.corriekay.pokegoutil.DATA.models.BPMResult;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import okhttp3.OkHttpClient;

/*this controller does the login/log off, and different account information (aka player data)
 *
 */
public final class AccountManager {

    private static AccountManager S_INSTANCE;

    public static AccountManager getInstance() {
        if (S_INSTANCE == null) {
            S_INSTANCE = new AccountManager();
            // DO any required initialization stuff here
        }
        return S_INSTANCE;
    }

    private ConfigNew config = ConfigNew.getConfig();
    private PokemonGo go = null;

    private AccountManager() {

    }

    private void deleteLoginData(LoginType type) {
        deleteLoginData(type, false);
    }

    private void deleteLoginData(LoginType type, boolean justCleanup) {
        if (!justCleanup)
            config.delete(ConfigKey.LOGIN_SAVE_AUTH);
        switch (type) {
            case BOTH:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                config.delete(ConfigKey.LOGIN_PTC_USERNAME);
                config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
                break;
            case GOOGLE:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                break;
            case PTC:
                config.delete(ConfigKey.LOGIN_PTC_USERNAME);
                config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
                break;
            default:
        }
    }

    public LoginData getLoginData() {
        LoginData loginData = new LoginData(
                config.getString(ConfigKey.LOGIN_PTC_USERNAME),
                config.getString(ConfigKey.LOGIN_PTC_PASSWORD),
                config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN));

        if (loginData.isValidGoogleLogin()) {
            loginData.setSavedToken(true);
        }

        return loginData;

    }

    public PlayerProfile getPlayerProfile() {
        return go != null ? go.getPlayerProfile() : null;
    }

    private void initOtherControllers() {
        InventoryManager.initialize(go);
        PokemonBagManager.initialize(go);
        ProfileManager.initialize(go);
    }

    public BPMResult login(LoginData loginData) throws Exception {
        switch (loginData.getLoginType()) {
            case GOOGLE:
                if (loginData.isValidGoogleLogin()) {
                    return logOnGoogleAuth(loginData);
                }
                break;
            case PTC:
                if (loginData.isValidPTCLogin()) {
                    return logOnPTC(loginData);
                }
                break;
            default:
        }
        return new BPMResult("Invalid Login Type");
    }

    private BPMResult logOnGoogleAuth(LoginData loginData) {
        OkHttpClient http;
        CredentialProvider cp;
        http = new OkHttpClient();

        String authCode = loginData.getToken();
        boolean saveAuth = config.getBool(ConfigKey.LOGIN_SAVE_AUTH);

        boolean shouldRefresh = false;
        if (loginData.isSavedToken() && saveAuth) {
            shouldRefresh = true;
        }

        try {
            GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
            if (shouldRefresh) {
                provider.refreshToken(authCode);
            } else {
                provider.login(authCode);
            }

            if (provider.isTokenIdExpired()) {
                throw new LoginFailedException();
            }

            cp = provider;
            if (saveAuth && !shouldRefresh) {
                config.setString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, provider.getRefreshToken());
            } else {
                deleteLoginData(LoginType.GOOGLE);
            }
        } catch (Exception e) {
            deleteLoginData(LoginType.GOOGLE);
            return new BPMResult(e.getMessage());
        }

        try {
            prepareLogin(cp, http);
            return new BPMResult();
        } catch (LoginFailedException | RemoteServerException e) {
            deleteLoginData(LoginType.BOTH);
            return new BPMResult(e.getMessage());
        }
    }

    private BPMResult logOnPTC(LoginData loginData) throws Exception {
        OkHttpClient http;
        CredentialProvider cp;
        http = new OkHttpClient();

        String username = loginData.getUsername();
        String password = loginData.getPassword();
        boolean saveAuth = config.getBool(ConfigKey.LOGIN_SAVE_AUTH);

        try {
            cp = new PtcCredentialProvider(http, username, password);
            config.setString(ConfigKey.LOGIN_PTC_USERNAME, username);
            if (saveAuth) {
                config.setString(ConfigKey.LOGIN_PTC_PASSWORD, password);
            } else {
                deleteLoginData(LoginType.PTC);
            }
        } catch (Exception e) {
            deleteLoginData(LoginType.PTC);
            return new BPMResult(e.getMessage());
        }

        try {
            prepareLogin(cp, http);
            return new BPMResult();
        } catch (LoginFailedException | RemoteServerException e) {
            deleteLoginData(LoginType.BOTH);
            return new BPMResult(e.getMessage());
        }

    }

    private void prepareLogin(CredentialProvider cp, OkHttpClient http)
            throws LoginFailedException, RemoteServerException {
        go = new PokemonGo(http);
        go.login(cp);
        initOtherControllers();
    }

    public void setSaveLogin(boolean save) {
        config.setBool(ConfigKey.LOGIN_SAVE_AUTH, save);
    }
}
