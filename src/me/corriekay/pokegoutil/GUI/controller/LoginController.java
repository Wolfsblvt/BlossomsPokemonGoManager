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
import javafx.stage.WindowEvent;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.DATA.controllers.AccountManager;
import me.corriekay.pokegoutil.DATA.controllers.AccountManager.LoginType;
import me.corriekay.pokegoutil.utils.helpers.Browser;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class LoginController extends StackPane {

    @FXML
    private final String fxmlLayout = "layout/Login.fxml";
    private final URL icon;
    private ClassLoader classLoader = getClass().getClassLoader();
    private Scene rootScene;
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
        AccountManager.initialize();
        boolean saveCredentials = AccountManager.checkForSavedCredentials();
        googleAuthBtn.setOnAction(this::onGoogleAuthBtnClicked);
        ptcLoginBtn.setOnAction(this::onPTCLoginBtnClicked);
        saveAuthChkbx.setSelected(saveCredentials);
        saveAuthChkbx.setOnAction(this::onAutoRelogChanged);
        getTokenBtn.setOnAction(this::onGetToken);
        if(saveCredentials){
            LoginType loginType = AccountManager.checkSavedConfig();
            List<String> loginData = AccountManager.getLoginData(loginType);
            switch (loginType) {
                case BOTH:
                    usernameField.setText(loginData.get(0));
                    usernameField.setDisable(true);
                    passwordField.setText(loginData.get(1));
                    passwordField.setDisable(true);
                    tokenField.setText("Using Previous Token");
                    tokenField.setDisable(true);
                    getTokenBtn.setDisable(true);
                    break;
                case GOOGLE:
                    tokenField.setText("Using Previous Token");
                    tokenField.setDisable(true);
                    getTokenBtn.setDisable(true);
                    break;
                case PTC:
                    usernameField.setText(loginData.get(0));
                    usernameField.setDisable(true);
                    passwordField.setText(loginData.get(1));
                    passwordField.setDisable(true);
                    break;
                default:
            }
        }
    }

    private void onGetToken(ActionEvent actionEvent) {
        tokenField.setDisable(false);
        Browser.openUrl(GoogleUserCredentialProvider.LOGIN_URL);
    }

    private void onAutoRelogChanged(ActionEvent actionEvent) {
        boolean saveCredentials = ((CheckBox)actionEvent.getSource()).isSelected();
        AccountManager.setSaveLogin(saveCredentials);
        toggleFields(saveCredentials);
    }

    private void toggleFields(boolean save){
        if(usernameField.getText().isEmpty() || !save)
            usernameField.setDisable(false);
        else
            usernameField.setDisable(true);

        if(passwordField.getText().isEmpty() || !save)
            passwordField.setDisable(false);
        else
            passwordField.setDisable(true);

        if(tokenField.getText().isEmpty() || !save)
            tokenField.setDisable(false);
        else
            tokenField.setDisable(true);

        getTokenBtn.setDisable(false);
    }

    @FXML
    void onGoogleAuthBtnClicked(ActionEvent event) {
        AccountManager.logOnGoogleAuth(tokenField.getText());
        rootScene.getWindow().hide();
        openMainWindow();
    }

    @FXML
    void onPTCLoginBtnClicked(ActionEvent event) {
        AccountManager.logOnPTC(usernameField.getText(), passwordField.getText());
        rootScene.getWindow().hide();
        openMainWindow();
    }

    void openMainWindow() {
        new MainWindowController();
        BlossomsPoGoManager.getPrimaryStage().show();
    }
}
