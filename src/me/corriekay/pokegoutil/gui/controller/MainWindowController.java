package me.corriekay.pokegoutil.gui.controller;

import java.text.NumberFormat;

import com.pokegoapi.api.inventory.Stats;
import com.pokegoapi.api.player.PlayerProfile;

import POGOProtos.Data.PlayerDataOuterClass.PlayerData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.data.managers.AccountManager;
import me.corriekay.pokegoutil.data.managers.InventoryManager;
import me.corriekay.pokegoutil.data.managers.PokemonBagManager;
import me.corriekay.pokegoutil.data.managers.ProfileManager;

/**
 * The MainWindowController is the main view of the program after logging in.
 */
public class MainWindowController extends BaseController<BorderPane> {

    private static final String SLASH = "/";

    private final AccountManager accountManager = AccountManager.getInstance();

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
    private final PokemonTableController pokemontableController;

    public MainWindowController() {
        super();
        initializeController();
        pokemontableController = new PokemonTableController(pokemontable);
    }

    @Override
    public String getFxmlLayout() {
        return "layout/MainWindow.fxml";
    }

    @FXML
    // TODO fix exceptions
    private void initialize() {
        quitMenuItem.setOnAction(this::onQuitClicked);
        logOffMenuItem.setOnAction(this::onLogOffClicked);

        final PlayerProfile pp = ProfileManager.getProfile();
        refreshGUI(pp);
    }

    @FXML
    void onAboutClicked(final ActionEvent event) {
        // Not done
    }

    @FXML
    void onLogOffClicked(final ActionEvent event) {
        new LoginController();
        BlossomsPoGoManager.getPrimaryStage().show();
    }

    @FXML
    void onQuitClicked(final ActionEvent event) {
        // TODO Kill in a more humane way, maybe...
        System.exit(0);
    }

    @FXML
    void onSettingsClicked(final ActionEvent event) {
        // Not done
    }

    /**
     * The player profile will be used to refresh the interface.
     *
     * @param pp the player profile
     */
    private void refreshGUI(final PlayerProfile pp) {
        boolean done = false;
        if (pp != null) {
            while (!done) {
                done = true;
                final PlayerData pd = pp.getPlayerData();
                final Stats stats = pp.getStats();

                Color color = null;
                switch (pd.getTeam().getNumber()) {
                    case 0 :// noteam
                        color = Color.rgb(160, 160, 160, 0.99);
                        break;
                    case 1 :// blue
                        color = Color.rgb(0, 0, 255, 0.99);
                        break;
                    case 2 :// red
                        color = Color.rgb(204, 0, 0, 0.99);
                        break;
                    case 3 :// yellow
                        color = Color.rgb(255, 255, 0, 0.99);
                        break;
                }
                if (color != null) {
                    final int width = (int) teamIcon.getFitWidth();
                    final int height = (int) teamIcon.getFitHeight();
                    final WritableImage dest = new WritableImage(width, height);
                    final PixelWriter writer = dest.getPixelWriter();
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            writer.setColor(x, y, color);
                        }
                    }
                    teamIcon.setImage(dest);
                }
                playerNameLabel.setText(pd.getUsername() + " ");
                playerLvl.setText("Lvl: " + Integer.toString(stats.getLevel()));
                final NumberFormat f = NumberFormat.getInstance();
                playerStardustLbl.setText(f.format(
                        pp.getCurrency(PlayerProfile.Currency.STARDUST))
                        + " Stardust");
                nbPkmInBagsLbl.setText(Integer.toString(PokemonBagManager.getNbPokemon())
                        + SLASH
                        + Integer.toString(pp.getPlayerData().getMaxPokemonStorage())
                        + " Pokémon");
                nbItemsBagsLbl.setText(
                        Integer.toString(InventoryManager.getInventories().getItemBag().getItemsCount())
                        + SLASH
                        + Integer.toString(pp.getPlayerData().getMaxItemStorage())
                        + " Items");
            }
        }
    }

    @Override
    public void setGuiControllerSettings() {
        try {
            final NumberFormat f = NumberFormat.getInstance();
            final PlayerProfile pp = accountManager.getPlayerProfile();
            guiControllerSettings.setTitle(String.format(
                    "%s - Stardust: %s - Blossom's Pokémon Go Manager",
                    pp.getPlayerData().getUsername(),
                    f.format(pp.getCurrency(PlayerProfile.Currency.STARDUST))));
        } catch (NumberFormatException | NullPointerException e) {
            guiControllerSettings.setTitle("Blossom's Pokémon Go Manager");
        }

        guiControllerSettings.setStageStyle(StageStyle.DECORATED);
        guiControllerSettings.setMaximized(true);
    }
}
