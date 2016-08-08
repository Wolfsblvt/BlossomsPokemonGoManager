package me.corriekay.pokegoutil.GUI.controller;

import java.io.IOException;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.controllers.AccountController;

public class LoginController extends StackPane{

    @FXML
    private TextField user;

    @FXML
    private TextField password;

    @FXML
    private Button ptcLoginBtn;

    @FXML
    private Button googleAuthBtn;
    
    @FXML
    private void initialize(){
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
    	ptcLoginBtn.getScene().getWindow().hide();    	
		Parent root;
		try {
			root = (Parent) FXMLLoader.load(ChooseGuiWindowController.class.getClassLoader().getResource("resources/layout/MainWindow.fxml"));
		} catch (IOException e1) {
			System.err.println("Error loading resource: resources/layout/MainWindow.fxml");
			return;
		}
		Scene scene = new Scene(root);
		
		Stage mainWindow = new Stage();
		
		mainWindow.getIcons().add(new Image("resources/icon/PokeBall-icon.png"));
		try {
			NumberFormat f = NumberFormat.getInstance();
			PlayerProfile pp = AccountController.getPlayerProfile();
			mainWindow.setTitle(String.format("%s - Stardust: %s - Blossom's Pokémon Go Manager", pp.getPlayerData().getUsername(),
					f.format(pp.getCurrency(PlayerProfile.Currency.STARDUST))));
		} catch (InvalidCurrencyException | LoginFailedException | RemoteServerException | NullPointerException e) {
			mainWindow.setTitle("Blossom's Pokémon Go Manager");
		}
		
		mainWindow.initStyle(StageStyle.UNIFIED);
		
		mainWindow.setScene(scene);    	  
		mainWindow.show();
    }
}
