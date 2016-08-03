package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;

import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;

@SuppressWarnings("serial")
public class PokemonGoMainWindow extends JFrame {
	
	private final PokemonGo go;
	
	public PokemonGo getPoGo() { return go; }
	
	private final JTabbedPane tab = new JTabbedPane();
	
	public PokemonGoMainWindow(PokemonGo pkmngo, Console console){
		go = pkmngo;
		PlayerProfile pp = go.getPlayerProfile();
		console.clearAllLines();
		try{
			System.out.println("Successfully logged in. Welcome, " + pp.getPlayerData().getUsername() + ".");
			System.out.println("Stats: Lvl " + pp.getStats().getLevel() + " " + StringUtils.capitalize(pp.getPlayerData().getTeam().toString().toLowerCase().replaceAll("team_", "") + " player."));
			System.out.println("Pokedex - Types Caught: " + pp.getStats().getUniquePokedexEntries() + ", Total Pokemon Caught: " + pp.getStats().getPokemonsCaptured() + ", Total Current Pokemon: " + go.getInventories().getPokebank().getPokemons().size());
		} catch (Exception e) {e.printStackTrace();}
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
		//pack();
		setVisible(true);
	}
}