package me.corriekay.pokegoutil.GUI.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class MainWindowController extends VBox{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem logOffMenuItem;

    @FXML
    private MenuItem quitMenuItem;
    
    @FXML
    void onAboutClicked(ActionEvent event) {

    }

    @FXML
    void onLogOffClicked(ActionEvent event) {

    }

    @FXML
    void onQuitClicked(ActionEvent event) {
    	//TODO Kill in a more humane way, maybe...
    	System.exit(0);
    }

    @FXML
    void onSettingsClicked(ActionEvent event) {

    }

}
