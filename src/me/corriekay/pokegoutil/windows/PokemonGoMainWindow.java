package me.corriekay.pokegoutil.windows;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

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
		try{
			System.out.println("Successfully logged in. Welcome, " + p.getPlayerData().getUsername() + ".");
			System.out.println("Stats: Lvl " + p.getStats().getLevel() + " " + StringUtils.capitalize(p.getPlayerData().getTeam().toString().toLowerCase().replaceAll("team_", "") + " player."));
			System.out.println("Pokedex - Types Caught: " + p.getStats().getUniquePokedexEntries() + ", Total Pokemon Caught: " + p.getStats().getPokemonsCaptured() + ", Total Current Pokemon: " + go.getInventories().getPokebank().getPokemons().size());
		} catch (Exception e) {e.printStackTrace();}
		setLayout(new BorderLayout());
		refreshTitle();
		setIconImage(Utilities.loadImage("PokeBall-icon.png"));
		setBounds(0, 0, 1000, 550);
		Utilities.setLocationMidScreen(this);
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
			setTitle(String.format("%s - Stardust: %s - Blossom's Pokémon Go Manager", p.getPlayerData().getUsername(), f.format(p.getCurrency(PlayerProfile.Currency.STARDUST))));
		} catch (InvalidCurrencyException | LoginFailedException | RemoteServerException e) {
			setTitle("Blossom's Pokémon Go Manager");
		}
	}

	public void start() {
		//pack();
		setVisible(true);
	}
}
