package me.corriekay.pokegoutil.windows;

import javax.swing.*;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile.Currency;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
	
	private final PokemonGo go;
	
	public MenuBar(PokemonGo go) {
		this.go = go;
		
		JMenu file, help;
		
		//File
		file = new JMenu("File");
		
		JMenuItem trainerStats = new JMenuItem("View Trainer Stats");
			trainerStats.addActionListener(al->{
				try {
					displayTrainerStats();
				} catch(Exception e) {e.printStackTrace();}
			});
		
		
		file.add(trainerStats);
		
		add(file);
		
		help = new JMenu("Help");
		
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(l->JOptionPane.showMessageDialog(null, "Author: Corrie 'Blossom' Kay\n\nThis work is protected under the\nCreative Commons Attribution-\nNonCommercial-ShareAlike 4.0\nInternational license, which can\nbe found here:\nhttps://creativecommons.org/\nlicenses/by-nc-sa/4.0/\n\nThanks to Grover for providing\nsuch a great API.\n\nThanks for Draseart for\nthe icon art.", "About Blossom's Pokemon Go Manager", JOptionPane.PLAIN_MESSAGE));
		
		help.add(about);
		
		add(help);
	}
	
	private void displayTrainerStats() throws Exception {
		go.getInventories().updateInventories(true);
		Object[] tstats = {
				"Trainer Name: " + go.getPlayerProfile().getUsername(),
				"Team: " + go.getPlayerProfile().getTeam().toString(),
				"Level: " + go.getPlayerProfile().getStats().getLevel(),
				"XP: " + go.getPlayerProfile().getStats().getExperience() + " (" + go.getPlayerProfile().getStats().getNextLevelXp() + " to next level)",
				"Stardust: " + go.getPlayerProfile().getCurrency(Currency.STARDUST)
		};
		JOptionPane.showMessageDialog(null, tstats, "Trainer Stats", JOptionPane.PLAIN_MESSAGE);
	}
	
}
