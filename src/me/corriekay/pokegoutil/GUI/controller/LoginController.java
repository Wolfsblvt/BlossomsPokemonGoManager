package me.corriekay.pokegoutil.GUI.controller;

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
import me.corriekay.pokegoutil.DATA.enums.LoginType;
import me.corriekay.pokegoutil.DATA.managers.AccountManager;
import me.corriekay.pokegoutil.DATA.models.LoginData;
import me.corriekay.pokegoutil.DATA.models.BpmResult;
import me.corriekay.pokegoutil.utils.helpers.Browser;

import java.io.IOException;
import java.net.URL;

public class LoginController extends StackPane {

    @FXML
    private final String fxmlLayout = "layout/Login.fxml";
    private final URL icon;
    private ClassLoader classLoader = getClass().getClassLoader();
    private Scene rootScene;

    private AccountManager accountManager = AccountManager.getInstance();

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
        FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource(fxmlLayout));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        rootScene = new Scene(fxmlLoader.getRoot());
        Stage stage = new Stage();
        icon = classLoader.getResource("icon/PokeBall-icon.png");
        stage.getIcons().add(new Image(icon.toExternalForm()));
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(rootScene);

        BlossomsPoGoManager.setNewPrimaryStage(stage);
    }

    private void alertFailedLogin(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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

        boolean hasSavedCredentials = configLoginData.hasSavedCredentials();
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

    private void onAutoRelogChanged(ActionEvent actionEvent) {
        boolean saveCredentials = ((CheckBox) actionEvent.getSource()).isSelected();
        accountManager.setSaveLogin(saveCredentials);
        toggleFields(saveCredentials);
    }

    private void onGetToken(ActionEvent actionEvent) {
        tokenField.setDisable(false);
        Browser.openUrl(GoogleUserCredentialProvider.LOGIN_URL);
    }

    @FXML
    void onGoogleAuthBtnClicked(ActionEvent event) {
        LoginData loginData = new LoginData();

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
    void onPTCLoginBtnClicked(ActionEvent event) {
        LoginData loginData = new LoginData();

        loginData.setUsername(usernameField.getText());
        loginData.setPassword(passwordField.getText());
        loginData.setLoginType(LoginType.PTC);

        tryLogin(loginData);
    }

    private void openMainWindow() {
        new MainWindowController();
        BlossomsPoGoManager.getPrimaryStage().show();
    }

    private void toggleFields(boolean save) {
        if (usernameField.getText().isEmpty() || !save)
            usernameField.setDisable(false);
        else
            usernameField.setDisable(true);

        if (passwordField.getText().isEmpty() || !save)
            passwordField.setDisable(false);
        else
            passwordField.setDisable(true);

        if (tokenField.getText().isEmpty() || !save)
            tokenField.setDisable(false);
        else
            tokenField.setDisable(true);

        getTokenBtn.setDisable(false);
    }

    private void tryLogin(LoginData loginData) {
        try {
            BpmResult loginResult = accountManager.login(loginData);

            if (loginResult.isSuccess()) {
                openMainWindow();
            } else {
                alertFailedLogin(loginResult.getErrorMessage());
            }
        } catch (Exception e) {
            alertFailedLogin(e.toString());
        }
    }
}
