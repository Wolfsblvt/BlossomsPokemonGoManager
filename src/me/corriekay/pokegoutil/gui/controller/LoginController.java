package me.corriekay.pokegoutil.gui.controller;

import com.pokegoapi.auth.GoogleUserCredentialProvider;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.data.enums.LoginType;
import me.corriekay.pokegoutil.data.managers.AccountManager;
import me.corriekay.pokegoutil.data.models.BpmResult;
import me.corriekay.pokegoutil.data.models.LoginData;
import me.corriekay.pokegoutil.utils.helpers.Browser;

/**
 * The LoginController is use to handle all login related actions.
 */
public class LoginController extends BaseController<StackPane> {

    private final AccountManager accountManager = AccountManager.getInstance();
    private LoginData configLoginData = new LoginData();

    // UI elements
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button ptcLoginBtn;

    @FXML
    private TextField tokenField;

    @FXML
    private Button getTokenBtn;

    @FXML
    private Button googleAuthBtn;

    @FXML
    private CheckBox saveAuthChkbx;

    public LoginController() {
        super();
        initializeController();
    }

    /**
     * Displays an error dialog with a message.
     *
     * @param message the error message
     */
    private void alertFailedLogin(final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Login");
        alert.setHeaderText("Unfortunately, your login has failed");
        alert.setContentText(message != null ? message : "" + "\nPress OK to try again.");
        alert.showAndWait();
    }

    @Override
    public String getFxmlLayout() {
        return "layout/Login.fxml";
    }

    @Override
    public void setGuiControllerSettings() {
        guiControllerSettings.setTitle("Login");
        guiControllerSettings.setResizeable(false);
    }

    @FXML
    private void initialize() {
        configLoginData = accountManager.getLoginData();

        googleAuthBtn.setOnAction(this::onGoogleAuthBtnClicked);
        ptcLoginBtn.setOnAction(this::onPtcLoginBtnClicked);
        saveAuthChkbx.setOnAction(this::onSaveAuthChkbxChanged);
        getTokenBtn.setOnAction(this::ongetTokenBtnClicked);

        final boolean hasSavedCredentials = configLoginData.hasSavedCredentials();
        saveAuthChkbx.setSelected(hasSavedCredentials);

        if (hasSavedCredentials) {
            if (configLoginData.hasUsername()) {
                usernameField.setText(configLoginData.getUsername());
                usernameField.setDisable(true);
            }

            if (configLoginData.hasPassword()) {
                passwordField.setText(configLoginData.getPassword());
                passwordField.setDisable(true);
            }

            if (configLoginData.hasToken()) {
                tokenField.setText("Using Previous Token");
                tokenField.setDisable(true);
                getTokenBtn.setDisable(true);
            }
        }
    }

    /**
     * Event handler for saveAuthChkbx.
     *
     * @param actionEvent event
     */
    private void onSaveAuthChkbxChanged(final ActionEvent actionEvent) {
        final boolean saveCredentials = ((CheckBox) actionEvent.getSource()).isSelected();
        accountManager.setSaveLogin(saveCredentials);
        toggleFields(saveCredentials);
    }

    /**
     * Event handler for getTokenBtn.
     *
     * @param ignored event
     */
    private void ongetTokenBtnClicked(final ActionEvent ignored) {
        tokenField.setDisable(false);
        Browser.openUrl(GoogleUserCredentialProvider.LOGIN_URL);
    }

    /**
     * Event handler for googleAuthBtn.
     *
     * @param ignored event
     */
    private void onGoogleAuthBtnClicked(final ActionEvent ignored) {
        final LoginData loginData = new LoginData();

        if (configLoginData.hasToken()) {
            loginData.setToken(configLoginData.getToken());
            loginData.setSavedToken(true);
        } else {
            loginData.setToken(tokenField.getText());
        }
        loginData.setLoginType(LoginType.GOOGLE_AUTH);

        tryLogin(loginData);
    }

    /**
     * Event handler for ptcLoginBtn.
     *
     * @param ignored event
     */
    private void onPtcLoginBtnClicked(final ActionEvent ignored) {
        final LoginData loginData = new LoginData();

        loginData.setUsername(usernameField.getText());
        loginData.setPassword(passwordField.getText());
        loginData.setLoginType(LoginType.PTC);

        tryLogin(loginData);
    }

    private void openMainWindow() {
        new MainWindowController();
        BlossomsPoGoManager.getPrimaryStage().show();
    }

    /**
     * Handle enabling and disabling of gui if credentials are saved.
     *
     * @param save save credentials
     */
    private void toggleFields(final boolean save) {
        if (usernameField.getText().isEmpty() || !save) {
            usernameField.setDisable(false);
        } else {
            usernameField.setDisable(true);
        }

        if (passwordField.getText().isEmpty() || !save) {
            passwordField.setDisable(false);
        } else {
            passwordField.setDisable(true);
        }

        if (tokenField.getText().isEmpty() || !save) {
            tokenField.setDisable(false);
        } else {
            tokenField.setDisable(true);
        }

        getTokenBtn.setDisable(false);
    }

    /**
     * Try to login into pokemon go using the provided login credentials.
     *
     * @param loginData login credentials
     */
    private void tryLogin(final LoginData loginData) {
        final BpmResult loginResult = accountManager.login(loginData);

        if (loginResult.isSuccess()) {
            openMainWindow();
        } else {
            alertFailedLogin(loginResult.getErrorMessage());
        }
    }
}
