package me.corriekay.pokegoutil.windows;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.helpers.FileHelper;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.ui.Console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;

@SuppressWarnings("serial")
public class PokemonGoMainWindow extends JFrame {

    public static PokemonGoMainWindow window = null;
    private final PokemonGo go;
    private final PlayerProfile pp;
    private final JTabbedPane tab = new JTabbedPane();
    private ConfigNew config = ConfigNew.getConfig();

    public PokemonGoMainWindow(PokemonGo pkmngo, Console console) {
        go = pkmngo;
        pp = go.getPlayerProfile();

        console.clearAllLines();

        System.out.println(String.format("Successfully logged in. Welcome, %s.", pp.getPlayerData().getUsername()));
        System.out.println(String.format("Stats: Lvl %d %s player.",
                pp.getStats().getLevel(),
                PokemonUtils.convertTeamColorToName(pp.getPlayerData().getTeamValue())));

        String msg = String.format("Pokédex - Types Caught: %d, ", pp.getStats().getUniquePokedexEntries());
        msg += String.format("Total Pokémon Caught: %d, ", pp.getStats().getPokemonsCaptured());
        msg += String.format("Total Current Pokémon: %d (+%d Eggs)",
                go.getInventories().getPokebank().getPokemons().size(),
                go.getInventories().getHatchery().getEggs().size());
        System.out.println(msg);

        refreshTitle();
        setLayout(new BorderLayout());
        setIconImage(FileHelper.loadImage("icon/PokeBall-icon.png"));
        setBounds(0, 0, config.getInt(ConfigKey.WINDOW_WIDTH), config.getInt(ConfigKey.WINDOW_HEIGHT));

        // add EventHandler to save new window size and position to
        // config for the app to remember over restarts
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                JFrame w = (JFrame) e.getComponent();
                config.setInt(ConfigKey.WINDOW_WIDTH, w.getWidth());
                config.setInt(ConfigKey.WINDOW_HEIGHT, w.getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                JFrame w = (JFrame) e.getComponent();
                config.setInt(ConfigKey.WINDOW_POS_X, w.getX());
                config.setInt(ConfigKey.WINDOW_POS_Y, w.getY());
            }
        });

        Point pt = UIHelper.getLocationMidScreen(this);
        int posx = config.getInt(ConfigKey.WINDOW_POS_X, pt.x);
        int posy = config.getInt(ConfigKey.WINDOW_POS_Y, pt.y);
        setLocation(posx, posy);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PokemonTab pokemonTab = new PokemonTab(go);
        setJMenuBar(new MenuBar(go, pokemonTab));
        tab.add("Pokémon", pokemonTab);

        add(tab, BorderLayout.CENTER);

        console.setVisible(false);
        console.jsp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
        add(console.jsp, BorderLayout.SOUTH);

        window = this;
    }

    public PokemonGo getPoGo() {
        return go;
    }

    public void refreshTitle() {

        NumberFormat f = NumberFormat.getInstance();
        setTitle(String.format("%s - Stardust: %s - Blossom's Pokémon Go Manager",
                pp.getPlayerData().getUsername(),
                f.format(pp.getCurrency(PlayerProfile.Currency.STARDUST))));
    }

    public void start() {
        // pack();
        setVisible(true);
    }
}
