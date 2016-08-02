package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.commons.lang3.StringUtils;

import com.blossompone.utils.Console;
import com.blossompone.utils.Utilities;
import com.pokegoapi.api.PokemonGo;

@SuppressWarnings("serial")
public class PokemonGoMainWindow extends JFrame {

	private final PokemonGo go;

	public PokemonGo getPoGo() {
		return go;
	}

	private final JTabbedPane tab = new JTabbedPane();

	public PokemonGoMainWindow(PokemonGo pkmngo, Console console) {
		go = pkmngo;
		console.clearAllLines();
		System.out.println("Successfully logged in. Welcome, " + go.getPlayerProfile().getUsername() + ".");
		System.out.println("Stats: Lvl " + go.getPlayerProfile().getStats().getLevel() + " " + StringUtils.capitalize(
				go.getPlayerProfile().getTeam().toString().toLowerCase().replaceAll("team_", "") + " player."));
		System.out.println("Pokedex - Types Caught: " + go.getPlayerProfile().getStats().getUniquePokedexEntries()
				+ ", Total Pokemon Caught: " + go.getPlayerProfile().getStats().getPokemonsCaptured()
				+ ", Total Current Pokemon: " + go.getInventories().getPokebank().getPokemons().size());
		setLayout(new BorderLayout());
		setTitle("Blossom's Pokemon Go Manager");
		setIconImage(Utilities.loadImage("PokeBall-icon.png"));
		setBounds(0, 0, 600, 550);
		Utilities.setLocationMidScreen(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(new MenuBar(go));
		tab.add("Pokemon", new PokemonTab(go));

		add(tab, BorderLayout.CENTER);

		console.setVisible(false);
		console.jsp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
		add(console.jsp, BorderLayout.SOUTH);
	}

	public void start() {
		// pack();
		setVisible(true);
	}
}