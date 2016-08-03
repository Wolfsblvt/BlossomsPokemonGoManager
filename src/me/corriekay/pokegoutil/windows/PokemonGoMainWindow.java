package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;

@SuppressWarnings("serial")
public class PokemonGoMainWindow extends JFrame {

	private final PokemonGo go;
	private final PlayerProfile p;
	public static PokemonGoMainWindow window = null;

	public PokemonGo getPoGo() { return go; }

	private final JTabbedPane tab = new JTabbedPane();

	public PokemonGoMainWindow(PokemonGo pkmngo, Console console){
		go = pkmngo;
		p = go.getPlayerProfile();

		console.clearAllLines();
		System.out.println("Successfully logged in. Welcome, " + p.getUsername() + ".");
		System.out.println("Stats: Lvl " + p.getStats().getLevel() + " " + StringUtils.capitalize(p.getTeam().toString().toLowerCase().replaceAll("team_", "") + " player."));
		System.out.println("Pokedex - Types Caught: " + p.getStats().getUniquePokedexEntries() + ", Total Pokemon Caught: " + p.getStats().getPokemonsCaptured() + ", Total Current Pokemon: " + go.getInventories().getPokebank().getPokemons().size());
		setLayout(new BorderLayout());
		refreshTitle();
		setIconImage(Utilities.loadImage("PokeBall-icon.png"));
		setBounds(0, 0, 800, 650);
		Utilities.setLocationMidScreen(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(new MenuBar(go));
		tab.add("Pokemon", new PokemonTab(go));

		add(tab, BorderLayout.CENTER);

		console.setVisible(false);
		console.jsp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
		add(console.jsp, BorderLayout.SOUTH);

		window = this;
	}

	public void refreshTitle() {
		try {
			NumberFormat f = NumberFormat.getInstance();
			setTitle(String.format("%s - Stardust: %s - Blossom's Pokemon Go Manager", p.getUsername(), f.format(p.getCurrency(PlayerProfile.Currency.STARDUST))));
		} catch (InvalidCurrencyException e) {
			setTitle("Blossom's Pokemon Go Manager");
		}
	}

	public void start() {
		//pack();
		setVisible(true);
	}
}