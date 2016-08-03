package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.api.PokemonGo;

import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;

@SuppressWarnings("serial")
public class PokemonGoMainWindow extends JFrame {
	
	private final PokemonGo go;
	
	public PokemonGo getPoGo() { return go; }
	
	private final JTabbedPane tab = new JTabbedPane();
	
	public PokemonGoMainWindow(PokemonGo pkmngo, Console console){
		go = pkmngo;
		console.clearAllLines();
		System.out.println("Successfully logged in. Welcome, " + go.getPlayerProfile().getUsername() + ".");
		System.out.println("Stats: Lvl " + go.getPlayerProfile().getStats().getLevel() + " " + StringUtils.capitalize(go.getPlayerProfile().getTeam().toString().toLowerCase().replaceAll("team_", "") + " player."));
		System.out.println("Pokédex - Types Caught: " + go.getPlayerProfile().getStats().getUniquePokedexEntries() + ", Total Pokémon Caught: " + go.getPlayerProfile().getStats().getPokemonsCaptured() + ", Total Current Pokémon: " + go.getInventories().getPokebank().getPokemons().size());
		setLayout(new BorderLayout());
		setTitle("Blossom's Pokémon Go Manager");
		setIconImage(Utilities.loadImage("PokeBall-icon.png"));
		setBounds(0, 0, 600, 550);
		Utilities.setLocationMidScreen(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(new MenuBar(go));
		tab.add("Pokémon", new PokemonTab(go));
		
		add(tab, BorderLayout.CENTER);
		
		console.setVisible(false);
		console.jsp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
		add(console.jsp, BorderLayout.SOUTH);
	}
	
	public void start() {
		//pack();
		setVisible(true);
	}
}