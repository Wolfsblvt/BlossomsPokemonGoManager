package me.corriekay.pokegoutil.DATA.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import javafx.scene.control.Alert;
import me.corriekay.pokegoutil.DATA.enums.LoginType;
import me.corriekay.pokegoutil.DATA.models.LoginData;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import okhttp3.OkHttpClient;

/*this controller does the login/log off, and different account information (aka player data)
 *
 */
public final class AccountManager {

    private static final AccountManager S_INSTANCE = new AccountManager();
    private static boolean sIsInit = false;
    private static ConfigNew config = ConfigNew.getConfig();

    private PokemonGo go = null;
    private boolean loggedIn = false;

    private AccountManager() {

    }

    public static void initialize() {
        if (sIsInit)
            return;
        sIsInit = true;
    }

    public static void login(LoginData loginData) throws Exception {
              
        switch (loginData.getLoginType()) {
            case GOOGLE:
                logOnGoogleAuth(loginData.getToken());
                break;
            case PTC:
                logOnPTC(loginData.getUsername(), loginData.getPassword());
                break;
            default:
        }
    }

    private static void logOnPTC(String username, String password) throws Exception {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        OkHttpClient http;
        CredentialProvider cp;
        PokemonGo go;
        http = new OkHttpClient();

        try {
            cp = new PtcCredentialProvider(http, username, password);
            config.setString(ConfigKey.LOGIN_PTC_USERNAME, username);
            if (config.getBool(ConfigKey.LOGIN_SAVE_AUTH)) {
                config.setString(ConfigKey.LOGIN_PTC_PASSWORD, password);
            } else {
                deleteLoginData(LoginType.PTC);
            }
        } catch (Exception e) {
            alertFailedLogin(e.getMessage());
            deleteLoginData(LoginType.PTC);
            return;
        }

        if (cp != null) {
            try {
                go = new PokemonGo(cp, http);
            } catch (LoginFailedException | RemoteServerException e) {
                alertFailedLogin(e.getMessage());
                deleteLoginData(LoginType.BOTH);
                return;
            }
        }
        else
            throw new IllegalStateException();

        S_INSTANCE.go = go;
        initOtherControllers(go);
        S_INSTANCE.loggedIn = true;
    }

    private static void logOnGoogleAuth(String authCode) {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        OkHttpClient http;
        CredentialProvider cp;
        PokemonGo go;
        http = new OkHttpClient();

        boolean refresh = false;
        if (authCode.equals("Using Previous Token") && config.getBool(ConfigKey.LOGIN_SAVE_AUTH)) {
            // Get credentials
            authCode = config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
            refresh = true;
        }

        try {
            GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
            if (refresh)
                provider.refreshToken(authCode);
            else
                provider.login(authCode);
            if (provider.isTokenIdExpired())
                throw new LoginFailedException();

            cp = provider;
            if (config.getBool(ConfigKey.LOGIN_SAVE_AUTH)) {
                if (!refresh)
                    config.setString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, provider.getRefreshToken());
            } else {
                deleteLoginData(LoginType.GOOGLE);
            }
        } catch (Exception e) {
            alertFailedLogin(e.getMessage());
            deleteLoginData(LoginType.GOOGLE);
            return;
        }

        if (cp != null)
            try {
                go = new PokemonGo(cp, http);
            } catch (LoginFailedException | RemoteServerException e) {
                alertFailedLogin(e.getMessage());
                deleteLoginData(LoginType.BOTH);
                return;
            }
        else
            throw new IllegalStateException();

        S_INSTANCE.go = go;
        initOtherControllers(go);
        S_INSTANCE.loggedIn = true;
    }

    private static void initOtherControllers(PokemonGo go) {
        InventoryManager.initialize(go);
        PokemonBagManager.initialize(go);
        ProfileManager.initialize(go);
    }

    public static void alertFailedLogin(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Login");
        alert.setHeaderText("Unfortunately, your login has failed");
        alert.setContentText(message != null ? message : "" + "\nPress OK to try again.");
        alert.showAndWait();
    }

    public static LoginData getLoginData(LoginType type) {
        LoginData loginData = new LoginData();
        
        switch (type) {
            case GOOGLE:
                 loginData.setToken(config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN));
                 break;
            case PTC:
                loginData.setUsername(config.getString(ConfigKey.LOGIN_PTC_USERNAME));
                loginData.setPassword(config.getString(ConfigKey.LOGIN_PTC_PASSWORD));
                break;
            case BOTH:
                loginData.setToken(config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN));
                loginData.setUsername(config.getString(ConfigKey.LOGIN_PTC_USERNAME));
                loginData.setPassword(config.getString(ConfigKey.LOGIN_PTC_PASSWORD));
                break;
            default:
        }        
        return loginData;
    }

    private static void deleteLoginData(LoginType type) {
        deleteLoginData(type, false);
    }

    private static void deleteLoginData(LoginType type, boolean justCleanup) {
        if (!justCleanup) config.delete(ConfigKey.LOGIN_SAVE_AUTH);
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

    public static boolean checkForSavedCredentials() {
        LoginType savedLogin = checkSavedConfig();
        // TODO: Implement choose if you want to login with that saved data
        if (savedLogin == LoginType.NONE) return false;
        else return true;
    }

    public static LoginType checkSavedConfig() {
        if (!config.getBool(ConfigKey.LOGIN_SAVE_AUTH)) {
            return LoginType.NONE;
        } else {
            boolean google = getLoginData(LoginType.GOOGLE) != null;
            boolean PTC = getLoginData(LoginType.PTC) != null;
            if (google && PTC) return LoginType.BOTH;
            if (google) return LoginType.GOOGLE;
            if (PTC) return LoginType.PTC;
            return LoginType.NONE;
        }
    }

    public static PlayerProfile getPlayerProfile() {
        return S_INSTANCE.go != null ? S_INSTANCE.go.getPlayerProfile() : null;
    }

    public static void logOff() {
        S_INSTANCE.loggedIn = false;
    }

    public static boolean isLoggedIn() {
        return S_INSTANCE.loggedIn;
    }

    public static void setSaveLogin(boolean save){
        config.setBool(ConfigKey.LOGIN_SAVE_AUTH, save);
    }
}
