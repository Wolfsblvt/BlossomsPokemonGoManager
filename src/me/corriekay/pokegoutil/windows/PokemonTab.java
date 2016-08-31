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
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler.ReplacePattern;
import me.corriekay.pokegoutil.utils.ui.GhostText;
import me.corriekay.pokegoutil.utils.windows.PokemonTable;
import me.corriekay.pokegoutil.utils.windows.PokemonTableModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
                boolean afterTransfer = false;

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
                            // Sleep before transferring
                            int sleepMin = config.getInt(ConfigKey.DELAY_EVOLVE_MIN);
                            int sleepMax = config.getInt(ConfigKey.DELAY_EVOLVE_MAX);
                            Utilities.sleepRandom(sleepMin, sleepMax);

                            ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result result = newPoke
                                    .transferPokemon();
                            afterTransfer = true;
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
                                        + "(Candies: %d[%d-%d+%d], "
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
                    int sleepMin;
                    int sleepMax;
                    if (afterTransfer) {
                        sleepMin = config.getInt(ConfigKey.DELAY_TRANSFER_MIN);
                        sleepMax = config.getInt(ConfigKey.DELAY_TRANSFER_MAX);
                    }
                    else {
                        sleepMin = config.getInt(ConfigKey.DELAY_EVOLVE_MIN);
                        sleepMax = config.getInt(ConfigKey.DELAY_EVOLVE_MAX);
                    }
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
    	JPanel panel = null;
    	String message = "";
        String savedPattern = "";

        switch (operation) {
        	case "Rename":
        		panel = _buildPanelForRename();
	        	savedPattern = config.getString(ConfigKey.RENAME_PATTERN);
	        	message = "Renaming " + pokes.size() + " Pokémon.";
	        default:
//	    		panel = _buildPanelForOperation(operation, pokes);
        }

        String input = (String) JOptionPane.showInputDialog(null, panel, message, JOptionPane.PLAIN_MESSAGE, null, null, savedPattern);
        if(input!=null)  {
        	switch (operation) {
        	case "Rename":
        		config.setString(ConfigKey.RENAME_PATTERN, input);
        	}
        }
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
                	for (PokeHandler.ReplacePattern pattern : PokeHandler.ReplacePattern.values()) {
    	        		str += "%" + pattern.name().toLowerCase() + "% -> " + pattern.toString() + "\n";
    	        	}
                    break;
                case "Transfer":
                    break;
            }
            innerPanel.add(new JLabel(str));
        });
        panel.add(scroll);
        return panel;
    }
    
    private JPanel _buildPanelForRename() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setAlignmentX(LEFT_ALIGNMENT);
    	
    	JPanel innerPanel = new JPanel();
    	innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
    	innerPanel.setAlignmentX(LEFT_ALIGNMENT);
    	
    	JScrollPane scroll = new JScrollPane(innerPanel);
    	scroll.setAlignmentX(LEFT_ALIGNMENT);
    	
    	panel.setPreferredSize(new Dimension(500, 400));
    	
    	panel.add(new JLabel("You can rename with normal text and patterns, or both combined."));
    	panel.add(new JLabel("Patterns are going to be replaced with the Pokémons values."));
    	panel.add(new JLabel("Existing patterns: (double click on item to copy)"));

    	JList<ReplacePattern> listPattern = new JList<>(ReplacePattern.values());
    	listPattern.setCellRenderer(new ReplacePatternRenderer());
    	
    	listPattern.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent mouseEvent) {
                @SuppressWarnings("unchecked")
				JList<ReplacePattern> theList = ((JList<ReplacePattern>) mouseEvent.getSource());
                if (mouseEvent.getClickCount() == 2) {
                  int index = theList.locationToIndex(mouseEvent.getPoint());
                  if (index >= 0) {
                	  ReplacePattern o = (ReplacePattern) theList.getModel().getElementAt(index);
                	  Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                	  cb.setContents(new StringSelection("%" + o.name().toLowerCase() + "%"), null);
                  }
                }
              }
		});
    	
    	innerPanel.add(listPattern);
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
     * Provide custom formatting for the list of patterns
     */
    private class ReplacePatternRenderer extends JLabel implements ListCellRenderer<ReplacePattern>
	{
    	public ReplacePatternRenderer() {
    	    setOpaque(true);
    	}
    	
		@Override
		public Component getListCellRendererComponent(JList<? extends ReplacePattern> list, ReplacePattern value,
				int index, boolean isSelected, boolean cellHasFocus) {
			 //Get the selected index. (The index param isn't always valid, so just use the value.)
	        if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }

	        String str = "%" + value.name().toLowerCase() + "% -> " + value.toString() + "\n";
	        setText(str);
	        setFont(list.getFont());

	        return this;
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
