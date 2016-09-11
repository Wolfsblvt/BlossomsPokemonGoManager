package me.corriekay.pokegoutil.gui.controller;

import java.io.IOException;
import java.net.URL;

import com.pokegoapi.auth.GoogleUserCredentialProvider;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.data.enums.LoginType;
import me.corriekay.pokegoutil.data.managers.AccountManager;
import me.corriekay.pokegoutil.data.models.BpmResult;
import me.corriekay.pokegoutil.data.models.LoginData;
import me.corriekay.pokegoutil.utils.helpers.Browser;

public class LoginController extends StackPane {

    @FXML
    private final String fxmlLayout = "layout/Login.fxml";
    private final URL icon;
    private final ClassLoader classLoader = getClass().getClassLoader();
    private final Scene rootScene;

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
        final FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource(fxmlLayout));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        rootScene = new Scene(fxmlLoader.getRoot());
        final Stage stage = new Stage();
        icon = classLoader.getResource("icon/PokeBall-icon.png");
        stage.getIcons().add(new Image(icon.toExternalForm()));
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(rootScene);

        BlossomsPoGoManager.setNewPrimaryStage(stage);
    }

    private void alertFailedLogin(final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Login");
        alert.setHeaderText("Unfortunately, your login has failed");
        alert.setContentText(message != null ? message : "" + "\nPress OK to try again.");
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        configLoginData = accountManager.getLoginData();

        googleAuthBtn.setOnAction(this::onGoogleAuthBtnClicked);
        ptcLoginBtn.setOnAction(this::onPTCLoginBtnClicked);
        saveAuthChkbx.setOnAction(this::onAutoRelogChanged);
        getTokenBtn.setOnAction(this::onGetToken);

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

    private void onAutoRelogChanged(final ActionEvent actionEvent) {
        final boolean saveCredentials = ((CheckBox) actionEvent.getSource()).isSelected();
        accountManager.setSaveLogin(saveCredentials);
        toggleFields(saveCredentials);
    }

    private void onGetToken(final ActionEvent actionEvent) {
        tokenField.setDisable(false);
        Browser.openUrl(GoogleUserCredentialProvider.LOGIN_URL);
    }

    @FXML
    void onGoogleAuthBtnClicked(final ActionEvent event) {
        final LoginData loginData = new LoginData();

        if (configLoginData.hasToken()) {
            loginData.setToken(configLoginData.getToken());
            loginData.setSavedToken(true);
        } else {
            loginData.setToken(tokenField.getText());
        }
        loginData.setLoginType(LoginType.GOOGLE);

        tryLogin(loginData);
    }

    @FXML
    void onPTCLoginBtnClicked(final ActionEvent event) {
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

    private void tryLogin(final LoginData loginData) {
        try {
            final BpmResult loginResult = accountManager.login(loginData);

            if (loginResult.isSuccess()) {
                openMainWindow();
            } else {
                alertFailedLogin(loginResult.getErrorMessage());
            }
        } catch (final Exception e) {
            alertFailedLogin(e.toString());
        }
    }
}
