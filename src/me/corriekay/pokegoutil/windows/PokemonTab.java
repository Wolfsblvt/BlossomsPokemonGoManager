package me.corriekay.pokegoutil.windows;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass;
import POGOProtos.Networking.Responses.SetFavoritePokemonResponseOuterClass;
import POGOProtos.Networking.Responses.UpgradePokemonResponseOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.api.pokemon.Pokemon;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.LDocumentListener;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokeNick;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.ui.GhostText;
import me.corriekay.pokegoutil.utils.windows.PokemonTable;
import me.corriekay.pokegoutil.utils.windows.PokemonTableModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("serial")
public class PokemonTab extends JPanel {

    private final PokemonGo go;
    private final PokemonTable pt;
    private final JTextField searchBar = new JTextField("");
    private final JTextField ivTransfer = new JTextField("", 20);

    private ConfigNew config = ConfigNew.getConfig();

    public PokemonTab(PokemonGo go) {
        setLayout(new BorderLayout());
        this.go = go;
        pt = new PokemonTable(go);
        JPanel topPanel = new JPanel(new GridBagLayout());
        JButton refreshPkmn, renameSelected, transferSelected, evolveSelected, powerUpSelected, toggleFavorite;
        refreshPkmn = new JButton("Refresh List");
        renameSelected = new JButton("Rename");
        transferSelected = new JButton("Transfer");
        evolveSelected = new JButton("Evolve");
        powerUpSelected = new JButton("Power Up");
        toggleFavorite = new JButton("Toggle Favorite");

        pt.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == true) {
                    int selectedRows = pt.getSelectedRowCount();
                    if (selectedRows > 1) {
                        PokemonGoMainWindow.window.setTitle(selectedRows + " Pokémon selected");
                    } else {
                        PokemonGoMainWindow.window.refreshTitle();
                    }
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        topPanel.add(refreshPkmn, gbc);
        refreshPkmn.addActionListener(l -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                refreshPkmn();
                return null;
            }
        }.execute());
        topPanel.add(renameSelected, gbc);
        renameSelected.addActionListener(l -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                renameSelected();
                return null;
            }
        }.execute());
        topPanel.add(transferSelected, gbc);
        transferSelected.addActionListener(l -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                transferSelected();
                return null;
            }
        }.execute());
        topPanel.add(evolveSelected, gbc);
        evolveSelected.addActionListener(l -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                evolveSelected();
                return null;
            }
        }.execute());
        topPanel.add(powerUpSelected, gbc);
        powerUpSelected.addActionListener(l -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                powerUpSelected();
                return null;
            }
        }.execute());
        topPanel.add(toggleFavorite, gbc);
        toggleFavorite.addActionListener(l -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                toggleFavorite();
                return null;
            }
        }.execute());

        ivTransfer.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            new SwingWorker<Void, Void>() {
                                protected Void doInBackground() throws Exception {
                                    selectLessThanIv();
                                    return null;
                                }
                            }.execute();
                        }
                    }

                    @Override
                    public void keyTyped(KeyEvent e) {
                        // nothing here
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        // nothing here
                    }
                });

        topPanel.add(ivTransfer, gbc);

        new GhostText(ivTransfer, "Pokemon IV");
        JButton transferIv = new JButton("Select Pokemon < IV");
        transferIv.addActionListener(l -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                selectLessThanIv();
                return null;
            }
        }.execute());
        topPanel.add(transferIv, gbc);

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(searchBar, gbc);

        // pokemon name language drop down
        String[] locales = { "en", "de", "fr", "ru", "zh_CN", "zh_HK", "ja" };
        JComboBox<String> pokelang = new JComboBox<>(locales);
        String locale = config.getString(ConfigKey.LANGUAGE);
        pokelang.setSelectedItem(locale);
        pokelang.addActionListener(e -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                @SuppressWarnings("unchecked")
                JComboBox<String> pokelang1 = (JComboBox<String>) e.getSource();
                String lang = (String) pokelang1.getSelectedItem();
                changeLanguage(lang);
                return null;
            }
        }.execute());
        topPanel.add(pokelang);

        // Set font size if specified in config
        Font font = pt.getFont();
        int size = config.getInt(ConfigKey.FONT_SIZE, font.getSize());
        if (size != font.getSize()) {
            pt.setFont(font.deriveFont((float) size));
        }

        // Font size dropdown
        String[] sizes = { "11", "12", "13", "14", "15", "16", "17", "18" };
        JComboBox<String> fontSize = new JComboBox<>(sizes);
        fontSize.setSelectedItem(String.valueOf(size));
        fontSize.addActionListener(e -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                @SuppressWarnings("unchecked")
                JComboBox<String> source = (JComboBox<String>) e.getSource();
                String size = source.getSelectedItem().toString();
                pt.setFont(pt.getFont().deriveFont(Float.parseFloat(size)));
                config.setInt(ConfigKey.FONT_SIZE, Integer.parseInt(size));
                return null;
            }
        }.execute());
        topPanel.add(fontSize);

        LDocumentListener.addChangeListener(searchBar, e -> refreshList());
        new GhostText(searchBar, "Search Pokémon...");

        add(topPanel, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(pt);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp, BorderLayout.CENTER);
    }

    private void changeLanguage(String langCode) {
        config.setString(ConfigKey.LANGUAGE, langCode);
        refreshPkmn();
    }

    private void refreshPkmn() {
        try {
            go.getInventories().updateInventories(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(this::refreshList);
        System.out.println("Done refreshing Pokémon list");
    }

    private void showFinishedText(String message, int size, MutableInt success, MutableInt skipped, MutableInt err) {
        String finishText = message +
                "\nPokémon total: " + size +
                "\nSuccessful: " + success.getValue() +
                (skipped.getValue() > 0 ? "\nSkipped: " + skipped.getValue() : "") +
                (err.getValue() > 0 ? "\nErrors: " + err.getValue() : "");

        if (config.getBool(ConfigKey.SHOW_BULK_POPUP)) {
            JOptionPane.showMessageDialog(null, finishText);
        } else {
            System.out.println(finishText);
        }
    }

    private void renameSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() == 0)
            return;
        String renamePattern = inputOperation("Rename", selection);

        MutableInt err = new MutableInt(),
                skipped = new MutableInt(),
                success = new MutableInt(),
                total = new MutableInt(1);
        PokeHandler handler = new PokeHandler(selection);

        BiConsumer<NicknamePokemonResponse.Result, Pokemon> perPokeCallback = (renameResult, pokemon) -> {
            System.out.println("Doing Rename " + total.getValue() + " of " + selection.size());
            total.increment();

            PokeNick pokeNick = PokeHandler.generatePokemonNickname(renamePattern, pokemon);

            // We check if the Pokemon was skipped
            boolean isSkipped = (pokeNick.equals(pokemon.getNickname())
                    && renameResult.getNumber() == NicknamePokemonResponse.Result.UNSET_VALUE);
            if (isSkipped) {
                System.out.println("Skipped renaming "
                        + PokeHandler.getLocalPokeName(pokemon)
                        + ", already named "
                        + pokemon.getNickname());
                skipped.increment();
                return;
            }

            if (renameResult.getNumber() == NicknamePokemonResponse.Result.SUCCESS_VALUE) {
                success.increment();
                if (pokeNick.isTooLong()) {
                    System.out.println("WARNING: Nickname \"" + pokeNick.fullNickname
                            + "\" is too long. Get's cut to: " + pokeNick.toString());
                }
                System.out.println("Renaming " + PokeHandler.getLocalPokeName(pokemon)
                        + " from \"" + pokemon.getNickname()
                        + "\" to \"" + PokeHandler.generatePokemonNickname(renamePattern, pokemon)
                        + "\", Result: Success!");
            } else {
                err.increment();
                System.out.println("Renaming " + PokeHandler.getLocalPokeName(pokemon)
                        + " failed! Code: " + renameResult.toString()
                        + "; Nick: " + PokeHandler.generatePokemonNickname(renamePattern, pokemon));
            }

            // If not last element and API was queried, sleep until the next one
            if (!selection.get(selection.size() - 1).equals(pokemon)) {
                int sleepMin = config.getInt(ConfigKey.DELAY_RENAME_MIN);
                int sleepMax = config.getInt(ConfigKey.DELAY_RENAME_MAX);
                Utilities.sleepRandom(sleepMin, sleepMax);
            }
        };

        handler.bulkRenameWithPattern(renamePattern, perPokeCallback);

        try {
            go.getInventories().updateInventories(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText("Pokémon batch rename complete!", selection.size(), success, skipped, err);
    }

    private void transferSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() == 0)
            return;
        if (confirmOperation("Transfer", selection)) {
            MutableInt err = new MutableInt(),
                    skipped = new MutableInt(),
                    success = new MutableInt(),
                    total = new MutableInt(1);
            selection.forEach(poke -> {
                System.out.println("Doing Transfer " + total.getValue() + " of " + selection.size());
                total.increment();
                if (poke.isFavorite()) {
                    System.out.println(PokeHandler.getLocalPokeName(poke) + " with "
                            + poke.getCp() + " CP is favorite, skipping.");
                    skipped.increment();
                    return;
                }
                if (!poke.getDeployedFortId().isEmpty()) {
                    System.out.println(
                            PokeHandler.getLocalPokeName(poke) + " with "
                                    + poke.getCp() + " CP is in gym, skipping.");
                    skipped.increment();
                    return;
                }

                try {
                    int candies = poke.getCandy();
                    ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result transferResult = poke
                            .transferPokemon();

                    if (transferResult == ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result.SUCCESS) {
                        int newCandies = poke.getCandy();
                        System.out.println("Transferring " + PokeHandler.getLocalPokeName(poke) + ", Result: Success!");
                        System.out.println("Stat changes: "
                                + "(Candies : " + newCandies
                                + "[+" + (newCandies - candies) + "])");
                        success.increment();
                    } else {
                        System.out.println("Error transferring "
                                + PokeHandler.getLocalPokeName(poke)
                                + ", result: "
                                + transferResult.toString());
                        err.increment();
                    }

                    // If not last element, sleep until the next one
                    if (!selection.get(selection.size() - 1).equals(poke)) {
                        int sleepMin = config.getInt(ConfigKey.DELAY_TRANSFER_MIN);
                        int sleepMax = config.getInt(ConfigKey.DELAY_TRANSFER_MAX);
                        Utilities.sleepRandom(sleepMin, sleepMax);
                    }
                } catch (Exception e) {
                    err.increment();
                    System.out.println("Error transferring "
                            + PokeHandler.getLocalPokeName(poke)
                            + "! "
                            + Utilities.getRealExceptionMessage(e));
                }
            });
            try {
                go.getInventories().updateInventories(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(this::refreshList);
            showFinishedText("Pokémon batch transfer complete!", selection.size(), success, skipped, err);
        }
    }

    private void evolveSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() > 0) {
            if (confirmOperation("Evolve", selection)) {
                MutableInt err = new MutableInt(),
                        skipped = new MutableInt(),
                        success = new MutableInt(),
                        total = new MutableInt(1);
                selection.forEach(poke -> {
                    System.out.println("Doing Evolve " + total.getValue()
                            + " of " + selection.size());
                    total.increment();
                    if (!poke.getDeployedFortId().isEmpty()) {
                        System.out.println(PokeHandler.getLocalPokeName(poke) + " with "
                                + poke.getCp() + " CP is in gym, skipping.");
                        skipped.increment();
                        return;
                    }

                    try {
                        int candies = poke.getCandy();
                        int candiesToEvolve = poke.getCandiesToEvolve();
                        int cp = poke.getCp();
                        int hp = poke.getMaxStamina();

                        // Check if user has enough candy, otherwise we don't
                        // need to call server
                        if (candies < candiesToEvolve) {
                            err.increment();
                            System.out.println("Error. Not enough candy to evolve "
                                    + PokeHandler.getLocalPokeName(poke) + ". "
                                    + candies + " available, " + candiesToEvolve + " needed.");
                            return;
                        }

                        EvolutionResult evolutionResultWrapper = poke.evolve();
                        if (evolutionResultWrapper.isSuccessful()) {
                            Pokemon newPoke = evolutionResultWrapper.getEvolvedPokemon();
                            int newCandies = newPoke.getCandy();
                            int newCp = newPoke.getCp();
                            int newHp = newPoke.getStamina();
                            System.out.println("Evolving "
                                    + PokeHandler.getLocalPokeName(poke)
                                    + ". Evolve result: "
                                    + evolutionResultWrapper.getResult().toString());
                            if (config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE)) {
                                ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result result = newPoke
                                        .transferPokemon();
                                System.out.println("Transferring "
                                        + StringUtils.capitalize(newPoke.getPokemonId().toString().toLowerCase())
                                        + ", Result: " + result);
                                System.out.println("Stat changes: " +
                                        "(Candies: " + newCandies
                                        + "[" + candies + "-" + candiesToEvolve + "]");

                            } else {
                                System.out.println("Stat changes: "
                                        + "(Candies: " + newCandies + "[" + candies + "-" + candiesToEvolve + "],"
                                        + " CP: " + newCp + "[+" + (newCp - cp) + "],"
                                        + " HP: " + newHp + "[+" + (newHp - hp) + "])");
                            }
                            go.getInventories().updateInventories(true);
                            success.increment();
                        } else {
                            err.increment();
                            System.out.println("Error evolving " + PokeHandler.getLocalPokeName(poke) + ", result: "
                                    + evolutionResultWrapper.getResult().toString());
                        }

                        // If not last element, sleep until the next one
                        if (!selection.get(selection.size() - 1).equals(poke)) {
                            int sleepMin = config.getInt(ConfigKey.DELAY_EVOLVE_MIN);
                            int sleepMax = config.getInt(ConfigKey.DELAY_EVOLVE_MAX);
                            Utilities.sleepRandom(sleepMin, sleepMax);
                        }
                    } catch (Exception e) {
                        err.increment();
                        System.out.println("Error evolving "
                                + PokeHandler.getLocalPokeName(poke)
                                + "! " + Utilities.getRealExceptionMessage(e));
                    }
                });
                try {
                    go.getInventories().updateInventories(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(this::refreshList);
                showFinishedText("Pokémon batch evolve"
                        + (config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE) ? "/transfer" : "")
                        + " complete!", selection.size(), success, skipped, err);
            }
        }
    }

    private void powerUpSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() > 0) {
            if (confirmOperation("PowerUp", selection)) {
                MutableInt err = new MutableInt(),
                        skipped = new MutableInt(),
                        success = new MutableInt(),
                        total = new MutableInt(1);
                selection.forEach(poke -> {
                    try {
                        System.out.println("Doing Power Up " + total.getValue() + " of " + selection.size());
                        total.increment();
                        if (!poke.getDeployedFortId().isEmpty()) {
                            System.out.println(PokeHandler.getLocalPokeName(poke) + " with "
                                    + poke.getCp() + " CP is in gym, skipping.");
                            skipped.increment();
                            return;
                        }

                        int stardust = go.getPlayerProfile().getCurrency(Currency.STARDUST);
                        int candies = poke.getCandy();
                        int cp = poke.getCp();
                        int hp = poke.getMaxStamina();
                        int stardustToPowerUp = poke.getStardustCostsForPowerup();
                        int candiesToPowerUp = poke.getCandyCostsForPowerup();

                        // Check if user has enough candy and stardust,
                        // otherwise we don't need to call server
                        if (candies < candiesToPowerUp || stardust < stardustToPowerUp) {
                            err.increment();
                            System.out.println("Error. Not enough candy/stardust to power up "
                                    + PokeHandler.getLocalPokeName(poke)
                                    + ". Stardust: " + stardust + "/" + stardustToPowerUp
                                    + ", Candy: " + candies + "/" + candiesToPowerUp);
                            return;
                        }

                        UpgradePokemonResponseOuterClass.UpgradePokemonResponse.Result upgradeResult = poke.powerUp();
                        go.getPlayerProfile().updateProfile();
                        if (upgradeResult == UpgradePokemonResponseOuterClass.UpgradePokemonResponse.Result.SUCCESS) {
                            int newCandies = poke.getCandy();
                            int newCp = poke.getCp();
                            int newHp = poke.getMaxStamina();
                            System.out.println(
                                    "Powering Up " + PokeHandler.getLocalPokeName(poke) + ", Result: Success!");
                            System.out.println("Stat changes: " +
                                    "(Candies : " + newCandies + "[" + candies + "-" + candiesToPowerUp + "], "
                                    + "CP: " + newCp + "[+" + (newCp - cp) + "], "
                                    + "HP: " + newHp + "[+" + (newHp - hp) + "]) "
                                    + "Stardust used " + stardustToPowerUp
                                    + "[remaining: " + go.getPlayerProfile().getCurrency(Currency.STARDUST) + "]");
                            success.increment();
                        } else {
                            err.increment();
                            System.out.println("Error powering up " + PokeHandler.getLocalPokeName(poke) + ", result: "
                                    + upgradeResult.toString());
                        }

                        // If not last element, sleep until the next one
                        if (!selection.get(selection.size() - 1).equals(poke)) {
                            int sleepMin = config.getInt(ConfigKey.DELAY_POWERUP_MIN);
                            int sleepMax = config.getInt(ConfigKey.DELAY_POWERUP_MAX);
                            Utilities.sleepRandom(sleepMin, sleepMax);
                        }
                    } catch (Exception e) {
                        err.increment();
                        System.out.println("Error powering up " + PokeHandler.getLocalPokeName(poke) + "! "
                                + Utilities.getRealExceptionMessage(e));
                    }
                });
                try {
                    go.getInventories().updateInventories(true);
                    PokemonGoMainWindow.window.refreshTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(this::refreshList);
                showFinishedText("Pokémon batch powerup complete!", selection.size(), success, skipped, err);
            }
        }
    }

    // feature added by Ben Kauffman
    private void toggleFavorite() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() > 0) {
            if (confirmOperation("Toggle Favorite", selection)) {
                MutableInt err = new MutableInt(), skipped = new MutableInt(), success = new MutableInt(),
                        total = new MutableInt(1);
                selection.forEach(poke -> {
                    try {
                        System.out.println("Toggling favorite " + total.getValue() + " of " + selection.size());
                        total.increment();
                        SetFavoritePokemonResponseOuterClass.SetFavoritePokemonResponse.Result favoriteResult = poke
                                .setFavoritePokemon(!poke.isFavorite());
                        System.out.println("Attempting to set favorite for " + PokeHandler.getLocalPokeName(poke)
                                + " to " + !poke.isFavorite() + "...");
                        go.getPlayerProfile().updateProfile();

                        if (favoriteResult == SetFavoritePokemonResponseOuterClass.SetFavoritePokemonResponse.Result.SUCCESS) {
                            System.out.println("Favorite for " + PokeHandler.getLocalPokeName(poke)
                                    + " set to " + !poke.isFavorite()
                                    + ", Result: Success!");
                            success.increment();
                        } else {
                            err.increment();
                            System.out.println("Error toggling favorite for " + PokeHandler.getLocalPokeName(poke)
                                    + ", result: " + favoriteResult.toString());
                        }

                        // If not last element, sleep until the next one
                        if (!selection.get(selection.size() - 1).equals(poke)) {
                            int sleepMin = config.getInt(ConfigKey.DELAY_FAVORITE_MIN);
                            int sleepMax = config.getInt(ConfigKey.DELAY_FAVORITE_MAX);
                            Utilities.sleepRandom(sleepMin, sleepMax);
                        }
                    } catch (Exception e) {
                        err.increment();
                        System.out.println("Error toggling favorite for "
                                + PokeHandler.getLocalPokeName(poke)
                                + "! " + Utilities.getRealExceptionMessage(e));
                    }
                });
                try {
                    PokemonGoMainWindow.window.refreshTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(this::refreshPkmn);
                showFinishedText("Pokémon batch \"toggle favorite\" complete!", selection.size(), success, skipped,
                        err);
            }
        }
    }

    private void selectLessThanIv() {
        if (!NumberUtils.isNumber(ivTransfer.getText())) {
            System.out.println("Please select a valid IV value (0-100)");
            return;
        }

        double ivLessThan = Double.parseDouble(ivTransfer.getText());
        if (ivLessThan > 100 || ivLessThan < 0) {
            System.out.println("Please select a valid IV value (0-100)");
            return;
        }
        pt.clearSelection();
        System.out.println("Selecting Pokemon with IV less than: " + ivTransfer.getText());

        for (int i = 0; i < pt.getRowCount(); i++) {
            double pIv = Double.parseDouble((String) pt.getValueAt(i, 3));
            if (pIv < ivLessThan) {
                pt.getSelectionModel().addSelectionInterval(i, i);
            }
        }
    }

    private String inputOperation(String operation, ArrayList<Pokemon> pokes) {
        JPanel panel = _buildPanelForOperation(operation, pokes);
        String message = "";
        switch (operation) {
        case "Rename":
            message = "You want to rename " + pokes.size()
                    + " Pokémon.\nYou can rename with normal text and patterns, or both combined. "
                    + "Patterns are going to be replaced with the Pokémons values.\nExisting patterns:\n";
            for (PokeHandler.ReplacePattern pattern : PokeHandler.ReplacePattern.values()) {
                message += "%" + pattern.name().toLowerCase() + "% -> " + pattern.toString() + "\n";
            }
            message += "\n";
        }

        String input = JOptionPane.showInputDialog(panel, message, operation, JOptionPane.PLAIN_MESSAGE);
        return input;
    }

    private boolean confirmOperation(String operation, ArrayList<Pokemon> pokes) {
        JPanel panel = _buildPanelForOperation(operation, pokes);

        int response = JOptionPane.showConfirmDialog(null, panel,
                "Please confirm " + operation + " of " + pokes.size() + " Pokémon", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        return response == JOptionPane.OK_OPTION;
    }

    private JPanel _buildPanelForOperation(String operation, ArrayList<Pokemon> pokes) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setAlignmentX(CENTER_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(innerPanel);
        scroll.setAlignmentX(CENTER_ALIGNMENT);

        // Auto-height? Resizable? Haha. Funny joke.
        // I hate swing. But we need to get around here some way.
        // So lets get dirty.
        // We take 20 px for each row, 5 px buffer, and cap that at may 400
        // pixel.
        int height = Math.min(400, pokes.size() * 20 + 5);
        panel.setPreferredSize(new Dimension(500, height));

        pokes.forEach(p -> {
            String str = PokeHandler.getLocalPokeName(p) + " - CP: " + p.getCp() + ", IV: "
                    + Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(p)) + "%";
            switch (operation) {
            case "Evolve":
                str += " Cost: " + p.getCandiesToEvolve();
                str += p.getCandiesToEvolve() > 1 ? " Candies" : " Candy";
                break;
            case "PowerUp":
                str += " Cost: " + p.getCandyCostsForPowerup();
                str += p.getCandyCostsForPowerup() > 1 ? " Candies" : " Candy";
                str += " " + p.getStardustCostsForPowerup() + " Stardust";
                break;
            case "Rename":
                break;
            case "Transfer":
                break;
            }
            innerPanel.add(new JLabel(str));
        });
        panel.add(scroll);
        return panel;
    }

    public ArrayList<Pokemon> getSelectedPokemon() {
        ArrayList<Pokemon> pokes = new ArrayList<>();
        PokemonTableModel model = (PokemonTableModel) pt.getModel();
        for (int i : pt.getSelectedRows()) {
            Pokemon poke = model.getPokemonByIndex(i);
            if (poke != null) {
                pokes.add(poke);
            }
        }
        return pokes;
    }

    public void refreshList() {
        List<Pokemon> pokes = new ArrayList<>();
        String search = searchBar.getText().replaceAll(" ", "").replaceAll("_", "").replaceAll("snek", "ekans")
                .toLowerCase();
        String[] terms = search.split(";");
        try {
            go.getInventories().getPokebank().getPokemons().forEach(poke -> {
                boolean useFamilyName = config.getBool(ConfigKey.INCLUDE_FAMILY);
                String familyName = "";
                if (useFamilyName) {
                    // Try translating family name
                    try {
                        PokemonId familyPokemonId = PokemonId
                                .valueOf(poke.getPokemonFamily().toString().replaceAll("FAMILY_", ""));
                        familyName = PokeHandler.getLocalPokeName(familyPokemonId.getNumber());
                    } catch (IllegalArgumentException e) {
                        familyName = poke.getPokemonFamily().toString();
                    }
                }

                String searchme = Utilities.concatString(',',
                        PokeHandler.getLocalPokeName(poke).toString(),
                        ((useFamilyName) ? familyName : ""),
                        poke.getNickname().toString(),
                        poke.getMeta().getType1().toString(),
                        poke.getMeta().getType2().toString(),
                        poke.getMove1().toString(),
                        poke.getMove2().toString(),
                        poke.getPokeball().toString());
                searchme = searchme.replaceAll("_FAST", "").replaceAll("FAMILY_", "").replaceAll("NONE", "")
                        .replaceAll("ITEM_", "").replaceAll("_", "").replaceAll(" ", "").toLowerCase();

                for (String s : terms) {
                    if (searchme.contains(s)) {
                        pokes.add(poke);
                        // Break, so that a Pokémon isn't added twice even if it
                        // matches more than one criteria
                        break;
                    }
                }
            });
            pt.constructNewTableModel((search.equals("") || search.equals("searchpokémon...")
                    ? go.getInventories().getPokebank().getPokemons() : pokes));
            
           // for (int i = 0; i < pt.getModel().getColumnCount(); i++) {
            //    JTableColumnPacker.packColumn(pt, i, 4);
            //}

            // Custom cell renderers
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provide custom formatting for the moveset ranking columns while allowing
     * sorting on original values
     */
    public static class MoveSetRankingRenderer extends JLabel implements TableCellRenderer {

        private final long scale;

        public MoveSetRankingRenderer(long scale) {
            this.scale = scale;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int rowIndex, int vColIndex) {
            setText(Utilities.percentageWithTwoCharacters(Double.parseDouble(value.toString()), this.scale));
            setToolTipText(NumberFormat.getInstance().format(value));
            setOpaque(true);
            setDefaultSelectionColors(table, isSelected, this);
            return this;
        }
    }

    private static void setDefaultSelectionColors(JTable table, boolean isSelected, JLabel tcr) {
        if (isSelected) {
            tcr.setBackground(table.getSelectionBackground());
            tcr.setForeground(table.getSelectionForeground());
        } else {
            tcr.setBackground(table.getBackground());
            tcr.setForeground(table.getForeground());
        }
    }
}
