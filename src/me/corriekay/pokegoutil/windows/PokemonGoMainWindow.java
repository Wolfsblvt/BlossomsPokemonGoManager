package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;

import me.corriekay.pokegoutil.data.managers.GlobalSettingsController;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.helpers.FileHelper;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;
import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.ui.SmartScroller;
import me.corriekay.pokegoutil.utils.version.Updater;


@SuppressWarnings("serial")
public class PokemonGoMainWindow extends JFrame {

    public JTextArea textArea = new JTextArea();
    public JScrollPane jsp;

    public static PokemonGoMainWindow instance = null;

    private final PokemonGo go;
    private final PlayerProfile pp;
    private final JTabbedPane tab = new JTabbedPane();
    private final ConfigNew config = ConfigNew.getConfig();

    private static final int textAreaHeight = 150;

    private final GlobalSettingsController globalSettings = GlobalSettingsController.getGlobalSettingsController();

    /**
     * Instantiate a PokemonGoMainWindow instance.
     *
     * @param pkmngo      PokemonGo instance
     * @param smartscroll use smart scroll
     */
    public PokemonGoMainWindow(final PokemonGo pkmngo, final boolean smartscroll) {
        super();

        instance = this;
        go = pkmngo;
        pp = go.getPlayerProfile();

        setLayout(new BorderLayout());
        setIconImage(FileHelper.loadImage("icon/PokeBall-icon.png"));
        setBounds(0, 0, config.getInt(ConfigKey.WINDOW_WIDTH), config.getInt(ConfigKey.WINDOW_HEIGHT));

        // add EventHandler to save new window size and position to
        // config for the app to remember over restarts
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                final JFrame w = (JFrame) e.getComponent();
                config.setInt(ConfigKey.WINDOW_WIDTH, w.getWidth());
                config.setInt(ConfigKey.WINDOW_HEIGHT, w.getHeight());
            }

            @Override
            public void componentMoved(final ComponentEvent e) {
                final JFrame w = (JFrame) e.getComponent();
                config.setInt(ConfigKey.WINDOW_POS_X, w.getX());
                config.setInt(ConfigKey.WINDOW_POS_Y, w.getY());
            }
        });

        final Point pt = UIHelper.getLocationMidScreen(this);
        final int posx = config.getInt(ConfigKey.WINDOW_POS_X, pt.x);
        final int posy = config.getInt(ConfigKey.WINDOW_POS_Y, pt.y);
        setLocation(posx, posy);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final PokemonTab pokemonTab = new PokemonTab(go);
        setJMenuBar(new MenuBar(go, pokemonTab));
        tab.add("Pokémon", pokemonTab);

        add(tab, BorderLayout.CENTER);
        initializeConsole(smartscroll);

        try {
            System.out.println(String.format("Successfully logged in. Welcome, %s.", pp.getPlayerData().getUsername()));
            System.out.println(String.format("Hash Version: %s", Integer.toString(go.getHashProvider().getHashVersion()).replaceAll("(\\d\\d)(\\d)(\\d)", "0.$1.$2$3")));
            System.out.println(String.format("Stats: Lvl %d %s player.",
                pp.getStats().getLevel(),
                PokemonUtils.convertTeamColorToName(pp.getPlayerData().getTeamValue())));

            String msg = String.format("Pokédex - Types Caught: %d, ", pp.getStats().getUniquePokedexEntries());
            msg += String.format("Total Pokémon Caught: %d, ", pp.getStats().getPokemonsCaptured());
            msg += String.format("Total Current Pokémon: %d (+%d Eggs)",
                go.getInventories().getPokebank().getPokemons().size(),
                go.getInventories().getHatchery().getEggs().size());
            System.out.println(msg);

        } catch (final NumberFormatException e) {
            System.out.println("Error retrieving trainer data.");
            ConsolePrintStream.printException(e);
        }
        refreshTitle();

        // Check for new version
        final Updater updater = Updater.getUpdater();
        updater.checkForNewVersion();

        List<String> errors = pokemonTab.getColumnErrors();
        if (!errors.isEmpty()) {
            System.out.println("WARNING: Some column names from config could not be recognized!");
            String configString = ConfigNew.getConfig().getString(ConfigKey.POKEMONTABLE_COLUMNORDER);
            System.out.println("Config string is: '" + configString + "'");
            for (String wrongColumn : errors) {
                System.out.println("  Name not recognized: '" + wrongColumn+"'");
            }
            System.out.println("Existing columns for which no match in configuation was found will appear at the end of the table.");
            JOptionPane.showMessageDialog(this, "Some columns from configuration file (column order) were not recognized."
                    + StringLiterals.NEWLINE + "See console log for details!" + StringLiterals.NEWLINE
                    + "(The main window will appear after you close this dialog box.)",
                    "Configuration problem", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Initialize the text area and set it to append the logs.
     *
     * @param smartscroll use smart scroll
     */
    private void initializeConsole(final boolean smartscroll) {
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        jsp = new JScrollPane(textArea);
        jsp.setPreferredSize(new Dimension(Integer.MAX_VALUE, textAreaHeight));
        if (smartscroll) {
            new SmartScroller(jsp);
        }
        add(jsp, BorderLayout.SOUTH);

        globalSettings.getLogController().setTextArea(textArea);

    }

    /**
     * Returns the current instance of the PokemonGoMainWindow.
     *
     * @return instance of the PokemonGoMainWindow
     */
    public static PokemonGoMainWindow getInstance() {
        return instance;
    }

    public PokemonGo getPoGo() {
        return go;
    }

    public void refreshTitle() {
        try {
            final NumberFormat f = NumberFormat.getInstance();
            setTitle(String.format("%s - Pokemon: %s - Stardust: %s - Blossom's Pokémon Go Manager",
                pp.getPlayerData().getUsername(),
                f.format(go.getInventories().getPokebank().getPokemons().size()),
                f.format(pp.getCurrency(PlayerProfile.Currency.STARDUST))));
        } catch (final NumberFormatException e) {
            setTitle("Blossom's Pokémon Go Manager");
        }
    }

    public void start() {
        setVisible(true);
    }
}
