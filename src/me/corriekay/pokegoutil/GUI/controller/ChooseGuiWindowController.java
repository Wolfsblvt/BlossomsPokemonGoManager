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
    private URL icon;

	@FXML
    private Button oldGuiBtn;

    @FXML
    private Button newGuiBtn;
	
	public URL getIcon(){
		return icon;
	}
	
	public URL getLocation(){
		return location;
	}
    
    public ChooseGuiWindowController(){
    }
    
    @FXML
    void initialize() {
    }

    @FXML
    void onNewGuiBtnClicked(ActionEvent event){
    	newGuiBtn.getScene().getWindow().hide();	
    	ClassLoader classLoader = getClass().getClassLoader();
    	Parent login = new LoginController();		
		try {
			login = (Parent) FXMLLoader.load(classLoader.getResource("layout/Login.fxml"));
		} catch (IOException e) {
			System.err.println("Problem loading .fxml file: " + e.toString());
			return;
		}
    	
		Stage loginWindow = new Stage();		
		URL icon = classLoader.getResource("icon/PokeBall-icon.png");
		loginWindow.getIcons().add(new Image(icon.toExternalForm()));
		loginWindow.setTitle("Login");
		loginWindow.initStyle(StageStyle.UTILITY);
		loginWindow.setResizable(false);		
		loginWindow.setScene(new Scene(login));    	  
		loginWindow.show();
    }

    @FXML
    void onOldGuiBtnClicked(ActionEvent event) {
    	oldGuiBtn.getScene().getWindow().hide();
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
