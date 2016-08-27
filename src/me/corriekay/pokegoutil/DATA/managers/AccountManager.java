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
import me.corriekay.pokegoutil.DATA.models.LoginResult;
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

    public boolean checkForSavedCredentials() {
        LoginType savedLogin = checkSavedConfig();
        // TODO: Implement choose if you want to login with that saved data
        if (savedLogin == LoginType.NONE)
            return false;
        else
            return true;
    }

    public LoginType checkSavedConfig() {
        if (!config.getBool(ConfigKey.LOGIN_SAVE_AUTH)) {
            return LoginType.NONE;
        } else {
            boolean google = getLoginData(LoginType.GOOGLE).isValidGoogleLogin();
            boolean PTC = getLoginData(LoginType.PTC).isValidPTCLogin();
            if (google && PTC) {
                return LoginType.BOTH;
            } else if (google) {
                return LoginType.GOOGLE;
            } else if (PTC) {
                return LoginType.PTC;
            }
            return LoginType.NONE;
        }
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

    public LoginData getLoginData(LoginType type) {
        LoginData loginData = new LoginData();

        switch (type) {
            case GOOGLE:
                loginData.setToken(config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN));
                loginData.setSavedToken(true);
                break;
            case PTC:
                loginData.setUsername(config.getString(ConfigKey.LOGIN_PTC_USERNAME));
                loginData.setPassword(config.getString(ConfigKey.LOGIN_PTC_PASSWORD));
                break;
            case BOTH:
                loginData.setToken(config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN));
                loginData.setSavedToken(true);
                loginData.setUsername(config.getString(ConfigKey.LOGIN_PTC_USERNAME));
                loginData.setPassword(config.getString(ConfigKey.LOGIN_PTC_PASSWORD));
                break;
            default:
        }
        return loginData;

    }

    public PlayerProfile getPlayerProfile() {
        return S_INSTANCE.go != null ? S_INSTANCE.go.getPlayerProfile() : null;
    }

    private void initOtherControllers(PokemonGo go) {
        InventoryManager.initialize(go);
        PokemonBagManager.initialize(go);
        ProfileManager.initialize(go);
    }

    public LoginResult login(LoginData loginData) throws Exception {
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
        return new LoginResult("Invalid Login Type");
    }

    private LoginResult logOnGoogleAuth(LoginData loginData) {
        OkHttpClient http;
        CredentialProvider cp;
        http = new OkHttpClient();

        String authCode = loginData.getToken();
        boolean saveAuth = config.getBool(ConfigKey.LOGIN_SAVE_AUTH);

        boolean refresh = false;
        if (loginData.isSavedToken() && saveAuth) {
            refresh = true;
        }

        try {
            GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
            if (refresh) {
                provider.refreshToken(authCode);
            } else {
                provider.login(authCode);
            }

            if (provider.isTokenIdExpired()) {
                throw new LoginFailedException();
            }

            cp = provider;
            if (saveAuth && !refresh) {
                config.setString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, provider.getRefreshToken());
            } else {
                deleteLoginData(LoginType.GOOGLE);
            }
        } catch (Exception e) {
            deleteLoginData(LoginType.GOOGLE);
            return new LoginResult(e.getMessage());
        }

        try {
            prepareLogin(cp, http);
            return new LoginResult();
        } catch (LoginFailedException | RemoteServerException e) {
            deleteLoginData(LoginType.BOTH);
            return new LoginResult(e.getMessage());
        }
    }

    private LoginResult logOnPTC(LoginData loginData) throws Exception {
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
            return new LoginResult(e.getMessage());
        }

        try {
            prepareLogin(cp, http);
            return new LoginResult();
        } catch (LoginFailedException | RemoteServerException e) {
            deleteLoginData(LoginType.BOTH);
            return new LoginResult(e.getMessage());
        }

    }

    private void prepareLogin(CredentialProvider cp, OkHttpClient http)
            throws LoginFailedException, RemoteServerException {
        go = new PokemonGo(cp, http);
        S_INSTANCE.go = go;
        initOtherControllers(go);
    }

    public void setSaveLogin(boolean save) {
        config.setBool(ConfigKey.LOGIN_SAVE_AUTH, save);
    }
}
