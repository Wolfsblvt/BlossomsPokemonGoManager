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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.controllers.AccountController;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;

public class ChooseGuiWindowController extends Pane{
	
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
    void onNewGuiBtnClicked(ActionEvent event){
    	oldGuiBtn.getScene().getWindow().hide();
    	
    	//@SuppressWarnings("static-access")
		Parent root;
		try {
			root = (Parent) FXMLLoader.load(ChooseGuiWindowController.class.getClassLoader().getResource("resources/layout/Login.fxml"));
		} catch (IOException e) {
			System.err.println("Error loading resource: resources/layout/Login.fxml" );
			return;
		}
		Scene scene = new Scene(root);
		
		Stage mainWindow = new Stage();
		
		mainWindow.getIcons().add(new Image("/resources/icon/PokeBall-icon.png"));
		mainWindow.setTitle("Login");
		mainWindow.initStyle(StageStyle.UTILITY);
		
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
        		Utilities.setNativeLookAndFeel();
        		console = new Console("Console", 0, 0, true);
        		console.setVisible(false);
        		AccountController.initialize(console);
				AccountController.logOn();
				
            }
        });    	    	
    }
}
