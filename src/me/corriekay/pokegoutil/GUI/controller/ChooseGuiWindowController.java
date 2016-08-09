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
	
	private ClassLoader classLoader;
	
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
    	classLoader = getClass().getClassLoader();
    }

    @FXML
    void onNewGuiBtnClicked(ActionEvent event){
    	oldGuiBtn.getScene().getWindow().hide();
    	
		Parent root;
		try {
			root = (Parent) FXMLLoader.load(classLoader.getResource("layout/Login.fxml"));
		} catch (IOException e) {
			System.err.println("Error loading resource: layout/Login.fxml" );
			return;
		}
		Scene scene = new Scene(root);
		
		Stage mainWindow = new Stage();
		
		URL image = classLoader.getResource("icon/PokeBall-icon.png");
		mainWindow.getIcons().add(new Image(image.toExternalForm()));
		mainWindow.setTitle("Login");
		mainWindow.initStyle(StageStyle.UTILITY);
		mainWindow.setResizable(false);
		
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
