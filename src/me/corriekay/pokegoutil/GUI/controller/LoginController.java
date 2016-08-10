package me.corriekay.pokegoutil.GUI.controller;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;

import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.controllers.AccountController;

public class LoginController extends StackPane {

    @FXML
    private TextField user;

    @FXML
    private TextField password;

    @FXML
    private Button ptcLoginBtn;

    @FXML
    private Button googleAuthBtn;

    @FXML
    private CheckBox autoRelogChkbx;

    public LoginController() {
    }

    @FXML
    private void initialize() {
        AccountController.initialize();
    }

    @FXML
    void onGoogleAuthBtnClicked(ActionEvent event) {
        //TODO actually do the login
        //AccountController.logOnGoogleAuth();
        openMainWindow();
    }

    @FXML
    void onPTCLoginBtnClicked(ActionEvent event) {
        //TODO actually do the login
        //AccountController.logOnPTC(user.getText(), password.getText());
        openMainWindow();
    }

    void openMainWindow() {
        googleAuthBtn.getScene().getWindow().hide();
        ClassLoader classLoader = getClass().getClassLoader();
        Parent mainLayout;
        try {
            mainLayout = (Parent) FXMLLoader.load(classLoader.getResource("layout/MainWindow.fxml"));
        } catch (IOException e) {
            System.err.println("Problem loading .fxml file: " + e.toString());
            return;
        }
        Stage mainWindow = new Stage();
        URL icon = classLoader.getResource("icon/PokeBall-icon.png");
        mainWindow.getIcons().add(new Image(icon.toExternalForm()));
        try {
            NumberFormat f = NumberFormat.getInstance();
            PlayerProfile pp = AccountController.getPlayerProfile();
            mainWindow.setTitle(String.format("%s - Stardust: %s - Blossom's Pokémon Go Manager", pp.getPlayerData().getUsername(),
                    f.format(pp.getCurrency(PlayerProfile.Currency.STARDUST))));
        } catch (InvalidCurrencyException | LoginFailedException | RemoteServerException | NullPointerException e) {
            mainWindow.setTitle("Blossom's Pokémon Go Manager");
        }

        mainWindow.initStyle(StageStyle.DECORATED);
        mainWindow.setResizable(false);
        mainWindow.setScene(new Scene(mainLayout));
        mainWindow.show();
    }
}
