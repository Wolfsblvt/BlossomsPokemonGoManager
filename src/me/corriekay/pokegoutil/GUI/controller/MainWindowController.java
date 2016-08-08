package me.corriekay.pokegoutil.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class MainWindowController extends Pane{

	@FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem logOffMenuItem;

    @FXML
    private MenuItem quitMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private HBox PlayerInfo;

    @FXML
    private ImageView teamIcon;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Label playerLvl;

    @FXML
    private Label playerStardustLbl;

    @FXML
    private Label nbPkmInBagsLbl;

    @FXML
    private Label nbItemsBagsLbl;
    
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
