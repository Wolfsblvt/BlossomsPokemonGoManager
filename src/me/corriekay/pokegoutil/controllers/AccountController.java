package me.corriekay.pokegoutil.controllers;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;

import me.corriekay.pokegoutil.utils.Browser;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.windows.PokemonGoMainWindow;
import okhttp3.OkHttpClient;
import sun.rmi.runtime.Log;

/*this controller does the login/log off, and different account information (aka player data)
 *
 */
public final class AccountController {

    private static final AccountController S_INSTANCE = new AccountController();
    private static boolean sIsInit = false;

    private Console console;
    private boolean logged = false;
    private PokemonGoMainWindow mainWindow = null;
    private PokemonGo go = null;

    private static Config config = Config.getConfig();

    private AccountController() {

    }

    public static AccountController getInstance() {
        return S_INSTANCE;
    }

    public static void initialize(Console console) {
        if (sIsInit)
            return;

        S_INSTANCE.console = console;

        sIsInit = true;
    }

    public static void logOn() throws Exception {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        OkHttpClient http;
        CredentialProvider cp;
        PokemonGo go = null;
        while (!S_INSTANCE.logged) {
            //BEGIN LOGIN WINDOW
            go = null;
            cp = null;
            http = new OkHttpClient();

            JTextField username = new JTextField(config.getString("login.PTCUsername", null));
            JTextField password = new JPasswordField(config.getString("login.PTCPassword", null));

            boolean directLoginWithSavedCredentials = checkForSavedCredentials();

            int response;

            if (directLoginWithSavedCredentials) {
                if (getLoginData(LoginType.GOOGLE) != null) {
                    response = JOptionPane.NO_OPTION; // This means Google. Trust me
                } else if (getLoginData(LoginType.PTC) != null) {
                    response = JOptionPane.YES_OPTION; // And this PTC. Yeah, really
                } else {
                    // Something is wrong here, we delete login and start anew
                    deleteLoginData(LoginType.BOTH);
                    return;
                }
            } else {
                // We do not want to login directly, so go for the question box and delete that data before
                deleteLoginData(LoginType.BOTH);

                UIManager.put("OptionPane.noButtonText", "Use Google Auth");
                UIManager.put("OptionPane.yesButtonText", "Use PTC Auth");
                UIManager.put("OptionPane.cancelButtonText", "Exit");
                UIManager.put("OptionPane.okButtonText", "Ok");

                JPanel panel1 = new JPanel(new BorderLayout());
                panel1.add(new JLabel("PTC Username: "), BorderLayout.LINE_START);
                panel1.add(username, BorderLayout.CENTER);
                JPanel panel2 = new JPanel(new BorderLayout());
                panel2.add(new JLabel("PTC Password: "), BorderLayout.LINE_START);
                panel2.add(password, BorderLayout.CENTER);
                Object[] panel = {panel1, panel2,};

                response = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            }

            if (response == JOptionPane.CANCEL_OPTION) {
                System.exit(0);
            } else if (response == JOptionPane.OK_OPTION) {
                //Using PTC, remove Google infos
                deleteLoginData(LoginType.GOOGLE, true);
                try {
                    cp = new PtcCredentialProvider(http, username.getText(), password.getText());
                    config.setString("login.PTCUsername", username.getText());
                    if (config.getBool("login.SaveAuth", false) || checkSaveAuth()) {
                        config.setString("login.PTCPassword", password.getText());
                        config.setBool("login.SaveAuth", true);
                    } else {
                        deleteLoginData(LoginType.PTC);
                    }
                } catch (Exception e) {
                    alertFailedLogin();
                    deleteLoginData(LoginType.PTC);
                    continue;
                }
            } else if (response == JOptionPane.NO_OPTION) {
                //Using Google, remove PTC infos
                deleteLoginData(LoginType.PTC, true);
                String authCode = config.getString("login.GoogleAuthToken", null);
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
                    GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
                    if (refresh) provider.refreshToken(authCode);
                    else provider.login(authCode);
                    cp = provider;
                    if (config.getBool("login.SaveAuth", false) || checkSaveAuth()) {
                        if (!refresh)
                            config.setString("login.GoogleAuthToken", provider.getRefreshToken());
                        config.setBool("login.SaveAuth", true);
                    } else {
                        deleteLoginData(LoginType.GOOGLE);
                    }
                } catch (Exception e) {
                    alertFailedLogin();
                    deleteLoginData(LoginType.GOOGLE);
                    continue;
                }

            }
            UIManager.put("OptionPane.noButtonText", "No");
            UIManager.put("OptionPane.yesButtonText", "Yes");
            UIManager.put("OptionPane.okButtonText", "Ok");
            UIManager.put("OptionPane.cancelButtonText", "Cancel");

            if (cp != null)
                go = new PokemonGo(cp, http);
            else
                throw new IllegalStateException();
            S_INSTANCE.logged = true;
        }
        S_INSTANCE.go = go;
        initOtherControllers(go);
        S_INSTANCE.mainWindow = new PokemonGoMainWindow(go, S_INSTANCE.console);
        S_INSTANCE.mainWindow.start();
    }

    private static void initOtherControllers(PokemonGo go) {
        InventoryController.initialize(go);
        PokemonBagController.initialize(go);
    }

    private static void alertFailedLogin() {
        JOptionPane.showMessageDialog(null, "Unfortunately, your login has failed. Press OK to try again.", "Login Failed", JOptionPane.PLAIN_MESSAGE);
    }

    private static boolean checkSaveAuth() {
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.okButtonText", "Ok");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        return JOptionPane.showConfirmDialog(null, "Do you wish to save the password/auth token?\nCaution: These are saved in plain-text.", "Save Authentication?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private static LoginType checkSavedConfig() {
        if (!config.getBool("login.SaveAuth", false)) {
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
                String token = config.getString("login.GoogleAuthToken", null);
                return (token != null) ? Collections.singletonList(token) : null;
            case PTC:
                String username = config.getString("login.PTCUsername", null);
                String password = config.getString("login.PTCPassword", null);
                return (username != null && password != null) ? Arrays.asList(username, password) : null;
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
            case GOOGLE:
                config.delete("login.GoogleAuthToken");
            case PTC:
                config.delete("login.PTCUsername");
                config.delete("login.PTCPassword");
            default:
        }
    }

    private static boolean checkForSavedCredentials() {
        LoginType savedLogin = checkSavedConfig();
        if (savedLogin == LoginType.NONE) {
            return false;
        } else {
            UIManager.put("OptionPane.noButtonText", "No");
            UIManager.put("OptionPane.yesButtonText", "Yes");
            UIManager.put("OptionPane.okButtonText", "Ok");
            UIManager.put("OptionPane.cancelButtonText", "Cancel");
            return JOptionPane.showConfirmDialog(null, "You have saved login data for " + savedLogin.toString() + ". Want to login with that?", "Use Saved Login", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
        }
    }

    public static void logOff() throws Exception {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        if (!S_INSTANCE.logged)
            return;

        S_INSTANCE.logged = false;
        S_INSTANCE.mainWindow.setVisible(false);
        S_INSTANCE.mainWindow.dispose();
        S_INSTANCE.mainWindow = null;
        deleteLoginData(LoginType.BOTH);
        logOn();
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
