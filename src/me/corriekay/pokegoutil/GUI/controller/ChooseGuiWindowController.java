package me.corriekay.pokegoutil.GUI.controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.controllers.AccountController;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;

public class ChooseGuiWindowController {
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button oldGuiBtn;

    @FXML
    private Button newGuiBtn;
    
    @FXML
    void initialize() {
    	
    }

    @FXML
    void onNewGuiBtnClicked(ActionEvent event) throws IOException {
    	oldGuiBtn.getScene().getWindow().hide();
    	
    	Parent root = (Parent) FXMLLoader.load(getClass().getClassLoader().getResource("me/corriekay/pokegoutil/GUI/view/MainWindow.fxml"));
		Scene scene = new Scene(root);
		
		Stage mainWindow = new Stage();
		
		mainWindow.getIcons().add(new Image("/res/PokeBall-icon.png"));
		mainWindow.setTitle("Blossom's Pokémon Go Manager");
		mainWindow.initStyle(StageStyle.UNIFIED);
		
		mainWindow.setScene(scene);    	  
		mainWindow.show();
    }

    @FXML
    void onOldGuiBtnClicked(ActionEvent event) {
    	Stage chooseGuiWindow = (Stage) oldGuiBtn.getScene().getWindow();
    	chooseGuiWindow.close();
    	SwingUtilities.invokeLater(new Runnable() {

            private Console console;

			@Override
            public void run() {
            	try {
            		Utilities.setNativeLookAndFeel();
            		console = new Console("Console", 0, 0, true);
            		console.setVisible(false);
            		AccountController.initialize(console);
					AccountController.logOn();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });    	    	
    }
}
