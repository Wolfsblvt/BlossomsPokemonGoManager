package me.corriekay.pokegoutil.windows;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.ui.Console;
import me.corriekay.pokegoutil.utils.helpers.FileHelper;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;

@SuppressWarnings("serial")
public class PokemonGoMainWindow extends JFrame {

    private final PokemonGo go;
    private final PlayerProfile pp;
    public static PokemonGoMainWindow window = null;
    private Config config = Config.getConfig();

    public PokemonGo getPoGo() {
        return go;
    }

    private final JTabbedPane tab = new JTabbedPane();

    public PokemonGoMainWindow(PokemonGo pkmngo, Console console) {
        go = pkmngo;
        pp = go.getPlayerProfile();

        console.clearAllLines();
        try {

            System.out.println("Successfully logged in. Welcome, " + pp.getPlayerData().getUsername() + ".");
            System.out.println("Stats: Lvl " + pp.getStats().getLevel() + " "
                    + PokemonUtils.convertTeamColorToName(pp.getPlayerData().getTeamValue()) + " player.");
            System.out.println("Pokédex - Types Caught: " + pp.getStats().getUniquePokedexEntries()
                    + ", Total Pokémon Caught: " + pp.getStats().getPokemonsCaptured() + ", Total Current Pokémon: "
                    + go.getInventories().getPokebank().getPokemons().size() + " (+" + go.getInventories().getHatchery().getEggs().size() + " Eggs)");
        } catch (RemoteServerException | LoginFailedException e) {
            // System.out.println("Unable to login!");
            // e.printStackTrace();
        }
        setLayout(new BorderLayout());
        refreshTitle();
        setIconImage(FileHelper.loadImage("main/resources/icon/PokeBall-icon.png"));
        setBounds(0, 0, config.getInt("options.window.width", 800), config.getInt("options.window.height", 650));
        // add EventHandler to save new window size and position to
        // config for the app to remember over restarts
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                JFrame w = (JFrame) e.getComponent();
                config.setInt("options.window.width", w.getWidth());
                config.setInt("options.window.height", w.getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                JFrame w = (JFrame) e.getComponent();
                config.setInt("options.window.posx", w.getX());
                config.setInt("options.window.posy", w.getY());
            }
        });
        Point pt = UIHelper.getLocationMidScreen(this);
        int posx = config.getInt("options.window.posx", pt.x);
        int posy = config.getInt("options.window.posy", pt.y);
        setLocation(posx, posy);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(new MenuBar(go));
        tab.add("Pokémon", new PokemonTab(go));

        add(tab, BorderLayout.CENTER);

        console.setVisible(false);
        console.jsp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
        add(console.jsp, BorderLayout.SOUTH);

        window = this;
    }

    public void refreshTitle() {
        try {
            NumberFormat f = NumberFormat.getInstance();
            setTitle(String.format("%s - Stardust: %s - Blossom's Pokémon Go Manager", pp.getPlayerData().getUsername(),
                    f.format(pp.getCurrency(PlayerProfile.Currency.STARDUST))));
        } catch (InvalidCurrencyException | LoginFailedException | RemoteServerException e) {
            setTitle("Blossom's Pokémon Go Manager");
        }
    }

    public void start() {
        // pack();
        setVisible(true);
    }
}
