package me.corriekay.pokegoutil.windows;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.Stats;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.DATA.controllers.AccountController;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

import javax.swing.*;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

    private final PokemonGo go;
    private ConfigNew config = ConfigNew.getConfig();

    public MenuBar(PokemonGo go) {
        this.go = go;

        JMenu file, settings, help;

        // File menu
        file = new JMenu("File");

        JMenuItem trainerStats = new JMenuItem("View Trainer Stats");
        trainerStats.addActionListener(al -> {
            try {
                displayTrainerStats();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        file.add(trainerStats);

        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(al -> {
            try {
                logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        file.add(logout);

        add(file);

        // Settings menu
        settings = new JMenu("Settings");

        JCheckBoxMenuItem tAfterE = new JCheckBoxMenuItem("Transfer After Evolve");
        tAfterE.setSelected(config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE));
        tAfterE.addItemListener(e -> config.setBool(ConfigKey.TRANSFER_AFTER_EVOLVE, tAfterE.isSelected()));
        settings.add(tAfterE);

        JCheckBoxMenuItem doNotShowBulkPopup = new JCheckBoxMenuItem("Show Bulk Completion");
        doNotShowBulkPopup.setSelected(config.getBool(ConfigKey.SHOW_BULK_POPUP));
        doNotShowBulkPopup
                .addItemListener(e -> config.setBool(ConfigKey.SHOW_BULK_POPUP, doNotShowBulkPopup.isSelected()));
        settings.add(doNotShowBulkPopup);

        add(settings);

        // Help menu
        help = new JMenu("Help");

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(l -> JOptionPane.showMessageDialog(null,
                "Version: " + BlossomsPoGoManager.VERSION + "\n\nAuthor: Corrie 'Blossom' Kay"
                        + "\n\nThis work is protected under the"
                        + "\nCreative Commons Attribution-"
                        + "\nNonCommercial-ShareAlike 4.0"
                        + "\nInternational license, which can"
                        + "\nbe found here:"
                        + "\nhttps://creativecommons.org/"
                        + "\nlicenses/by-nc-sa/4.0/"
                        + "\n\nThanks to Grover for providing"
                        + "\nsuch a great API." + "\n\nThanks for Draseart for"
                        + "\nthe icon art.",
                "About Blossom's Pokémon Go Manager", JOptionPane.PLAIN_MESSAGE));
        help.add(about);

        add(help);
    }

    private void logout() throws Exception {
        AccountController.logOff();
    }

    private void displayTrainerStats() throws Exception {
        go.getInventories().updateInventories(true);
        PlayerProfile pp = go.getPlayerProfile();
        Stats stats = pp.getStats();
        Object[] tstats = {"Trainer Name: " + pp.getPlayerData().getUsername(),
                "Team: " + PokemonUtils.convertTeamColorToName(pp.getPlayerData().getTeamValue()),
                "Level: " + stats.getLevel(), "XP: " + stats.getExperience() + " ("
                + (stats.getNextLevelXp() - stats.getExperience()) + " to next level)",
                "Stardust: " + pp.getCurrency(Currency.STARDUST)};
        JOptionPane.showMessageDialog(null, tstats, "Trainer Stats", JOptionPane.PLAIN_MESSAGE);
    }

}
