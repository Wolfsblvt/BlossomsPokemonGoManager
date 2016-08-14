package me.corriekay.pokegoutil.DATA.controllers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.helpers.Browser;
import me.corriekay.pokegoutil.windows.PokemonGoMainWindow;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
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

    protected PokemonGo go = null;
    private boolean loggedIn = false;

    private AccountManager() {

    }

    public static void initialize() {
        if (sIsInit)
            return;
        sIsInit = true;
    }

    public static void logOnPTC(String username, String password) {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        OkHttpClient http;
        CredentialProvider cp;
        PokemonGo go = null;
        while (!S_INSTANCE.loggedIn) {
            //
            go = null;
            cp = null;
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
                continue;
            }

            if (cp != null)
                try {
                    go = new PokemonGo(cp, http);
                } catch (LoginFailedException | RemoteServerException e) {
                    alertFailedLogin(e.getMessage());
                    deleteLoginData(LoginType.BOTH);
                    continue;
                }
            else
                throw new IllegalStateException();
            S_INSTANCE.loggedIn = true;
        }
        S_INSTANCE.go = go;
        initOtherControllers(go);
    }

    public static void logOnGoogleAuth(String authCode) {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        OkHttpClient http;
        CredentialProvider cp;
        PokemonGo go = null;
        while (!S_INSTANCE.loggedIn) {
            //
            go = null;
            cp = null;
            http = new OkHttpClient();

            boolean refresh = false;
            if (authCode.equals("Using Previous Token")) {
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
                continue;
            }

            if (cp != null)
                try {
                    go = new PokemonGo(cp, http);
                } catch (LoginFailedException | RemoteServerException e) {
                    alertFailedLogin(e.getMessage());
                    deleteLoginData(LoginType.BOTH);
                    continue;
                }
            else
                throw new IllegalStateException();
            S_INSTANCE.loggedIn = true;
        }
        S_INSTANCE.go = go;
        initOtherControllers(go);
    }

    private static void initOtherControllers(PokemonGo go) {
        InventoryController.initialize(go);
        PokemonBagController.initialize(go);
    }

    private static void alertFailedLogin(String message) {
        // TODO UI STUFF
        //JOptionPane.showMessageDialog(null, "Unfortunately, your login has failed. Reason: " + message + "\nPress OK to try again.", "Login Failed", JOptionPane.PLAIN_MESSAGE);
    }

    public static List<String> getLoginData(LoginType type) {
        switch (type) {
            case GOOGLE:
                String token = config.getString("login.GoogleAuthToken", null);
                return (token != null) ? Collections.singletonList(token) : null;
            case PTC:
                String username = config.getString("login.PTCUsername", null);
                String password = config.getString("login.PTCPassword", null);
                return (username != null && password != null) ? Arrays.asList(username, password) : null;
            case BOTH:
                String token2 = config.getString("login.GoogleAuthToken", null);
                String username2 = config.getString("login.PTCUsername", null);
                String password2 = config.getString("login.PTCPassword", null);
                return (username2 != null && password2 != null && token2 != null) ? Arrays.asList(username2, password2, token2) : null;
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
        if (savedLogin == LoginType.NONE) {
            return false;
        } else {
            // TODO: Implement choose if you want to login with that saved data
            return true;
        }
    }

    public static LoginType checkSavedConfig() {
        if (!config.getBool("login.SaveAuth", false)) {
            return LoginType.NONE;
        } else {
            if (getLoginData(LoginType.GOOGLE) != null && getLoginData(LoginType.PTC) != null) return LoginType.BOTH;
            if (getLoginData(LoginType.GOOGLE) != null) return LoginType.GOOGLE;
            if (getLoginData(LoginType.PTC) != null) return LoginType.PTC;
            return LoginType.NONE;
        }
    }

    public static void logOff() {
        S_INSTANCE.loggedIn = false;
    }

    public static PlayerProfile getPlayerProfile() {
        return S_INSTANCE.go != null ? S_INSTANCE.go.getPlayerProfile() : null;
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
