package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;

@SuppressWarnings("serial")
public class PokemonGoMainWindow extends JFrame {

	private final PokemonGo go;
	private final PlayerProfile p;
	public static PokemonGoMainWindow window = null;
	private Config config = Config.getConfig();

	public PokemonGo getPoGo() {
		return go;
	}

	private final JTabbedPane tab = new JTabbedPane();

	public PokemonGoMainWindow(PokemonGo pkmngo, Console console) {
		go = pkmngo;
		p = go.getPlayerProfile();

		console.clearAllLines();
		try {
			System.out.println("Successfully logged in. Welcome, " + p.getPlayerData().getUsername() + ".");
			System.out.println("Stats: Lvl " + p.getStats().getLevel() + " " + StringUtils.capitalize(
					p.getPlayerData().getTeam().toString().toLowerCase().replaceAll("team_", "") + " player."));
			System.out.println("Pokédex - Types Caught: " + p.getStats().getUniquePokedexEntries()
					+ ", Total Pokémon Caught: " + p.getStats().getPokemonsCaptured() + ", Total Current Pokémon: "
					+ go.getInventories().getPokebank().getPokemons().size());
		} catch (RemoteServerException | LoginFailedException e) {
			System.out.println("Unable to login!");
			e.printStackTrace();
		}
		setLayout(new BorderLayout());
		refreshTitle();
		setIconImage(Utilities.loadImage("resources/icon/PokeBall-icon.png"));
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
		Point pt = Utilities.getLocationMidScreen(this);
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
			setTitle(String.format("%s - Stardust: %s - Blossom's Pokémon Go Manager", p.getPlayerData().getUsername(),
					f.format(p.getCurrency(PlayerProfile.Currency.STARDUST))));
		} catch (InvalidCurrencyException | LoginFailedException | RemoteServerException e) {
			setTitle("Blossom's Pokémon Go Manager");
		}
	}

	public void start() {
		// pack();
		setVisible(true);
	}
}
