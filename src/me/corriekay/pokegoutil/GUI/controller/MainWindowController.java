package me.corriekay.pokegoutil.GUI.controller;

import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.stage.WindowEvent;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.DATA.controllers.AccountManager;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;

public class MainWindowController extends VBox {

    private final String fxmlLayout = "layout/MainWindow.fxml";
    private final URL icon;
    private ClassLoader classLoader = getClass().getClassLoader();
    private Scene rootScene;
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

    public MainWindowController() {
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
        try {
            NumberFormat f = NumberFormat.getInstance();
            PlayerProfile pp = AccountManager.getPlayerProfile();
            stage.setTitle(String.format("%s - Stardust: %s - Blossom's Pokémon Go Manager", pp.getPlayerData().getUsername(),
                    f.format(pp.getCurrency(PlayerProfile.Currency.STARDUST))));
        } catch (InvalidCurrencyException | LoginFailedException | RemoteServerException | NullPointerException e) {
            stage.setTitle("Blossom's Pokémon Go Manager");
        }
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.setScene(rootScene);

        BlossomsPoGoManager.setNewPrimaryStage(stage);
    }

    @FXML
    private void initialize() {
        openGrinderBtn.setOnAction(this::onOpenGrinderClicked);
        quitMenuItem.setOnAction(this::onQuitClicked);
        logOffMenuItem.setOnAction(this::onLogOffClicked);
    }

    @FXML
    void onAboutClicked(ActionEvent event) {

    }

    @FXML
    void onLogOffClicked(ActionEvent event) {
        AccountManager.logOff();
        rootScene.getWindow().hide();
        new LoginController();
        BlossomsPoGoManager.getPrimaryStage().show();
    }

    @FXML
    void onOpenGrinderClicked(ActionEvent event) {
        new LuckyEggGrinderController();
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
