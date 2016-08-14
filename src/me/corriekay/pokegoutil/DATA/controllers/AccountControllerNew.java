package me.corriekay.pokegoutil.DATA.controllers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import me.corriekay.pokegoutil.utils.helpers.Browser;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
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
public final class AccountControllerNew {

    private static final AccountControllerNew S_INSTANCE = new AccountControllerNew();
    private static boolean sIsInit = false;

    private boolean logged = false;

    protected PokemonGo go = null;
    protected OkHttpClient http;
    protected CredentialProvider cp;

    private static ConfigNew config = ConfigNew.getConfig();

    private AccountControllerNew() {

    }

    public static AccountControllerNew getInstance() {
        return S_INSTANCE;
    }

    public static void initialize() {
        if (sIsInit)
            return;
        sIsInit = true;
    }

    public static void logOnPTC(String username, String password) {
        S_INSTANCE.http = new OkHttpClient();
        while (!S_INSTANCE.logged) {
            //Using PTC, remove Google infos
            config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
            try {
                S_INSTANCE.cp = new PtcCredentialProvider(S_INSTANCE.http, username, password);
                config.setString(ConfigKey.LOGIN_PTC_USERNAME, username);
                if (config.getBool(ConfigKey.LOGIN_SAVE_AUTH) || checkSaveAuth()) {
                    config.setString(ConfigKey.LOGIN_PTC_PASSWORD, password);
                    config.setBool(ConfigKey.LOGIN_SAVE_AUTH, true);
                } else {
                    config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
                    config.delete(ConfigKey.LOGIN_SAVE_AUTH);
                }
            } catch (Exception e) {
                alertFailedLogin(e.getMessage());
            }
        }

    }

    public static void logOnGoogleAuth() {
        S_INSTANCE.go = null;
        S_INSTANCE.cp = null;
        S_INSTANCE.http = new OkHttpClient();
        while (!S_INSTANCE.logged) {
            //Using Google, remove PTC infos
            config.delete(ConfigKey.LOGIN_PTC_USERNAME);
            config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
            String authCode = config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
            boolean refresh = false;
            if (authCode == null) {
                //We need to get the auth code, as we do not have it yet.
                UIManager.put("OptionPane.okButtonText", "Ok");
                JOptionPane.showMessageDialog(null, "You will need to provide a google authentication key to log in. Press OK to continue.", "Google Auth", JOptionPane.PLAIN_MESSAGE);

                //We're gonna try to load the page using the users browser
                JOptionPane.showMessageDialog(null, "A webpage should open up, please allow the permissions, and then copy the code into your clipboard. Press OK to continue", "Google Auth", JOptionPane.PLAIN_MESSAGE);
                boolean success = Browser.openUrl(GoogleUserCredentialProvider.LOGIN_URL);

                if (!success) {
                    // Okay, couldn't open it. We use the manual copy window
                    UIManager.put("OptionPane.cancelButtonText", "Copy To Clipboard");
                    if (JOptionPane.showConfirmDialog(null, "Please copy this link and paste it into a browser.\nThen, allow the permissions, and copy the code into your clipboard.", "Google Auth", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                        StringSelection ss = new StringSelection(GoogleUserCredentialProvider.LOGIN_URL);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
                    }
                    UIManager.put("OptionPane.cancelButtonText", "Cancel");
                }

                //The user should have the auth code now. Lets get it.
                authCode = JOptionPane.showInputDialog(null, "Please provide the authentication code", "Google Auth", JOptionPane.PLAIN_MESSAGE);
            } else {
                refresh = true;
            }
            try {
                GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(S_INSTANCE.http);
                if (refresh) provider.refreshToken(authCode);
                else provider.login(authCode);
                S_INSTANCE.cp = provider;
                if (config.getBool(ConfigKey.LOGIN_SAVE_AUTH) || checkSaveAuth()) {
                    if (!refresh)
                        config.setString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, provider.getRefreshToken());
                    config.setBool(ConfigKey.LOGIN_SAVE_AUTH, true);
                } else {
                    config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                    config.delete(ConfigKey.LOGIN_SAVE_AUTH);
                }
            } catch (Exception e) {
                alertFailedLogin(e.getMessage());
            }
        }

    }

    private static void initOtherControllers(PokemonGo go) {
        InventoryController.initialize(go);
        PokemonBagController.initialize(go);
    }

    private static void alertFailedLogin(String message) {
        //TODO alertFailedLogin
        return;
    }

    private static boolean checkSaveAuth() {
        return true;
    }

    private static LoginType checkSavedConfig() {
        if (!config.getBool(ConfigKey.LOGIN_SAVE_AUTH)) {
            return LoginType.NONE;
        } else {
            if (getLoginData(LoginType.GOOGLE) != null) return LoginType.GOOGLE;
            if (getLoginData(LoginType.PTC) != null) return LoginType.PTC;
            return LoginType.NONE;
        }
    }

    private static List<String> getLoginData(LoginType type) {
        switch (type) {
            case GOOGLE:
                String token = config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                return (token != null) ? Collections.singletonList(token) : null;
            case PTC:
                String username = config.getString(ConfigKey.LOGIN_PTC_USERNAME);
                String password = config.getString(ConfigKey.LOGIN_PTC_PASSWORD);
                return (username != null && password != null) ? Arrays.asList(username, password) : null;
            default:
                return null;
        }
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
            case GOOGLE:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
            case PTC:
                config.delete(ConfigKey.LOGIN_PTC_USERNAME);
                config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
            default:
        }
    }

    private static boolean checkForSavedCredentials() {
        LoginType savedLogin = checkSavedConfig();
        if (savedLogin == LoginType.NONE) {
            return false;
        } else {
            // TODO: Implement choose if you want to login with that saved data
            return true;
        }
    }

    public static void logOff() {

    }

    public static PlayerProfile getPlayerProfile() {
        return S_INSTANCE.go != null ? S_INSTANCE.go.getPlayerProfile() : null;
    }

    public enum LoginType {
        GOOGLE,
        PTC,
        BOTH,
        NONE
    }
}
