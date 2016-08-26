package me.corriekay.pokegoutil.GUI.controller;

import com.pokegoapi.api.inventory.Stats;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.DATA.managers.AccountManager;
import me.corriekay.pokegoutil.DATA.managers.InventoryManager;
import me.corriekay.pokegoutil.DATA.managers.PokemonBagManager;
import me.corriekay.pokegoutil.DATA.managers.ProfileManager;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;

import static POGOProtos.Data.PlayerDataOuterClass.PlayerData;

public class MainWindowController extends BorderPane {

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
    private GridPane pokemontable;

    @FXML
    private PokemonTableController pokemontableController;

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

        pokemontableController = new PokemonTableController(pokemontable);
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(rootScene);

        BlossomsPoGoManager.setNewPrimaryStage(stage);
    }

    @FXML
    //TODO fix exceptions
    private void initialize() {
        quitMenuItem.setOnAction(this::onQuitClicked);
        logOffMenuItem.setOnAction(this::onLogOffClicked);

        PlayerProfile pp = ProfileManager.getProfile();
        refreshGUI(pp);
    }

    private void refreshGUI(PlayerProfile pp) {
        boolean done = false;
        if (pp != null) {
            while (!done) {
                done = true;
                try {
                    PlayerData pd = pp.getPlayerData();
                    Stats stats = pp.getStats();


                    Color color = null;
                    switch (pd.getTeam().getNumber()) {
                        case 0://noteam
                            color = Color.rgb(160, 160, 160, 0.99);
                            break;
                        case 1://blue
                            color = Color.rgb(0, 0, 255, 0.99);
                            break;
                        case 2://red
                            color = Color.rgb(204, 0, 0, 0.99);
                            break;
                        case 3://yellow
                            color = Color.rgb(255, 255, 0, 0.99);
                            break;
                    }
                    if (color != null) {
                        int width = (int) teamIcon.getFitWidth();
                        int height = (int) teamIcon.getFitHeight();
                        WritableImage dest = new WritableImage(width, height);
                        PixelWriter writer = dest.getPixelWriter();
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                writer.setColor(x, y, color);
                            }
                        }
                        teamIcon.setImage(dest);
                    }
                    playerNameLabel.setText(pd.getUsername() + " ");
                    playerLvl.setText("Lvl: " + Integer.toString(stats.getLevel()));
                    NumberFormat f = NumberFormat.getInstance();
                    playerStardustLbl.setText(f.format(pp.getCurrency(PlayerProfile.Currency.STARDUST))
                            + " Stardust");
                    nbPkmInBagsLbl.setText(Integer.toString(PokemonBagManager.getNbPokemon())
                            + "/" + Integer.toString(pp.getPlayerData().getMaxPokemonStorage())
                            + " Pokémon");
                    nbItemsBagsLbl.setText(Integer.toString(InventoryManager.getInventories().getItemBag().getItemsCount())
                            + "/" + Integer.toString(pp.getPlayerData().getMaxItemStorage())
                            + " Items");
                } catch (Exception e) {
                    System.out.println("bad stuff happened:" + e.toString());
                    done = false;
                }
            }
        }
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
    void onQuitClicked(ActionEvent event) {
        //TODO Kill in a more humane way, maybe...
        System.exit(0);
    }

    @FXML
    void onSettingsClicked(ActionEvent event) {

    }

}
