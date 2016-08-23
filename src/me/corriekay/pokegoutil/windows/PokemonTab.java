package me.corriekay.pokegoutil.windows;

import POGOProtos.Enums.PokemonFamilyIdOuterClass.PokemonFamilyId;
import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass;
import POGOProtos.Networking.Responses.SetFavoritePokemonResponseOuterClass;
import POGOProtos.Networking.Responses.UpgradePokemonResponseOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.helpers.JTableColumnPacker;
import me.corriekay.pokegoutil.utils.helpers.LDocumentListener;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokeNick;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCpUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.pokemon.Evolves;
import me.corriekay.pokegoutil.utils.ui.GhostText;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.WordUtils;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
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
    private final PokemonTable pt = new PokemonTable();
    private final JTextField searchBar = new JTextField("");
    private final JTextField ivTransfer = new JTextField("", 20);

    private ConfigNew config = ConfigNew.getConfig();

    public PokemonTab(PokemonGo go) {
        setLayout(new BorderLayout());
        this.go = go;
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
                        //nothing here
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        //nothing here
                    }
                }
        );

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
        String[] locales = {"en", "de", "fr", "ru", "zh_CN", "zh_HK", "ja"};
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
        String[] sizes = {"11", "12", "13", "14", "15", "16", "17", "18"};
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
            Evolves.print(go);
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
        if (selection.size() == 0) return;
        String renamePattern = inputOperation("Rename", selection);

        MutableInt err = new MutableInt(), skipped = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
        PokeHandler handler = new PokeHandler(selection);

        BiConsumer<NicknamePokemonResponse.Result, Pokemon> perPokeCallback = (renameResult, pokemon) -> {
            System.out.println("Doing Rename " + total.getValue() + " of " + selection.size());
            total.increment();

            PokeNick pokeNick = PokeHandler.generatePokemonNickname(renamePattern, pokemon);

            // We check if the Pokemon was skipped
            boolean isSkipped = (pokeNick.equals(pokemon.getNickname())
                    && renameResult.getNumber() == NicknamePokemonResponse.Result.UNSET_VALUE);
            if (isSkipped) {
                System.out.println("Skipped renaming " + PokeHandler.getLocalPokeName(pokemon) + ", already named " + pokemon.getNickname());
                skipped.increment();
                return;
            }

            if (renameResult.getNumber() == NicknamePokemonResponse.Result.SUCCESS_VALUE) {
                success.increment();
                if (pokeNick.isTooLong()) {
                    System.out.println("WARNING: Nickname \"" + pokeNick.fullNickname + "\" is too long. Get's cut to: " + pokeNick.toString());
                }
                System.out.println("Renaming " + PokeHandler.getLocalPokeName(pokemon) + " from \"" + pokemon.getNickname() + "\" to \"" + PokeHandler.generatePokemonNickname(renamePattern, pokemon) + "\", Result: Success!");
            } else {
                err.increment();
                System.out.println("Renaming " + PokeHandler.getLocalPokeName(pokemon) + " failed! Code: " + renameResult.toString() + "; Nick: " + PokeHandler.generatePokemonNickname(renamePattern, pokemon));
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
        if (selection.size() == 0) return;
        if (confirmOperation("Transfer", selection)) {
            MutableInt err = new MutableInt(), skipped = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
            selection.forEach(poke -> {
                System.out.println("Doing Transfer " + total.getValue() + " of " + selection.size());
                total.increment();
                if (poke.isFavorite()) {
                    System.out.println(PokeHandler.getLocalPokeName(poke) + " with " + poke.getCp() + " CP is favorite, skipping.");
                    skipped.increment();
                    return;
                }
                if (!poke.getDeployedFortId().isEmpty()) {
                    System.out.println(PokeHandler.getLocalPokeName(poke) + " with " + poke.getCp() + " CP is in gym, skipping.");
                    skipped.increment();
                    return;
                }

                try {
                    int candies = poke.getCandy();
                    ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result transferResult = poke.transferPokemon();

                    if (transferResult == ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result.SUCCESS) {
                        int newCandies = poke.getCandy();
                        System.out.println("Transferring " + PokeHandler.getLocalPokeName(poke) + ", Result: Success!");
                        System.out.println("Stat changes: (Candies : " + newCandies + "[+" + (newCandies - candies) + "])");
                        success.increment();
                    } else {
                        System.out.println("Error transferring " + PokeHandler.getLocalPokeName(poke) + ", result: " + transferResult.toString());
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
                    System.out.println("Error transferring " + PokeHandler.getLocalPokeName(poke) + "! " + Utilities.getRealExceptionMessage(e));
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
                MutableInt err = new MutableInt(), skipped = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
                selection.forEach(poke -> {
                    System.out.println("Doing Evolve " + total.getValue() + " of " + selection.size());
                    total.increment();
                    if (!poke.getDeployedFortId().isEmpty()) {
                        System.out.println(PokeHandler.getLocalPokeName(poke) + " with " + poke.getCp() + " CP is in gym, skipping.");
                        skipped.increment();
                        return;
                    }

                    try {
                        int candies = poke.getCandy();
                        int candiesToEvolve = poke.getCandiesToEvolve();
                        int cp = poke.getCp();
                        int hp = poke.getMaxStamina();

                        // Check if user has enough candy, otherwise we don't need to call server
                        if (candies < candiesToEvolve) {
                            err.increment();
                            System.out.println("Error. Not enough candy to evolve " + PokeHandler.getLocalPokeName(poke) + ". " + candies + " available, " + candiesToEvolve + " needed.");
                            return;
                        }

                        EvolutionResult evolutionResultWrapper = poke.evolve();
                        if (evolutionResultWrapper.isSuccessful()) {
                            Pokemon newPoke = evolutionResultWrapper.getEvolvedPokemon();
                            int newCandies = newPoke.getCandy();
                            int newCp = newPoke.getCp();
                            int newHp = newPoke.getStamina();
                            System.out.println("Evolving " + PokeHandler.getLocalPokeName(poke) + ". Evolve result: " + evolutionResultWrapper.getResult().toString());
                            if (config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE)) {
                                if (newPoke.isFavorite()) {
                                    System.out.println("Skipping \"Transfer After Evolve\" for " + StringUtils.capitalize(newPoke.getPokemonId().toString().toLowerCase()) + " because favorite.");
                                    System.out.println("Stat changes: (Candies: " + newCandies + "[" + candies + "-" + candiesToEvolve + "], CP: " + newCp + "[+" + (newCp - cp) + "], HP: " + newHp + "[+" + (newHp - hp) + "])");
                                } else {
                                    ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result result = newPoke.transferPokemon();
                                    System.out.println("Transferring " + StringUtils.capitalize(newPoke.getPokemonId().toString().toLowerCase()) + ", Result: " + result);
                                    System.out.println("Stat changes: (Candies: " + newCandies + "[" + candies + "-" + candiesToEvolve + "]");
                                }
                            } else {
                                System.out.println("Stat changes: (Candies: " + newCandies + "[" + candies + "-" + candiesToEvolve + "], CP: " + newCp + "[+" + (newCp - cp) + "], HP: " + newHp + "[+" + (newHp - hp) + "])");
                            }
                            go.getInventories().updateInventories(true);
                            success.increment();
                        } else {
                            err.increment();
                            System.out.println("Error evolving " + PokeHandler.getLocalPokeName(poke) + ", result: " + evolutionResultWrapper.getResult().toString());
                        }

                        // If not last element, sleep until the next one
                        if (!selection.get(selection.size() - 1).equals(poke)) {
                            int sleepMin = config.getInt(ConfigKey.DELAY_EVOLVE_MIN);
                            int sleepMax = config.getInt(ConfigKey.DELAY_EVOLVE_MAX);
                            Utilities.sleepRandom(sleepMin, sleepMax);
                        }
                    } catch (Exception e) {
                        err.increment();
                        System.out.println("Error evolving " + PokeHandler.getLocalPokeName(poke) + "! " + Utilities.getRealExceptionMessage(e));
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
                MutableInt err = new MutableInt(), skipped = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
                selection.forEach(poke -> {
                    try {
                        System.out.println("Doing Power Up " + total.getValue() + " of " + selection.size());
                        total.increment();
                        if (!poke.getDeployedFortId().isEmpty()) {
                            System.out.println(PokeHandler.getLocalPokeName(poke) + " with " + poke.getCp() + " CP is in gym, skipping.");
                            skipped.increment();
                            return;
                        }

                        int stardust = go.getPlayerProfile().getCurrency(Currency.STARDUST);
                        int candies = poke.getCandy();
                        int cp = poke.getCp();
                        int hp = poke.getMaxStamina();
                        int stardustToPowerUp = poke.getStardustCostsForPowerup();
                        int candiesToPowerUp = poke.getCandyCostsForPowerup();

                        // Check if user has enough candy and stardust, otherwise we don't need to call server
                        if (candies < candiesToPowerUp || stardust < stardustToPowerUp) {
                            err.increment();
                            System.out.println("Error. Not enough candy/stardust to power up " + PokeHandler.getLocalPokeName(poke) + ". Stardust: " + stardust + "/" + stardustToPowerUp + ", Candy: " + candies + "/" + candiesToPowerUp);
                            return;
                        }

                        UpgradePokemonResponseOuterClass.UpgradePokemonResponse.Result upgradeResult = poke.powerUp();
                        go.getPlayerProfile().updateProfile();
                        if (upgradeResult == UpgradePokemonResponseOuterClass.UpgradePokemonResponse.Result.SUCCESS) {
                            int newCandies = poke.getCandy();
                            int newCp = poke.getCp();
                            int newHp = poke.getMaxStamina();
                            System.out.println("Powering Up " + PokeHandler.getLocalPokeName(poke) + ", Result: Success!");
                            System.out.println("Stat changes: (Candies : " + newCandies + "[" + candies + "-" + candiesToPowerUp + "], CP: " + newCp + "[+" + (newCp - cp) + "], HP: " + newHp + "[+" + (newHp - hp) + "]) Stardust used " + stardustToPowerUp + "[remaining: " + go.getPlayerProfile().getCurrency(Currency.STARDUST) + "]");
                            success.increment();
                        } else {
                            err.increment();
                            System.out.println("Error powering up " + PokeHandler.getLocalPokeName(poke) + ", result: " + upgradeResult.toString());
                        }

                        // If not last element, sleep until the next one
                        if (!selection.get(selection.size() - 1).equals(poke)) {
                            int sleepMin = config.getInt(ConfigKey.DELAY_POWERUP_MIN);
                            int sleepMax = config.getInt(ConfigKey.DELAY_POWERUP_MAX);
                            Utilities.sleepRandom(sleepMin, sleepMax);
                        }
                    } catch (Exception e) {
                        err.increment();
                        System.out.println("Error powering up " + PokeHandler.getLocalPokeName(poke) + "! " + Utilities.getRealExceptionMessage(e));
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

    //feature added by Ben Kauffman
    private void toggleFavorite() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() > 0) {
            if (confirmOperation("Toggle Favorite", selection)) {
                MutableInt err = new MutableInt(), skipped = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
                selection.forEach(poke -> {
                    try {
                        System.out.println("Toggling favorite " + total.getValue() + " of " + selection.size());
                        total.increment();
                        SetFavoritePokemonResponseOuterClass.SetFavoritePokemonResponse.Result favoriteResult = poke.setFavoritePokemon(!poke.isFavorite());
                        System.out.println("Attempting to set favorite for " + PokeHandler.getLocalPokeName(poke) + " to " + !poke.isFavorite() + "...");
                        go.getPlayerProfile().updateProfile();

                        if (favoriteResult == SetFavoritePokemonResponseOuterClass.SetFavoritePokemonResponse.Result.SUCCESS) {
                            System.out.println("Favorite for " + PokeHandler.getLocalPokeName(poke) + " set to " + !poke.isFavorite() + ", Result: Success!");
                            success.increment();
                        } else {
                            err.increment();
                            System.out.println("Error toggling favorite for " + PokeHandler.getLocalPokeName(poke) + ", result: " + favoriteResult.toString());
                        }

                        // If not last element, sleep until the next one
                        if (!selection.get(selection.size() - 1).equals(poke)) {
                            int sleepMin = config.getInt(ConfigKey.DELAY_FAVORITE_MIN);
                            int sleepMax = config.getInt(ConfigKey.DELAY_FAVORITE_MAX);
                            Utilities.sleepRandom(sleepMin, sleepMax);
                        }
                    } catch (Exception e) {
                        err.increment();
                        System.out.println("Error toggling favorite for " + PokeHandler.getLocalPokeName(poke) + "! " + Utilities.getRealExceptionMessage(e));
                    }
                });
                try {
                    PokemonGoMainWindow.window.refreshTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(this::refreshPkmn);
                showFinishedText("Pokémon batch \"toggle favorite\" complete!", selection.size(), success, skipped, err);
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
            double pIv = (double) pt.getValueAt(i, 3);
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
                message = "You want to rename " + pokes.size() + " Pokémon.\nYou can rename with normal text and patterns, or both combined. Patterns are going to be replaced with the Pokémons values.\nExisting patterns:\n";
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
        // We take 20 px for each row, 5 px buffer, and cap that at may 400 pixel.
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
        String search = searchBar.getText().replaceAll(" ", "").replaceAll("_", "").replaceAll("snek", "ekans").toLowerCase();
        String[] terms = search.split(";");
        try {
            go.getInventories().getPokebank().getPokemons().forEach(poke -> {
                boolean useFamilyName = config.getBool(ConfigKey.INCLUDE_FAMILY);
                String familyName = "";
                if (useFamilyName) {
                    // Try translating family name
                    try {
                        PokemonId familyPokemonId = PokemonId.valueOf(poke.getPokemonFamily().toString().replaceAll("FAMILY_", ""));
                        familyName = PokeHandler.getLocalPokeName(familyPokemonId.getNumber());
                    } catch (IllegalArgumentException e) {
                        familyName = poke.getPokemonFamily().toString();
                    }
                }

                String searchme = PokeHandler.getLocalPokeName(poke)
                        + ((useFamilyName) ? familyName : "")
                        + poke.getNickname()
                        + poke.getMeta().getType1() + poke.getMeta().getType2() + poke.getMove1() + poke.getMove2()
                        + poke.getPokeball();
                searchme = searchme.replaceAll("_FAST", "").replaceAll("FAMILY_", "").replaceAll("NONE", "").replaceAll("ITEM_", "").replaceAll("_", "").replaceAll(" ", "").toLowerCase();
                for (String s : terms) {
                    if (searchme.contains(s)) {
                        pokes.add(poke);
                        // Break, so that a Pokémon isn't added twice even if it matches more than one criteria
                        break;
                    }
                }
            });
            pt.constructNewTableModel(go, (search.equals("") || search.equals("searchpokémon...")
                    ? go.getInventories().getPokebank().getPokemons() : pokes));
            for (int i = 0; i < pt.getModel().getColumnCount(); i++) {
                JTableColumnPacker.packColumn(pt, i, 4);
            }

            // Custom cell renderers
            // Magic numbers pulled from max values of their respective columns in the moveset rankings spreadsheet calculations
            // @see https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
            TableColumn duelAbilityCol = this.pt.getColumnModel().getColumn(24);
            duelAbilityCol.setCellRenderer(new MoveSetRankingRenderer(PokemonUtils.DUEL_ABILITY_MAX));
            TableColumn gymAttackCol = this.pt.getColumnModel().getColumn(25);
            gymAttackCol.setCellRenderer(new MoveSetRankingRenderer(PokemonUtils.GYM_OFFENSE_MAX));
            TableColumn gymDefenseCol = this.pt.getColumnModel().getColumn(26);
            gymDefenseCol.setCellRenderer(new MoveSetRankingRenderer(PokemonUtils.GYM_DEFENSE_MAX));
            TableColumn duelAbilityIVCol = this.pt.getColumnModel().getColumn(31);
            duelAbilityIVCol.setCellRenderer(new MoveSetRankingRenderer(PokemonUtils.DUEL_ABILITY_IV_MAX));
            TableColumn gymAttackIVCol = this.pt.getColumnModel().getColumn(32);
            gymAttackIVCol.setCellRenderer(new MoveSetRankingRenderer(PokemonUtils.GYM_OFFENSE_IV_MAX));
            TableColumn gymDefenseIVCol = this.pt.getColumnModel().getColumn(33);
            gymDefenseIVCol.setCellRenderer(new MoveSetRankingRenderer(PokemonUtils.GYM_DEFENSE_IV_MAX));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provide custom formatting for the moveset ranking columns while allowing sorting on original values
     */
    private static class MoveSetRankingRenderer extends JLabel implements TableCellRenderer {

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

    private static class PokemonTable extends JTable {

        /**
         * data types:
         * 0 String - Nickname
         * 1 Integer - Pokemon Number
         * 2 String - Type / Pokemon
         * 3 String(Percentage) - IV Rating
         * 4 Double - Level
         * 5 Integer - Attack
         * 6 Integer - Defense
         * 7 Integer - Stamina
         * 8 String - Type 1
         * 9 String - Type 2
         * 10 String - Move 1
         * 11 String - Move 2
         * 12 Integer - CP
         * 13 Integer - HP
         * 14 Integer - Max CP (Current)
         * 15 Integer - Max CP
         * 16 Integer - Max Evolved CP (Current)
         * 17 Integer - Max Evolved CP
         * 18 Integer - Candies of type
         * 19 String(Nullable Int) - Candies to Evolve
         * 20 Integer - Star Dust to level
         * 21 String - Pokeball Type
         * 22 String(Date) - Caught at
         * 23 Boolean - Favorite
         * 24 Long - duelAbility
         * 25 Integer - gymOffense
         * 26 Integer - gymDefense
         * 27 String(Percentage) - Move 1 Rating
         * 28 String(Percentage) - Move 2 Rating
         * 29 String(Nullable Int) - CP Evolved
         * 30 String(Nullable Int) - Evolvable
         * 31 Long - duelAbility IV
         * 32 Double - gymOffense IV
         * 33 Long - gymDefense IV
         */
        ConfigNew config = ConfigNew.getConfig();

        int sortColIndex1, sortColIndex2;
        SortOrder sortOrder1, sortOrder2;

        private PokemonTable() {
            setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            setAutoResizeMode(AUTO_RESIZE_OFF);

            // Load sort configs
            sortColIndex1 = config.getInt(ConfigKey.SORT_COLINDEX_1);
            sortColIndex2 = config.getInt(ConfigKey.SORT_COLINDEX_2);
            try {
                sortOrder1 = SortOrder.valueOf(config.getString(ConfigKey.SORT_ORDER_1));
                sortOrder2 = SortOrder.valueOf(config.getString(ConfigKey.SORT_ORDER_2));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                sortOrder1 = SortOrder.ASCENDING;
                sortOrder2 = SortOrder.ASCENDING;
            }
        }

        private void constructNewTableModel(PokemonGo go, List<Pokemon> pokes) {
            PokemonTableModel ptm = new PokemonTableModel(go, pokes, this);
            setModel(ptm);
            TableRowSorter<TableModel> trs = new TableRowSorter<>(getModel());
            Comparator<Integer> c = Integer::compareTo;
            Comparator<Double> cDouble = Double::compareTo;
            Comparator<String> cDate = (date1, date2) -> DateHelper.fromString(date1).compareTo(DateHelper.fromString(date2));
            Comparator<String> cNullableInt = (s1, s2) -> {
                if ("-".equals(s1))
                    s1 = "0";
                if ("-".equals(s2))
                    s2 = "0";
                return Integer.parseInt(s1) - Integer.parseInt(s2);
            };
            Comparator<Long> cLong = Long::compareTo;
            Comparator<String> cPercentageWithTwoCharacters = (s1, s2) -> {
                int i1 = ("XX".equals(s1)) ? 100 : Integer.parseInt(s1);
                int i2 = ("XX".equals(s2)) ? 100 : Integer.parseInt(s2);
                return i1 - i2;
            };
            trs.setComparator(0, c);
            trs.setComparator(3, cPercentageWithTwoCharacters);
            trs.setComparator(4, cDouble);
            trs.setComparator(5, c);
            trs.setComparator(6, c);
            trs.setComparator(7, c);
            trs.setComparator(12, c);
            trs.setComparator(13, c);
            trs.setComparator(14, c);
            trs.setComparator(15, c);
            trs.setComparator(16, c);
            trs.setComparator(17, c);
            trs.setComparator(18, c);
            trs.setComparator(19, cNullableInt);
            trs.setComparator(20, c);
            trs.setComparator(22, cDate);
            trs.setComparator(24, cLong);
            trs.setComparator(25, cDouble);
            trs.setComparator(26, cLong);
            trs.setComparator(27, cPercentageWithTwoCharacters);
            trs.setComparator(28, cPercentageWithTwoCharacters);
            trs.setComparator(29, cNullableInt);
            trs.setComparator(30, cNullableInt);
            trs.setComparator(31, cLong);
            trs.setComparator(32, cDouble);
            trs.setComparator(33, cLong);
            setRowSorter(trs);
            List<SortKey> sortKeys = new ArrayList<>();
            sortKeys.add(new SortKey(sortColIndex1, sortOrder1));
            sortKeys.add(new SortKey(sortColIndex2, sortOrder2));
            trs.setSortKeys(sortKeys);

            // Add listener to save those sorting values
            trs.addRowSorterListener(
                    e -> {
                        RowSorter sorter = e.getSource();
                        if (sorter != null) {
                            List<SortKey> keys = sorter.getSortKeys();
                            if (keys.size() > 0) {
                                SortKey prim = keys.get(0);
                                sortOrder1 = prim.getSortOrder();
                                config.setString(ConfigKey.SORT_ORDER_1, sortOrder1.toString());
                                sortColIndex1 = prim.getColumn();
                                config.setInt(ConfigKey.SORT_COLINDEX_1, sortColIndex1);
                            }
                            if (keys.size() > 1) {
                                SortKey sec = keys.get(1);
                                sortOrder2 = sec.getSortOrder();
                                config.setString(ConfigKey.SORT_ORDER_2, sortOrder2.toString());
                                sortColIndex2 = sec.getColumn();
                                config.setInt(ConfigKey.SORT_COLINDEX_2, sortColIndex2);
                            }
                        }
                    });
        }
    }

    private static class PokemonTableModel extends AbstractTableModel {

        PokemonTable pt;

        private final ArrayList<Pokemon> pokeCol = new ArrayList<>();
        private final ArrayList<Integer> numIdCol = new ArrayList<>();//0
        private final ArrayList<String> nickCol = new ArrayList<>(),//1
                speciesCol = new ArrayList<>(),//2
                ivCol = new ArrayList<>();//3
        private final ArrayList<Double> levelCol = new ArrayList<>();//4
        private final ArrayList<Integer> atkCol = new ArrayList<>(),//5
                defCol = new ArrayList<>(),//6
                stamCol = new ArrayList<>();//7
        private final ArrayList<String> type1Col = new ArrayList<>(),//8
                type2Col = new ArrayList<>(),//9
                move1Col = new ArrayList<>(),//10
                move2Col = new ArrayList<>();//11
        private final ArrayList<Integer> cpCol = new ArrayList<>(),//12
                hpCol = new ArrayList<>(),//13
                maxCpCurrentCol = new ArrayList<>(),//14
                maxCpCol = new ArrayList<>(),//15
                maxEvolvedCpCurrentCol = new ArrayList<>(),//16
                maxEvolvedCpCol = new ArrayList<>(),//17
                candiesCol = new ArrayList<>();//18
        private final ArrayList<String> candies2EvlvCol = new ArrayList<>();//19
        private final ArrayList<Integer> dustToLevelCol = new ArrayList<>();//20
        private final ArrayList<String> pokeballCol = new ArrayList<>(),//21
                caughtCol = new ArrayList<>(),//22
                favCol = new ArrayList<>();//23
        private final ArrayList<Long> duelAbilityCol = new ArrayList<>();//24
        private final ArrayList<Double> gymOffenseCol = new ArrayList<>();//25
        private final ArrayList<Long> gymDefenseCol = new ArrayList<>();//26
        private final ArrayList<String> move1RatingCol = new ArrayList<>(),//27
                move2RatingCol = new ArrayList<>();//28
        private final ArrayList<String> cpEvolvedCol = new ArrayList<>(),//29
                evolvableCol = new ArrayList<>();//30
        private final ArrayList<Long> duelAbilityIVCol = new ArrayList<>();//31
        private final ArrayList<Double> gymOffenseIVCol = new ArrayList<>();//32
        private final ArrayList<Long> gymDefenseIVCol = new ArrayList<>();//33

        @Deprecated
        private PokemonTableModel(PokemonGo go, List<Pokemon> pokes, PokemonTable pt) {
            this.pt = pt;
            MutableInt i = new MutableInt();
            pokes.forEach(p -> {
                pokeCol.add(i.getValue(), p);
                numIdCol.add(i.getValue(), p.getMeta().getNumber());
                nickCol.add(i.getValue(), p.getNickname());
                speciesCol.add(i.getValue(),
                        PokeHandler.getLocalPokeName(p));
                levelCol.add(i.getValue(), (double) p.getLevel());
                ivCol.add(i.getValue(), Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(p)));
                cpCol.add(i.getValue(), p.getCp());
                atkCol.add(i.getValue(), p.getIndividualAttack());
                defCol.add(i.getValue(), p.getIndividualDefense());
                stamCol.add(i.getValue(), p.getIndividualStamina());
                type1Col.add(i.getValue(), StringUtils.capitalize(p.getMeta().getType1().toString().toLowerCase()));
                type2Col.add(i.getValue(), StringUtils.capitalize(p.getMeta().getType2().toString().toLowerCase().replaceAll("none", "")));

                Double dps1 = PokemonUtils.dpsForMove(p, true);
                Double dps2 = PokemonUtils.dpsForMove(p, false);

                move1Col.add(i.getValue(), WordUtils.capitalize(p.getMove1().toString().toLowerCase().replaceAll("_fast", "").replaceAll("_", " ")) + " (" + String.format("%.2f", dps1) + "dps)");
                move2Col.add(i.getValue(), WordUtils.capitalize(p.getMove2().toString().toLowerCase().replaceAll("_", " ")) + " (" + String.format("%.2f", dps2) + "dps)");
                hpCol.add(i.getValue(), p.getMaxStamina());

                int trainerLevel = 1;
                try {
                    trainerLevel = go.getPlayerProfile().getStats().getLevel();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                // Max CP calculation for current Pokemon
                PokemonMeta pokemonMeta = PokemonMetaRegistry.getMeta(p.getPokemonId());
                int maxCpCurrent = 0, maxCp = 0;
                if (pokemonMeta == null) {
                    System.out.println("Error: Cannot find meta data for " + p.getPokemonId().name());
                } else {
                    int attack = p.getIndividualAttack() + pokemonMeta.getBaseAttack();
                    int defense = p.getIndividualDefense() + pokemonMeta.getBaseDefense();
                    int stamina = p.getIndividualStamina() + pokemonMeta.getBaseStamina();
                    maxCpCurrent = PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel);
                    maxCp = PokemonCpUtils.getMaxCp(attack, defense, stamina);
                    maxCpCurrentCol.add(i.getValue(), maxCpCurrent);
                    maxCpCol.add(i.getValue(), maxCp);
                }

                // Max CP calculation for highest evolution of current Pokemon
                PokemonFamilyId familyId = p.getPokemonFamily();
                PokemonId highestFamilyId = PokemonMetaRegistry.getHightestForFamily(familyId);

                // Eeveelutions exception handling
                if (familyId.getNumber() == PokemonFamilyId.FAMILY_EEVEE.getNumber()) {
                    if (p.getPokemonId().getNumber() == PokemonId.EEVEE.getNumber()) {
                        PokemonMeta vap = PokemonMetaRegistry.getMeta(PokemonId.VAPOREON);
                        PokemonMeta fla = PokemonMetaRegistry.getMeta(PokemonId.FLAREON);
                        PokemonMeta jol = PokemonMetaRegistry.getMeta(PokemonId.JOLTEON);
                        if (vap != null && fla != null && jol != null) {
                            Comparator<PokemonMeta> cMeta = (m1, m2) -> {
                                int comb1 = PokemonCpUtils.getMaxCp(m1.getBaseAttack(), m1.getBaseDefense(), m1.getBaseStamina());
                                int comb2 = PokemonCpUtils.getMaxCp(m2.getBaseAttack(), m2.getBaseDefense(), m2.getBaseStamina());
                                return comb1 - comb2;
                            };
                            highestFamilyId = PokemonId.forNumber(Collections.max(Arrays.asList(vap, fla, jol), cMeta).getNumber());
                        }
                    } else {
                        // This is one of the eeveelutions, so PokemonMetaRegistry.getHightestForFamily() returns Eevee.
                        // We correct that here
                        highestFamilyId = p.getPokemonId();
                    }
                }

                PokemonMeta highestFamilyMeta = PokemonMetaRegistry.getMeta(highestFamilyId);
                if (highestFamilyId == p.getPokemonId()) {
                    maxEvolvedCpCurrentCol.add(i.getValue(), maxCpCurrent);
                    maxEvolvedCpCol.add(i.getValue(), maxCp);
                    cpEvolvedCol.add(i.getValue(), "-");
                } else if (highestFamilyMeta == null) {
                    System.out.println("Error: Cannot find meta data for " + highestFamilyId.name());
                } else {
                    int attack = highestFamilyMeta.getBaseAttack() + p.getIndividualAttack();
                    int defense = highestFamilyMeta.getBaseDefense() + p.getIndividualDefense();
                    int stamina = highestFamilyMeta.getBaseStamina() + p.getIndividualStamina();
                    maxEvolvedCpCurrentCol.add(i.getValue(), PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel));
                    maxEvolvedCpCol.add(i.getValue(), PokemonCpUtils.getMaxCp(attack, defense, stamina));
                    cpEvolvedCol.add(i.getValue(), String.valueOf(PokemonCpUtils.getCpForPokemonLevel(attack, defense, stamina, p.getLevel())));
                }

                int candies = 0;
                try {
                    candies = p.getCandy();
                    candiesCol.add(i.getValue(), candies);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (p.getCandiesToEvolve() != 0) {
                    candies2EvlvCol.add(i.getValue(), String.valueOf(p.getCandiesToEvolve()));
                    evolvableCol.add(i.getValue(), String.valueOf((int)((double) candies / p.getCandiesToEvolve()))); // Rounded down candies / toEvolve
                }
                else {
                    candies2EvlvCol.add(i.getValue(), "-");
                    evolvableCol.add(i.getValue(), "-");
                }
                dustToLevelCol.add(i.getValue(), p.getStardustCostsForPowerup());
                pokeballCol.add(i.getValue(), WordUtils.capitalize(p.getPokeball().toString().toLowerCase().replaceAll("item_", "").replaceAll("_", " ")));
                caughtCol.add(i.getValue(), DateHelper.toString(DateHelper.fromTimestamp(p.getCreationTimeMs())));
                favCol.add(i.getValue(), (p.isFavorite()) ? "True" : "");
                duelAbilityCol.add(i.getValue(), PokemonUtils.duelAbility(p, false));
                gymOffenseCol.add(i.getValue(), PokemonUtils.gymOffense(p, false));
                gymDefenseCol.add(i.getValue(), PokemonUtils.gymDefense(p, false));
                duelAbilityIVCol.add(i.getValue(), PokemonUtils.duelAbility(p, true));
                gymOffenseIVCol.add(i.getValue(), PokemonUtils.gymOffense(p, true));
                gymDefenseIVCol.add(i.getValue(), PokemonUtils.gymDefense(p, true));
                move1RatingCol.add(i.getValue(), PokemonUtils.moveRating(p, true));
                move2RatingCol.add(i.getValue(), PokemonUtils.moveRating(p, false));
                i.increment();
            });
        }

        private Pokemon getPokemonByIndex(int i) {
            try {
                return pokeCol.get(pt.convertRowIndexToModel(i));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Id";
                case 1:
                    return "Nickname";
                case 2:
                    return "Species";
                case 3:
                    return "IV %";
                case 4:
                    return "Lvl";
                case 5:
                    return "Atk";
                case 6:
                    return "Def";
                case 7:
                    return "Stam";
                case 8:
                    return "Type 1";
                case 9:
                    return "Type 2";
                case 10:
                    return "Move 1";
                case 11:
                    return "Move 2";
                case 12:
                    return "CP";
                case 13:
                    return "HP";
                case 14:
                    return "Max CP (Cur)";
                case 15:
                    return "Max CP (40)";
                case 16:
                    return "Max Evolved CP (Cur)";
                case 17:
                    return "Max Evolved CP (40)";
                case 18:
                    return "Candies";
                case 19:
                    return "To Evolve";
                case 20:
                    return "Stardust";
                case 21:
                    return "Caught With";
                case 22:
                    return "Time Caught";
                case 23:
                    return "Favorite";
                case 24:
                    return "Duel Ability";
                case 25:
                    return "Gym Offense";
                case 26:
                    return "Gym Defense";
                case 27:
                    return "Move 1 Rating";
                case 28:
                    return "Move 2 Rating";
                case 29:
                    return "CP Evolved";
                case 30:
                    return "Evolvable";
                case 31:
                    return "Duel Ability IV";
                case 32:
                    return "Gym Offense IV";
                case 33:
                    return "Gym Defense IV";
                default:
                    return "UNKNOWN?";
            }
        }

        @Override
        public int getColumnCount() {
            return 34;
        }

        @Override
        public int getRowCount() {
            return pokeCol.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return numIdCol.get(rowIndex);
                case 1:
                    return nickCol.get(rowIndex);
                case 2:
                    return speciesCol.get(rowIndex);
                case 3:
                    return ivCol.get(rowIndex);
                case 4:
                    return levelCol.get(rowIndex);
                case 5:
                    return atkCol.get(rowIndex);
                case 6:
                    return defCol.get(rowIndex);
                case 7:
                    return stamCol.get(rowIndex);
                case 8:
                    return type1Col.get(rowIndex);
                case 9:
                    return type2Col.get(rowIndex);
                case 10:
                    return move1Col.get(rowIndex);
                case 11:
                    return move2Col.get(rowIndex);
                case 12:
                    return cpCol.get(rowIndex);
                case 13:
                    return hpCol.get(rowIndex);
                case 14:
                    return maxCpCurrentCol.get(rowIndex);
                case 15:
                    return maxCpCol.get(rowIndex);
                case 16:
                    return maxEvolvedCpCurrentCol.get(rowIndex);
                case 17:
                    return maxEvolvedCpCol.get(rowIndex);
                case 18:
                    return candiesCol.get(rowIndex);
                case 19:
                    return candies2EvlvCol.get(rowIndex);
                case 20:
                    return dustToLevelCol.get(rowIndex);
                case 21:
                    return pokeballCol.get(rowIndex);
                case 22:
                    return caughtCol.get(rowIndex);
                case 23:
                    return favCol.get(rowIndex);
                case 24:
                    return duelAbilityCol.get(rowIndex);
                case 25:
                    return gymOffenseCol.get(rowIndex);
                case 26:
                    return gymDefenseCol.get(rowIndex);
                case 27:
                    return move1RatingCol.get(rowIndex);
                case 28:
                    return move2RatingCol.get(rowIndex);
                case 29:
                    return cpEvolvedCol.get(rowIndex);
                case 30:
                    return evolvableCol.get(rowIndex);
                case 31:
                    return duelAbilityIVCol.get(rowIndex);
                case 32:
                    return gymOffenseIVCol.get(rowIndex);
                case 33:
                    return gymDefenseIVCol.get(rowIndex);
                default:
                    return null;
            }
        }
    }
}
