package me.corriekay.pokegoutil.DATA.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import me.corriekay.pokegoutil.utils.Config;
import okhttp3.OkHttpClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*this controller does the login/log off, and different account information (aka player data)
 *
 */
public final class AccountManager {

    private static final AccountManager S_INSTANCE = new AccountManager();
    private static boolean sIsInit = false;
    private static Config config = Config.getConfig();

    private PokemonGo go = null;
    private boolean loggedIn = false;
    private String username;
    private String password;
    private String token;

    private AccountManager() {

    }

    public static void initialize() {
        if (sIsInit)
            return;
        sIsInit = true;
    }

    public static void login(List<Pair> loginData, LoginType loginType) {
        loginData.forEach(pair -> {
            switch (pair.getKey().toString()) {
                case "username":
                    S_INSTANCE.username = pair.getValue().toString();
                case "password":
                    S_INSTANCE.password = pair.getValue().toString();
                case "token":
                    S_INSTANCE.token = pair.getValue().toString();
            }
        });
        switch (loginType) {
            case GOOGLE:
                logOnGoogleAuth(S_INSTANCE.token);
                break;
            case PTC:
                logOnPTC(S_INSTANCE.username, S_INSTANCE.password);
        }
    }

    private static void logOnPTC(String username, String password) {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        OkHttpClient http;
        CredentialProvider cp;
        PokemonGo go;
        http = new OkHttpClient();

        try {
            cp = new PtcCredentialProvider(http, username, password);
            config.setString("login.PTCUsername", username);
            if (config.getBool("login.SaveAuth", false)) {
                config.setString("login.PTCPassword", password);
            } else {
                deleteLoginData(LoginType.PTC);
            }
        } catch (Exception e) {
            alertFailedLogin(e.getMessage());
            deleteLoginData(LoginType.PTC);
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

    private static void logOnGoogleAuth(String authCode) {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        OkHttpClient http;
        CredentialProvider cp;
        PokemonGo go;
        http = new OkHttpClient();

        boolean refresh = false;
        if (authCode.equals("Using Previous Token") && config.getBool("login.SaveAuth", false)) {
            // Get credentials
            authCode = config.getString("login.GoogleAuthToken", null);
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
            if (config.getBool("login.SaveAuth", false)) {
                if (!refresh)
                    config.setString("login.GoogleAuthToken", provider.getRefreshToken());
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

    private static void alertFailedLogin(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Login");
        alert.setHeaderText("Unfortunately, your login has failed");
        alert.setContentText(message != null ? message : "" + "\nPress OK to try again.");
        alert.showAndWait();
    }

    public static List<Pair> getLoginData(LoginType type) {
        switch (type) {
            case GOOGLE:
                String token = config.getString("login.GoogleAuthToken", null);
                return (token != null) ? Collections.singletonList(
                        new Pair<>("token", token)) : null;
            case PTC:
                String username = config.getString("login.PTCUsername", null);
                String password = config.getString("login.PTCPassword", null);
                return (username != null && password != null) ? Arrays.asList(
                        new Pair<>("username", username), new Pair<>("password", password)) : null;
            case BOTH:
                String token2 = config.getString("login.GoogleAuthToken", null);
                String username2 = config.getString("login.PTCUsername", null);
                String password2 = config.getString("login.PTCPassword", null);
                return (username2 != null && password2 != null && token2 != null) ? Arrays.asList(
                        new Pair<>("username", username2), new Pair<>("password", password2), new Pair<>("token", token2)) : null;
            default:
                return null;
        }
    }

    private static void deleteLoginData(LoginType type) {
        deleteLoginData(type, false);
    }

    private static void deleteLoginData(LoginType type, boolean justCleanup) {
        if (!justCleanup) config.delete("login.SaveAuth");
        switch (type) {
            case BOTH:
                config.delete("login.GoogleAuthToken");
                config.delete("login.PTCUsername");
                config.delete("login.PTCPassword");
                break;
            case GOOGLE:
                config.delete("login.GoogleAuthToken");
                break;
            case PTC:
                config.delete("login.PTCUsername");
                config.delete("login.PTCPassword");
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
        if (!config.getBool("login.SaveAuth", false)) {
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
        config.setBool("login.SaveAuth", save);
    }

    public enum LoginType {
        GOOGLE,
        PTC,
        BOTH,
        NONE
    }
}
