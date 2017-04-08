package me.corriekay.pokegoutil.data.managers;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
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
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.request.RequestFailedException;

import me.corriekay.pokegoutil.data.enums.LoginType;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.helpers.Browser;
import me.corriekay.pokegoutil.utils.helpers.LoginHelper;
import me.corriekay.pokegoutil.utils.windows.WindowStuffHelper;
import me.corriekay.pokegoutil.windows.PokemonGoMainWindow;
import okhttp3.OkHttpClient;

/**
 * This controller does the login/log off, and different account information (aka player data).
 */
@Deprecated
public final class AccountController {

    private static final AccountController instance = new AccountController();
    private static boolean sIsInit = false;
    private static ConfigNew config = ConfigNew.getConfig();
    protected PokemonGoMainWindow mainWindow = null;
    protected PokemonGo go = null;
    protected OkHttpClient http;
    protected CredentialProvider cp;
    private boolean logged = false;

    private AccountController() {

    }

    public static AccountController getInstance() {
        return instance;
    }

    /**
     * Does pre-initializiton before using of the class.
     */
    public static void initialize() {
        if (sIsInit) {
            return;
        }

        sIsInit = true;
    }

    public static void logOn() {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        OkHttpClient http;
        CredentialProvider credentialProvider;
        PokemonGo go = null;
        int tries = 0;

        while (!instance.logged) {
            tries++;
            //BEGIN LOGIN WINDOW
            go = null;
            credentialProvider = null;
            http = new OkHttpClient();

            final JTextField ptcUsernameTextField = new JTextField(config.getString(ConfigKey.LOGIN_PTC_USERNAME));
            final JTextField ptcPasswordTextField = new JPasswordField(config.getString(ConfigKey.LOGIN_PTC_PASSWORD));

            final int directLoginWithSavedCredentials = checkForSavedCredentials();

            int response;
            
            if (directLoginWithSavedCredentials == JOptionPane.CANCEL_OPTION) {
                System.exit(0);
                return;
            } else if (directLoginWithSavedCredentials == JOptionPane.YES_OPTION) {
                if (getLoginData(LoginType.GOOGLE_AUTH) != null || getLoginData(LoginType.GOOGLE_APP_PASSWORD) != null) {
                    response = JOptionPane.NO_OPTION; // This means Google. Trust me
                } else if (getLoginData(LoginType.PTC) != null) {
                    response = JOptionPane.YES_OPTION; // And this PTC. Yeah, really
                } else {
                    // Something is wrong here, we delete login and start anew
                    deleteLoginData(LoginType.ALL);
                    return;
                }
            } else {
                // We do not want to login directly, so go for the question box and delete that data before
                deleteLoginData(LoginType.ALL);

                UIManager.put("OptionPane.noButtonText", "Use Google Auth");
                UIManager.put("OptionPane.yesButtonText", "Use PTC Auth");
                UIManager.put("OptionPane.cancelButtonText", "Exit");
                UIManager.put("OptionPane.okButtonText", "Ok");

                final JPanel panel1 = new JPanel(new BorderLayout());
                panel1.add(new JLabel("PTC Username: "), BorderLayout.LINE_START);
                panel1.add(ptcUsernameTextField, BorderLayout.CENTER);
                final JPanel panel2 = new JPanel(new BorderLayout());
                panel2.add(new JLabel("PTC Password: "), BorderLayout.LINE_START);
                panel2.add(ptcPasswordTextField, BorderLayout.CENTER);
                final Object[] panel = {panel1, panel2};

                response = JOptionPane.showConfirmDialog(
                    WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
                    panel,
                    "Login",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            }

            if (response == JOptionPane.CANCEL_OPTION || response == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            } else if (response == JOptionPane.OK_OPTION) {
                try {
                    credentialProvider = new PtcCredentialProvider(http, ptcUsernameTextField.getText(), ptcPasswordTextField.getText());
                    config.setString(ConfigKey.LOGIN_PTC_USERNAME, ptcUsernameTextField.getText());
                    if (config.getBool(ConfigKey.LOGIN_SAVE_AUTH) || checkSaveAuth()) {
                        config.setString(ConfigKey.LOGIN_PTC_PASSWORD, ptcPasswordTextField.getText());
                        config.setBool(ConfigKey.LOGIN_SAVE_AUTH, true);
                    } else {
                        deleteLoginData(LoginType.PTC);
                    }
                } catch (final Exception e) {
                    alertFailedLogin(e.getClass().getSimpleName(), e.getMessage(), tries);
                    e.printStackTrace();
                    // deleteLoginData(LoginType.PTC);
                    continue;
                }

                //Using PTC, remove Google infos
                deleteLoginData(LoginType.GOOGLE_AUTH, true);
                deleteLoginData(LoginType.GOOGLE_APP_PASSWORD, true);

            } else if (response == JOptionPane.NO_OPTION) {
                final String googleAuthTitle = "Google Auth";

                // We to set up some vars that we may need later.
                // Those will be overwritten if data is entered, otherwise they should contain the correct values loaded from the config.
                String googleAuthToken = config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, null);
                String googleUsername = config.getString(ConfigKey.LOGIN_GOOGLE_APP_USERNAME, null);
                String googlePassword = config.getString(ConfigKey.LOGIN_GOOGLE_APP_PASSWORD, null);
                boolean authTokenRefresh = false;

                LoginType usedGoogleLoginType = checkSavedConfig();

                if (LoginType.isGoogle(usedGoogleLoginType)) {
                    // We check if google login data saved and skip the input options then
                    if (usedGoogleLoginType == LoginType.GOOGLE_AUTH) {
                        // Sets refresh, when we use the existing token, cause it needs to be refreshed
                        authTokenRefresh = true;
                    }
                } else {
                    // Okay, user should choose which login method he wants:
                    String message = "Choose your method to login with your Google Account."
                        + StringLiterals.NEWLINE
                        + StringLiterals.NEWLINE + "If you are unexperienced, just choose \"OAuth Token\"."
                        + StringLiterals.NEWLINE + "If you have trouble logging in with that method, or know what you are doing,"
                        + StringLiterals.NEWLINE + "go ahead and take the App Password method.";
                    String[] options = new String[] {"Use OAuth Token", "Advanced: Use App Password"};
                    final int answer = JOptionPane.showOptionDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT, message, googleAuthTitle,
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);

                    switch (answer) {
                        case 0: // Use OAuth Token
                            //We need to get the auth code, as we do not have it yet.
                            UIManager.put("OptionPane.okButtonText", "Ok");
                            JOptionPane.showMessageDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
                                "You will need to provide a google authentication key to log in."
                                    + StringLiterals.NEWLINE + "A webpage should open up, please allow the permissions, and then copy the code into your clipboard."
                                    + StringLiterals.NEWLINE
                                    + StringLiterals.NEWLINE + "Press OK to continue",
                                googleAuthTitle,
                                JOptionPane.PLAIN_MESSAGE);

                            //We're gonna try to load the page using the users browser
                            final boolean success = Browser.openUrl(GoogleUserCredentialProvider.LOGIN_URL);

                            // Okay, couldn't open it. We use the manual copy window
                            UIManager.put("OptionPane.cancelButtonText", "Copy To Clipboard");
                            if (!success && JOptionPane.showConfirmDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
                                "Please copy this link and paste it into a browser.\nThen, allow the permissions, and copy the code into your clipboard.",
                                googleAuthTitle,
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                                final StringSelection ss = new StringSelection(GoogleUserCredentialProvider.LOGIN_URL);
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
                            }
                            UIManager.put("OptionPane.cancelButtonText", ButtonText.CANCEL);

                            //The user should have the auth code now. Lets get it.
                            googleAuthToken = JOptionPane.showInputDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
                                "Please provide the authentication code",
                                googleAuthTitle,
                                JOptionPane.PLAIN_MESSAGE);
                            usedGoogleLoginType = LoginType.GOOGLE_AUTH;
                            break;

                        case 1: // Advanced: Use App Password
                            //We need to get the user data, as we do not have it yet.
                            final String appPasswordMessage = "You want to login via App Password."
                                + StringLiterals.NEWLINE + "For that, an app password has to be created."
                                + StringLiterals.NEWLINE + "If you already have your password, click \"Skip\"."
                                + StringLiterals.NEWLINE
                                + StringLiterals.NEWLINE + "Otherwise, click on \"Open Website\" to access the google account control page where you are able"
                                + StringLiterals.NEWLINE + "to create an app password."
                                + StringLiterals.NEWLINE + "Choose 'Other' as app and name it something like 'BPGM' or such. Then copy your password."
                                + StringLiterals.NEWLINE
                                + StringLiterals.NEWLINE + "Do note: If google tells you you can't configure your App Passwords, then go back to your"
                                + StringLiterals.NEWLINE + "google account control page and enable 2-Step Verification. Then you will be able to.";

                            final String[] appPasswordOptions = new String[] {"Open Website", "Skip"};
                            final int appPasswordResponse = JOptionPane.showOptionDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT, appPasswordMessage, googleAuthTitle,
                                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, appPasswordOptions, options[0]);

                            // Open URL if chosen
                            switch (appPasswordResponse) {
                                case 0: // Open Webpage
                                    String appPasswordUrl = "https://security.google.com/settings/security/apppasswords";
                                    Browser.openUrl(appPasswordUrl);
                                    break;
                                case 1: // Skip
                                    // We do nothing here, we have skipped
                                    break;
                                default:
                                    // Can't happen
                            }

                            // We ask the user to enter his login data now
                            final int width = 15;
                            JTextField googleUsernameTextField = new JTextField(width);
                            JTextField googlePasswordTextField = new JTextField(width);

                            final JPanel panel1 = new JPanel(new BorderLayout());
                            panel1.add(new JLabel("Username: "), BorderLayout.LINE_START);
                            panel1.add(googleUsernameTextField, BorderLayout.CENTER);
                            final JPanel panel2 = new JPanel(new BorderLayout());
                            panel2.add(new JLabel("Password: "), BorderLayout.LINE_START);
                            panel2.add(googlePasswordTextField, BorderLayout.CENTER);
                            final Object[] goggleAppPasswordPanel = {panel1, panel2};

                            final int googleAppLoginDetailsResult = JOptionPane.showConfirmDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
                                goggleAppPasswordPanel,
                                googleAuthTitle,
                                JOptionPane.OK_CANCEL_OPTION);
                            if (googleAppLoginDetailsResult == JOptionPane.OK_OPTION) {
                                googleUsername = googleUsernameTextField.getText();
                                googlePassword = googlePasswordTextField.getText();
                            }
                            usedGoogleLoginType = LoginType.GOOGLE_APP_PASSWORD;
                            break;
                        default:
                            // Can't happen
                    }
                }

                // Okay, we gathered all information now, so we will decide what we do now with it
                // and then log in.
                try {
                    GoogleUserCredentialProvider googleProvider = null;
                    GoogleAutoCredentialProvider googleAutoProvider;
                    if (usedGoogleLoginType == LoginType.GOOGLE_AUTH) {
                        // We have google OAuth here, so we use the token and/or refresh it
                        if (authTokenRefresh) {
                            // Based on usage in https://github.com/Grover-c13/PokeGOAPI-Java
                            googleProvider = new GoogleUserCredentialProvider(http, googleAuthToken);
                        } else {
                            googleProvider = new GoogleUserCredentialProvider(http);
                            googleProvider.login(googleAuthToken);
                        }
                        credentialProvider = googleProvider;
                    } else if (usedGoogleLoginType == LoginType.GOOGLE_APP_PASSWORD) {
                        // We have app password login here
                        googleAutoProvider = new GoogleAutoCredentialProvider(http, googleUsername, googlePassword);
                        credentialProvider = googleAutoProvider;
                    }

                    // Now check if the data should be saved in config. Asking the user.
                    if (config.getBool(ConfigKey.LOGIN_SAVE_AUTH) || checkSaveAuth()) {
                        // We save that auth is generally saved
                        config.setBool(ConfigKey.LOGIN_SAVE_AUTH, true);

                        // We save the login data now
                        if (usedGoogleLoginType == LoginType.GOOGLE_AUTH && !authTokenRefresh) {
                            config.setString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, googleProvider.getRefreshToken());
                        } else if (usedGoogleLoginType == LoginType.GOOGLE_APP_PASSWORD) {
                            config.setString(ConfigKey.LOGIN_GOOGLE_APP_USERNAME, googleUsername);
                            config.setString(ConfigKey.LOGIN_GOOGLE_APP_PASSWORD, googlePassword);
                        }
                    } else {
                        deleteLoginData(LoginType.GOOGLE_APP_PASSWORD);
                        deleteLoginData(LoginType.GOOGLE_AUTH);
                    }
                } catch (final Exception e) {
                    alertFailedLogin(e.getClass().getSimpleName(), e.getMessage(), tries);
                    e.printStackTrace();
                    // deleteLoginData(LoginType.GOOGLE_APP_PASSWORD);
                    // deleteLoginData(LoginType.GOOGLE_AUTH);
                    continue;
                }

                //Using Google, remove PTC infos
                deleteLoginData(LoginType.PTC, true);
            }
            UIManager.put("OptionPane.noButtonText", "No");
            UIManager.put("OptionPane.yesButtonText", "Yes");
            UIManager.put("OptionPane.okButtonText", "Ok");
            UIManager.put("OptionPane.cancelButtonText", ButtonText.CANCEL);

            try {
                if (credentialProvider != null) {

                    go = new PokemonGo(http);
                    LoginHelper.login(go, credentialProvider, api -> {
                        instance.logged = true;
                        instance.go = api;
                        initOtherControllers(api);
                        instance.mainWindow = new PokemonGoMainWindow(api, true);
                        instance.mainWindow.start();
                    });
                } else {
                    throw new IllegalStateException("credentialProvider is null.");
                }
            } catch (RequestFailedException e) {
                alertFailedLogin(e.getClass().getSimpleName(), e.getMessage(), tries);
                e.printStackTrace();
            }
        }
    }

    private static void initOtherControllers(final PokemonGo go) {
        InventoryManager.initialize(go);
        PokemonBagManager.initialize(go);
    }

    /**
     * Alerts a failed login with a popup showing the specific error.
     *
     * @param exceptionClass The class of the exception, or error type.
     * @param message        The message that is shown.
     * @param tries          The number of the try that this is (zero based).
     */
    private static void alertFailedLogin(final String exceptionClass, final String message, final int tries) {
        System.out.println("Error: " + exceptionClass + StringLiterals.NEWLINE + message);
        JOptionPane.showMessageDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
            "Unfortunately, your login has failed. Reason: "
                + StringLiterals.NEWLINE + exceptionClass + ": " + message
                + StringLiterals.NEWLINE + "This is try number " + tries + "."
                + StringLiterals.NEWLINE + "Press OK to try again.",
            "Login Failed",
            JOptionPane.ERROR_MESSAGE);
    }

    private static boolean checkSaveAuth() {
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.okButtonText", "Ok");
        UIManager.put("OptionPane.cancelButtonText", ButtonText.CANCEL);
        return JOptionPane.showConfirmDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
            "Do you wish to save the password/auth token?\nCaution: These are saved in plain-text.", "Save Authentication?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private static LoginType checkSavedConfig() {
        if (!config.getBool(ConfigKey.LOGIN_SAVE_AUTH)) {
            return LoginType.NONE;
        } else {
            if (getLoginData(LoginType.GOOGLE_AUTH) != null) {
                return LoginType.GOOGLE_AUTH;
            }
            if (getLoginData(LoginType.GOOGLE_APP_PASSWORD) != null) {
                return LoginType.GOOGLE_APP_PASSWORD;
            }
            if (getLoginData(LoginType.PTC) != null) {
                return LoginType.PTC;
            }
            return LoginType.NONE;
        }
    }

    private static List<String> getLoginData(final LoginType type) {
        switch (type) {
            case GOOGLE_AUTH:
                final String token = config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                return (token != null) ? Collections.singletonList(token) : null;
            case GOOGLE_APP_PASSWORD:
                final String googleUsername = config.getString(ConfigKey.LOGIN_GOOGLE_APP_USERNAME);
                final String googlePassword = config.getString(ConfigKey.LOGIN_GOOGLE_APP_PASSWORD);
                return (googleUsername != null && googlePassword != null) ? Arrays.asList(googleUsername, googlePassword) : null;
            case PTC:
                final String ptcUsername = config.getString(ConfigKey.LOGIN_PTC_USERNAME);
                final String ptcPassword = config.getString(ConfigKey.LOGIN_PTC_PASSWORD);
                return (ptcUsername != null && ptcPassword != null) ? Arrays.asList(ptcUsername, ptcPassword) : null;
            default:
                return null;
        }
    }


    private static void deleteLoginData(final LoginType type) {
        deleteLoginData(type, false);
    }

    private static void deleteLoginData(final LoginType type, final boolean justCleanup) {
        if (!justCleanup) {
            config.delete(ConfigKey.LOGIN_SAVE_AUTH);
        }
        switch (type) {
            case ALL:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                config.delete(ConfigKey.LOGIN_GOOGLE_APP_USERNAME);
                config.delete(ConfigKey.LOGIN_GOOGLE_APP_PASSWORD);
                config.delete(ConfigKey.LOGIN_PTC_USERNAME);
                config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
                break;
            case PTC:
                config.delete(ConfigKey.LOGIN_PTC_USERNAME);
                config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
                break;
            case GOOGLE_AUTH:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                break;
            case GOOGLE_APP_PASSWORD:
                config.delete(ConfigKey.LOGIN_GOOGLE_APP_USERNAME);
                config.delete(ConfigKey.LOGIN_GOOGLE_APP_PASSWORD);
                break;
            default:
        }
    }

    /** 
     * Check if there is any login saved and ask for user to use it or not.
     * @return JOptionPane.NO_OPTION, JOptionPane.YES_OPTION, JOptionPane.CANCEL_OPTION
     */
    private static int checkForSavedCredentials() {
        final LoginType savedLogin = checkSavedConfig();
        if (savedLogin == LoginType.NONE) {
            return JOptionPane.NO_OPTION;
        } else {
            UIManager.put("OptionPane.noButtonText", "No");
            UIManager.put("OptionPane.yesButtonText", "Yes");
            UIManager.put("OptionPane.okButtonText", "Ok");
            UIManager.put("OptionPane.cancelButtonText", "Exit");
            return JOptionPane.showConfirmDialog(WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
                "You have saved login data for " + savedLogin.toString() + ". Want to login with that?",
                "Use Saved Login",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        }
    }

    public static void logOff() throws Exception {
        if (!sIsInit) {
            throw new ExceptionInInitializerError("AccountController needs to be initialized before logging on");
        }
        if (!instance.logged) {
            return;
        }

        instance.logged = false;
        instance.mainWindow.setVisible(false);
        instance.mainWindow.dispose();
        instance.mainWindow = null;
        logOn();
    }

    public static PlayerProfile getPlayerProfile() {
        return instance.go != null ? instance.go.getPlayerProfile() : null;
    }

    /**
     * Inner class that saves the texts buttons can contain.
     */
    private static final class ButtonText {
        private static final String CANCEL = "Cancel";
    }
}
