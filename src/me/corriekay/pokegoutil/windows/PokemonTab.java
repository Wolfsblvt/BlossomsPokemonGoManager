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
                if (event.getValueIsAdjusting()) {
                    // We need a break here. Cause otherwise mouse selection would trigger twice. (Yeah, that's swing)
                    return;
                }
                if (event.getSource() == pt.getSelectionModel() && pt.getRowSelectionAllowed()) {
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

        // Pokemon name language drop down
        String[] locales = { "en", "de", "fr", "ru", "zh_CN", "zh_HK", "ja" };
        JComboBox<String> pokelang = new JComboBox<>(locales);
        String locale = config.getString(ConfigKey.LANGUAGE);
        pokelang.setSelectedItem(locale);
        pokelang.addActionListener(e -> new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                String lang = (String) pokelang.getSelectedItem();
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
                String size = fontSize.getSelectedItem().toString();
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
            System.out.println(String.format(
                    "Doing Rename %d of %d",
                    total.getValue(),
                    selection.size()));
            total.increment();

            PokeNick pokeNick = PokeHandler.generatePokemonNickname(renamePattern, pokemon);

            // We check if the Pokemon was skipped
            boolean isSkipped = (pokeNick.equals(pokemon.getNickname())
                    && renameResult.getNumber() == NicknamePokemonResponse.Result.UNSET_VALUE);
            if (isSkipped) {
                System.out.println(String.format(
                        "Skipped renaming %s, already named %s",
                        PokeHandler.getLocalPokeName(pokemon),
                        pokemon.getNickname()));
                skipped.increment();
                return;
            }

            if (renameResult.getNumber() == NicknamePokemonResponse.Result.SUCCESS_VALUE) {
                success.increment();
                if (pokeNick.isTooLong()) {
                    System.out.println(String.format(
                            "WARNING: Nickname \"%s\" is too long. Get's cut to: %s",
                            pokeNick.fullNickname,
                            pokeNick.toString()));
                }
                System.out.println(String.format(
                        "Renaming %s from \"%s\" to \"%s\", Result: Success!",
                        PokeHandler.getLocalPokeName(pokemon),
                        pokemon.getNickname(),
                        PokeHandler.generatePokemonNickname(renamePattern, pokemon)));
            } else {
                err.increment();
                System.out.println(String.format(
                        "Renaming %s failed! Code: %s; Nick: %s",
                        PokeHandler.getLocalPokeName(pokemon),
                        renameResult.toString(),
                        PokeHandler.generatePokemonNickname(renamePattern, pokemon)));
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

        if (!confirmOperation("Transfer", selection))
            return;

        MutableInt err = new MutableInt(),
                skipped = new MutableInt(),
                success = new MutableInt(),
                total = new MutableInt(1);

        selection.forEach(poke -> {
            System.out.println(String.format("Doing Transfer %d of %d", total.getValue(), selection.size()));
            total.increment();
            if (poke.isFavorite()) {
                System.out.println(String.format(
                        "%s with %d CP is favorite, skipping.",
                        PokeHandler.getLocalPokeName(poke),
                        poke.getCp()));
                skipped.increment();
                return;
            }

            if (!poke.getDeployedFortId().isEmpty()) {
                System.out.println(String.format(
                        "%s with %d CP is in gym, skipping.",
                        PokeHandler.getLocalPokeName(poke),
                        poke.getCp()));
                skipped.increment();
                return;
            }

            try {
                int candies = poke.getCandy();
                ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result transferResult = poke.transferPokemon();

                if (transferResult == ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result.SUCCESS) {
                    int newCandies = poke.getCandy();
                    System.out.println(String.format(
                            "Transferring %s, Result: Success!",
                            PokeHandler.getLocalPokeName(poke)));
                    System.out.println(String.format(
                            "Stat changes: (Candies : %d[+%d])",
                            newCandies,
                            (newCandies - candies)));
                    success.increment();
                } else {
                    System.out.println(String.format(
                            "Error transferring %s, result: %s",
                            PokeHandler.getLocalPokeName(poke),
                            transferResult.toString()));
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
                System.out.println(String.format(
                        "Error transferring %s! %s",
                        PokeHandler.getLocalPokeName(poke),
                        Utilities.getRealExceptionMessage(e)));
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

    private void evolveSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() == 0)
            return;

        if (!confirmOperation("Evolve", selection))
            return;

        MutableInt err = new MutableInt(),
                skipped = new MutableInt(),
                success = new MutableInt(),
                total = new MutableInt(1);

        selection.forEach(poke -> {
            System.out.println(String.format(
                    "Doing Evolve %d of %d",
                    total.getValue(),
                    selection.size()));
            total.increment();

            if (!poke.getDeployedFortId().isEmpty()) {
                System.out.println(String.format(
                        "%s with %d CP is in gym, skipping.",
                        PokeHandler.getLocalPokeName(poke),
                        +poke.getCp()));
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
                    System.out.println(String.format(
                            "Error. Not enough candy to evolve %s. %d available, %d needed.",
                            PokeHandler.getLocalPokeName(poke),
                            candies,
                            candiesToEvolve));
                    return;
                }

                EvolutionResult evolutionResultWrapper = poke.evolve();
                if (evolutionResultWrapper.isSuccessful()) {
                    Pokemon newPoke = evolutionResultWrapper.getEvolvedPokemon();
                    int newCandies = newPoke.getCandy();
                    int newCp = newPoke.getCp();
                    int newHp = newPoke.getStamina();
                    int candyRefund = 1;
                    System.out.println(String.format(
                            "Evolving %s. Evolve result: %s",
                            PokeHandler.getLocalPokeName(poke),
                            evolutionResultWrapper.getResult().toString()));
                    if (config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE)) {
                        if (newPoke.isFavorite()) {
                            System.out.println(String.format(
                                    "Skipping \"Transfer After Evolve\" for %s because favorite.",
                                    StringUtils.capitalize(newPoke.getPokemonId().toString().toLowerCase())));

                            System.out.println(String.format(
                                    "Stat changes: "
                                            + "(Candies: %d[%d-%d+%d], "
                                            + "CP: %d[+%d], "
                                            + "HP: %d[+%d])",
                                    newCandies, candies, candiesToEvolve, candyRefund,
                                    newCp, (newCp - cp),
                                    newHp, (newHp - hp)));
                        } else {
                            ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result result = newPoke
                                    .transferPokemon();
                            if (result == ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result.SUCCESS) {
                                newCandies = newPoke.getCandy();
                                candyRefund++;
                            }
                            System.out.println(String.format(
                                    "Transferring %s, Result: %s",
                                    StringUtils.capitalize(newPoke.getPokemonId().toString().toLowerCase()),
                                    result));
                            System.out.println(String.format(
                                    "Stat changes: (Candies: %d[%d-%d+%d]",
                                    newCandies,
                                    candies,
                                    candiesToEvolve,
                                    candyRefund));
                        }
                    } else {
                        System.out.println(String.format(
                                "Stat changes: "
                                        + "(Candies: %d[%d-%d+1], "
                                        + "CP: %d[+%d], "
                                        + "HP: %d[+%d])",
                                newCandies, candies, candiesToEvolve, candyRefund,
                                newCp, (newCp - cp),
                                newHp, (newHp - hp)));
                    }
                    go.getInventories().updateInventories(true);
                    success.increment();
                } else {
                    err.increment();
                    System.out.println(String.format(
                            "Error evolving %s, result: %s",
                            PokeHandler.getLocalPokeName(poke),
                            evolutionResultWrapper.getResult().toString()));
                }

                // If not last element, sleep until the next one
                if (!selection.get(selection.size() - 1).equals(poke)) {
                    int sleepMin = config.getInt(ConfigKey.DELAY_EVOLVE_MIN);
                    int sleepMax = config.getInt(ConfigKey.DELAY_EVOLVE_MAX);
                    Utilities.sleepRandom(sleepMin, sleepMax);
                }
            } catch (Exception e) {
                err.increment();
                System.out.println(String.format(
                        "Error evolving %s! %s",
                        PokeHandler.getLocalPokeName(poke),
                        Utilities.getRealExceptionMessage(e)));
            }
        });
        try {
            go.getInventories().updateInventories(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText(String.format(
                "Pokémon batch evolve%s complete!",
                (config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE) ? "/transfer" : "")),
                selection.size(), success, skipped, err);
    }

    private void powerUpSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() == 0)
            return;

        if (!confirmOperation("PowerUp", selection))
            return;

        MutableInt err = new MutableInt(),
                skipped = new MutableInt(),
                success = new MutableInt(),
                total = new MutableInt(1);

        selection.forEach(poke -> {
            try {
                System.out.println(String.format("Doing Power Up %d of %d",
                        total.getValue(),
                        selection.size()));
                total.increment();
                if (!poke.getDeployedFortId().isEmpty()) {
                    System.out.println(String.format(
                            "%s with %d CP is in gym, skipping.",
                            PokeHandler.getLocalPokeName(poke),
                            +poke.getCp()));
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
                    System.out.println(String.format(
                            "Error. Not enough candy/stardust to power up %s. "
                                    + "Stardust: %d/%d, "
                                    + "Candy: %d/%d",
                            PokeHandler.getLocalPokeName(poke),
                            stardust, stardustToPowerUp,
                            candies, candiesToPowerUp));
                    return;
                }

                UpgradePokemonResponseOuterClass.UpgradePokemonResponse.Result upgradeResult = poke.powerUp();
                go.getPlayerProfile().updateProfile();
                if (upgradeResult == UpgradePokemonResponseOuterClass.UpgradePokemonResponse.Result.SUCCESS) {
                    int newCandies = poke.getCandy();
                    int newCp = poke.getCp();
                    int newHp = poke.getMaxStamina();
                    System.out.println(String.format(
                            "Powering Up %s, Result: Success!",
                            PokeHandler.getLocalPokeName(poke)));

                    System.out.println(String.format(
                            "Stat changes: "
                                    + "(Candies : %d[%d-%d], "
                                    + "CP: %d[+%d], "
                                    + "HP: %d[+%d], "
                                    + "Stardust used %d[remainding: %d])",
                            newCandies, candies, candiesToPowerUp,
                            newCp, (newCp - cp),
                            newHp, (newHp - hp),
                            stardustToPowerUp,
                            go.getPlayerProfile().getCurrency(Currency.STARDUST)));

                    success.increment();
                } else {
                    err.increment();
                    System.out.println(String.format(
                            "Error powering up %s, result: %s",
                            PokeHandler.getLocalPokeName(poke),
                            upgradeResult.toString()));
                }

                // If not last element, sleep until the next one
                if (!selection.get(selection.size() - 1).equals(poke)) {
                    int sleepMin = config.getInt(ConfigKey.DELAY_POWERUP_MIN);
                    int sleepMax = config.getInt(ConfigKey.DELAY_POWERUP_MAX);
                    Utilities.sleepRandom(sleepMin, sleepMax);
                }
            } catch (Exception e) {
                err.increment();
                System.out.println(String.format(
                        "Error powering up %s! %s",
                        PokeHandler.getLocalPokeName(poke),
                        Utilities.getRealExceptionMessage(e)));
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

    // feature added by Ben Kauffman
    private void toggleFavorite() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() == 0)
            return;

        if (!confirmOperation("Toggle Favorite", selection))
            return;

        MutableInt err = new MutableInt(),
                skipped = new MutableInt(),
                success = new MutableInt(),
                total = new MutableInt(1);

        selection.forEach(poke -> {
            try {
                System.out.println(String.format(
                        "Toggling favorite %d of %d",
                        total.getValue(),
                        selection.size()));
                total.increment();
                SetFavoritePokemonResponseOuterClass.SetFavoritePokemonResponse.Result favoriteResult = poke
                        .setFavoritePokemon(!poke.isFavorite());
                System.out.println(String.format(
                        "Attempting to set favorite for %s to %b...",
                        PokeHandler.getLocalPokeName(poke),
                        !poke.isFavorite()));
                go.getPlayerProfile().updateProfile();

                if (favoriteResult == SetFavoritePokemonResponseOuterClass.SetFavoritePokemonResponse.Result.SUCCESS) {
                    System.out.println(String.format(
                            "Favorite for %s set to %b, Result: Seccess!",
                            PokeHandler.getLocalPokeName(poke),
                            !poke.isFavorite()));
                    success.increment();
                } else {
                    err.increment();
                    System.out.println(String.format(
                            "Error toggling favorite for %s, result: %s",
                            PokeHandler.getLocalPokeName(poke),
                            favoriteResult.toString()));
                }

                // If not last element, sleep until the next one
                if (!selection.get(selection.size() - 1).equals(poke)) {
                    int sleepMin = config.getInt(ConfigKey.DELAY_FAVORITE_MIN);
                    int sleepMax = config.getInt(ConfigKey.DELAY_FAVORITE_MAX);
                    Utilities.sleepRandom(sleepMin, sleepMax);
                }
            } catch (Exception e) {
                err.increment();
                System.out.println(String.format(
                        "Error toggling favorite for %s! %s",
                        PokeHandler.getLocalPokeName(poke),
                        Utilities.getRealExceptionMessage(e)));
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
            message = String.format("You want to rename %d Pokémon.", pokes.size())
                    + "\nYou can rename with normal text and patterns, or both combined. "
                    + "Patterns are going to be replaced with the Pokémons values."
                    + "\nExisting patterns:\n";
            for (PokeHandler.ReplacePattern pattern : PokeHandler.ReplacePattern.values()) {
                message += String.format("%% %s%% -> %s\n",
                        pattern.name().toLowerCase(),
                        pattern.toString());
            }
            message += "\n";
        }

        String input = JOptionPane.showInputDialog(panel, message, operation, JOptionPane.PLAIN_MESSAGE);
        return input;
    }

    private boolean confirmOperation(String operation, ArrayList<Pokemon> pokes) {
        JPanel panel = _buildPanelForOperation(operation, pokes);

        int response = JOptionPane.showConfirmDialog(null, panel,
                String.format(
                        "Please confirm %s of %d Pokémon",
                        operation,
                        pokes.size()),
                JOptionPane.OK_CANCEL_OPTION,
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
            String str = String.format("%s - CP: %d, IV: %s%%",
                    PokeHandler.getLocalPokeName(p),
                    p.getCp(),
                    Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(p)));

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provide custom formatting for the moveset ranking columns while allowing sorting on original values
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
