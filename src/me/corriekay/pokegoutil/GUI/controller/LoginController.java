package me.corriekay.pokegoutil.GUI.controller;

import com.pokegoapi.auth.GoogleUserCredentialProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.DATA.enums.LoginType;
import me.corriekay.pokegoutil.DATA.managers.AccountManager;
import me.corriekay.pokegoutil.DATA.models.LoginData;
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
    
    //UI elements
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
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(rootScene);

        BlossomsPoGoManager.setNewPrimaryStage(stage);
    }

    @FXML
    private void initialize() {
        AccountManager.getInstance();

        googleAuthBtn.setOnAction(this::onGoogleAuthBtnClicked);
        ptcLoginBtn.setOnAction(this::onPTCLoginBtnClicked);
        saveAuthChkbx.setOnAction(this::onAutoRelogChanged);
        getTokenBtn.setOnAction(this::onGetToken);

        boolean saveCredentials = accountManager.checkForSavedCredentials();
        saveAuthChkbx.setSelected(saveCredentials);

        if (saveCredentials) {
            LoginType loginType = accountManager.checkSavedConfig();
            LoginData loginData = accountManager.getLoginData(loginType);

            if (loginData.hasUsername()) {
                usernameField.setText(loginData.getUsername());
                usernameField.setDisable(true);
            }

            if (loginData.hasPassword()) {
                passwordField.setText(loginData.getPassword());
                passwordField.setDisable(true);
            }

            if (loginData.hasToken()) {
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
        try {
            LoginData loginData = new LoginData();
            loginData.setToken(tokenField.getText());
            loginData.setLoginType(LoginType.GOOGLE);

            accountManager.login(loginData);
        } catch (Exception e) {
            accountManager.alertFailedLogin(e.toString());
        }
        if (accountManager.isLoggedIn()) {
            rootScene.getWindow().hide();
            openMainWindow();
        }
    }

    @FXML
    void onPTCLoginBtnClicked(ActionEvent event) {
        try {
            LoginData loginData = new LoginData();
            loginData.setUsername(usernameField.getText());
            loginData.setPassword(passwordField.getText());
            loginData.setLoginType(LoginType.PTC);
            
            accountManager.login(loginData);
        } catch (Exception e) {
            accountManager.alertFailedLogin(e.getMessage());
        }
        if (accountManager.isLoggedIn()) {
            rootScene.getWindow().hide();
            openMainWindow();
        }
    }

    void openMainWindow() {
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
}
