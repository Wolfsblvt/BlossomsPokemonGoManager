package me.corriekay.pokegoutil.windows;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.Stats;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import me.corriekay.pokegoutil.data.managers.AccountController;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCalculationUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.version.Updater;

import javax.swing.*;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

    private final PokemonGo go;
    private final PokemonTab pokemonTab;
    private ConfigNew config = ConfigNew.getConfig();

    public MenuBar(PokemonGo go, PokemonTab pokemonTab) {
        this.go = go;
        this.pokemonTab = pokemonTab;

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
        tAfterE.addItemListener(e -> {
            config.setBool(ConfigKey.TRANSFER_AFTER_EVOLVE, tAfterE.isSelected());
            SwingUtilities.invokeLater(pokemonTab::refreshList);
        });
        settings.add(tAfterE);

        JCheckBoxMenuItem doNotShowBulkPopup = new JCheckBoxMenuItem("Show Bulk Completion Window");
        doNotShowBulkPopup.setSelected(config.getBool(ConfigKey.SHOW_BULK_POPUP));
        doNotShowBulkPopup.addItemListener(
            e -> config.setBool(ConfigKey.SHOW_BULK_POPUP, doNotShowBulkPopup.isSelected()));
        settings.add(doNotShowBulkPopup);

        JCheckBoxMenuItem includeFamily = new JCheckBoxMenuItem("Include Family On Searchbar");
        includeFamily.setSelected(config.getBool(ConfigKey.INCLUDE_FAMILY));
        includeFamily.addItemListener(
            e -> {
                config.setBool(ConfigKey.INCLUDE_FAMILY, includeFamily.isSelected());
                if (!pokemonTab.getPokemonTable().getSelectedPokemon().isEmpty()) {
                    SwingUtilities.invokeLater(pokemonTab::refreshList);
                }
            });
        settings.add(includeFamily);

        JCheckBoxMenuItem alternativeIVCalculation = new JCheckBoxMenuItem(
            "Use Alternative IV Calculation (weighted stats)");
        alternativeIVCalculation.setSelected(config.getBool(ConfigKey.ALTERNATIVE_IV_CALCULATION));
        alternativeIVCalculation.addItemListener(
            e -> {
                config.setBool(ConfigKey.ALTERNATIVE_IV_CALCULATION, alternativeIVCalculation.isSelected());
                SwingUtilities.invokeLater(pokemonTab::refreshList);
            });
        settings.add(alternativeIVCalculation);
        JMenuItem saveColumnOrder = new JMenuItem("Save Column Order");
        saveColumnOrder.addActionListener(al -> {
            try {
                pokemonTab.saveColumnOrder();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        settings.add(saveColumnOrder);

        add(settings);

        // Help menu
        help = new JMenu("Help");

        JMenuItem checkUpdates = new JMenuItem("Check for Updates");
        checkUpdates.addActionListener(l -> {
            Updater updater = Updater.getUpdater();
            updater.checkForNewVersion();
            if (!updater.hasNewerVersion()) {
                JOptionPane.showMessageDialog(null,
                    "No new updates where found. Current version '" + updater.currentVersion + "' is the latest.",
                    checkUpdates.getText(),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        help.add(checkUpdates);

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(l -> JOptionPane.showMessageDialog(null,
            "Version: " + Updater.getUpdater().currentVersion
                + StringLiterals.NEWLINE
                + StringLiterals.NEWLINE + "Original Author: Corrie 'Blossom' Kay"
                + StringLiterals.NEWLINE + "Current Author: Wolfsblvt"
                + StringLiterals.NEWLINE
                + StringLiterals.NEWLINE + "Collaborators: Ljay, naderki, wullxz,"
                + StringLiterals.NEWLINE + "Cryptically, eralpsahin, weblue,"
                + StringLiterals.NEWLINE + "edysantosa, dylanpdx, michael-smith-versacom"
                + StringLiterals.NEWLINE
                + StringLiterals.NEWLINE + "This work is protected under the"
                + StringLiterals.NEWLINE + "Creative Commons Attribution-"
                + StringLiterals.NEWLINE + "NonCommercial-ShareAlike 4.0"
                + StringLiterals.NEWLINE + "International license, which can"
                + StringLiterals.NEWLINE + "be found here:"
                + StringLiterals.NEWLINE + "https://creativecommons.org/"
                + StringLiterals.NEWLINE + "licenses/by-nc-sa/4.0/"
                + StringLiterals.NEWLINE
                + StringLiterals.NEWLINE + "Thanks to Grover for providing"
                + StringLiterals.NEWLINE + "such a great API."
                + StringLiterals.NEWLINE
                + StringLiterals.NEWLINE + "Thanks to Draseart for "
                + "the icon art.",
            "About Blossom's Pokémon Go Manager", JOptionPane.PLAIN_MESSAGE));
        help.add(about);

        add(help);
    }

    private void logout() throws Exception {
        AccountController.logOff();
    }

    private void displayTrainerStats() throws Exception {
        PlayerProfile pp = go.getPlayerProfile();
        Stats stats = pp.getStats();
        Object[] tstats = {
            String.format("Trainer Name: %s", pp.getPlayerData().getUsername()),
            String.format("Team: %s", PokemonUtils.convertTeamColorToName(pp.getPlayerData().getTeamValue())),
            String.format("Level: %d", stats.getLevel()),
            String.format("XP: %d (%d to next level)",
                stats.getExperience(),
                (stats.getNextLevelXp() - stats.getExperience())),
            String.format("Stardust: %d", pp.getCurrency(Currency.STARDUST))};
        JOptionPane.showMessageDialog(null, tstats, "Trainer Stats", JOptionPane.PLAIN_MESSAGE);
    }
}
