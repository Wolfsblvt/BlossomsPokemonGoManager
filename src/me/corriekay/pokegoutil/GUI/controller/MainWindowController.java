package me.corriekay.pokegoutil.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class MainWindowController extends VBox {

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
    private Button openGrinderBtn;

    @FXML
    void onAboutClicked(ActionEvent event) {

    }

    @FXML
    void onLogOffClicked(ActionEvent event) {

    }

    @FXML
    void onOpenGrinderClicked(ActionEvent event) {
        //openGrinderBtn.getScene().getWindow().hide();
        ClassLoader classLoader = getClass().getClassLoader();
        Parent grinder = new LuckyEggGrinderController();
        try {
            grinder = (Parent) FXMLLoader.load(classLoader.getResource("layout/LuckyEggGrinder.fxml"));
        } catch (IOException e) {
            System.err.println("Problem loading .fxml file: " + e.toString());
            return;
        }

        Stage grinderWindow = new Stage();
        URL icon = classLoader.getResource("icon/PokeBall-icon.png");
        grinderWindow.getIcons().add(new Image(icon.toExternalForm()));
        grinderWindow.setTitle("LuckyEggGrinder");
        grinderWindow.initStyle(StageStyle.UTILITY);
        grinderWindow.setResizable(false);
        grinderWindow.setScene(new Scene(grinder));
        grinderWindow.show();
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
