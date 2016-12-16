package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.api.pokemon.Pokemon;

import me.corriekay.pokegoutil.data.enums.BatchOperation;
import me.corriekay.pokegoutil.data.enums.PokeColumn;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.LDocumentListener;
import me.corriekay.pokegoutil.utils.helpers.LocationHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler.ReplacePattern;
import me.corriekay.pokegoutil.utils.pokemon.PokeNick;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCalculationUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.ui.GhostText;
import me.corriekay.pokegoutil.utils.windows.PokemonTable;
import me.corriekay.pokegoutil.utils.windows.PokemonTableModel;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse;
import POGOProtos.Networking.Responses.SetFavoritePokemonResponseOuterClass.SetFavoritePokemonResponse;
import POGOProtos.Networking.Responses.UpgradePokemonResponseOuterClass.UpgradePokemonResponse;

/**
 * The main PokemonTab.
 */
@SuppressWarnings("serial")
public class PokemonTab extends JPanel {

    private final PokemonGo go;
    private final PokemonTable pt;
    private static final JTextField searchBar = new JTextField("");
    private static final JTextField ivTransfer = new JTextField("", 20);
    private static final ConfigNew config = ConfigNew.getConfig();

    // Used constants
    private static final int WHEN_TO_SHOW_SELECTION_TITLE = 2;
    private static final String GYM_SKIPPED_MESSAGE_UNFORMATTED = "%s with %d CP is in gym, skipping.";
    private static final int POPUP_WIDTH = 500;
    private static final int POPUP_HEIGHT = 400;
    private static final int MIN_FONT_SIZE = 2;

    /**
     * Creates an instance of the PokemonTab.
     *
     * @param go The go api class.
     */
    public PokemonTab(final PokemonGo go) {
        super();

        setLayout(new BorderLayout());
        this.go = go;
        pt = new PokemonTable(go);
        final JPanel topPanel = new JPanel(new GridBagLayout());
        final JButton refreshPkmn = new JButton("Refresh List"),
            renameSelected = new JButton(BatchOperation.RENAME.toString()),
            transferSelected = new JButton(BatchOperation.TRANSFER.toString()),
            evolveSelected = new JButton(BatchOperation.EVOLVE.toString()),
            powerUpSelected = new JButton(BatchOperation.POWER_UP.toString()),
            toggleFavorite = new JButton(BatchOperation.FAVORITE.toString());

        pt.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                // We need a break here. Cause otherwise mouse selection would trigger twice. (Yeah, that's swing)
                return;
            }
            if (event.getSource() == pt.getSelectionModel() && pt.getRowSelectionAllowed()) {
                final int selectedRows = pt.getSelectedRowCount();
                if (selectedRows >= WHEN_TO_SHOW_SELECTION_TITLE) {
                    PokemonGoMainWindow.getInstance().setTitle(selectedRows + " Pokémon selected");
                } else {
                    PokemonGoMainWindow.getInstance().refreshTitle();
                }
            }
        });

        final GridBagConstraints gbc = new GridBagConstraints();
        topPanel.add(refreshPkmn, gbc);
        refreshPkmn.addActionListener(l -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                refreshPkmn();
                return null;
            }
        }.execute());
        topPanel.add(renameSelected, gbc);
        renameSelected.addActionListener(l -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                renameSelected();
                return null;
            }
        }.execute());
        topPanel.add(transferSelected, gbc);
        transferSelected.addActionListener(l -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                transferSelected();
                return null;
            }
        }.execute());
        topPanel.add(evolveSelected, gbc);
        evolveSelected.addActionListener(l -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                evolveSelected();
                return null;
            }
        }.execute());
        topPanel.add(powerUpSelected, gbc);
        powerUpSelected.addActionListener(l -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                powerUpSelected();
                return null;
            }
        }.execute());
        topPanel.add(toggleFavorite, gbc);
        toggleFavorite.addActionListener(l -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                toggleFavorite();
                return null;
            }
        }.execute());

        ivTransfer.addKeyListener(
            new KeyListener() {
                @Override
                public void keyPressed(final KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                        new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() {
                                selectLessThanIv();
                                return null;
                            }
                        }.execute();
                    }
                }

                @Override
                public void keyTyped(final KeyEvent event) {
                    // nothing here
                }

                @Override
                public void keyReleased(final KeyEvent event) {
                    // nothing here
                }
            });

        topPanel.add(ivTransfer, gbc);

        new GhostText(ivTransfer, "Pokemon IV");
        final JButton transferIv = new JButton("Select Pokemon < IV");
        transferIv.addActionListener(l -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
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
        final String[] locales = {"en", "de", "fr", "ru", "zh_CN", "zh_HK", "ja"};
        final JComboBox<String> pokelang = new JComboBox<>(locales);
        pokelang.setSelectedItem(config.getString(ConfigKey.LANGUAGE));
        pokelang.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                final String lang = (String) pokelang.getSelectedItem();
                changeLanguage(lang);
                return null;
            }
        }.execute());
        topPanel.add(pokelang);

        // Set font size if specified in config
        final Font font = pt.getFont();
        final int size = Math.max(MIN_FONT_SIZE, config.getInt(ConfigKey.FONT_SIZE, font.getSize()));
        if (size != font.getSize()) {
            pt.setFont(font.deriveFont((float) size));
        }

        // Font size dropdown
        final String[] sizes = {"8", "10", "11", "12", "14", "16", "18"};
        final JComboBox<String> fontSize = new JComboBox<>(sizes);
        fontSize.setSelectedItem(String.valueOf(size));
        fontSize.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                final String size = fontSize.getSelectedItem().toString();
                pt.setFont(pt.getFont().deriveFont(Float.parseFloat(size)));
                config.setInt(ConfigKey.FONT_SIZE, Integer.parseInt(size));
                return null;
            }
        }.execute());
        topPanel.add(fontSize);

        LDocumentListener.addChangeListener(searchBar, e -> refreshList());
        new GhostText(searchBar, "Search Pokémon...");

        add(topPanel, BorderLayout.NORTH);
        final JScrollPane sp = new JScrollPane(pt);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp, BorderLayout.CENTER);
    }

    private void changeLanguage(final String langCode) {
        config.setString(ConfigKey.LANGUAGE, langCode);

        // Check if cached locations should be deleted, because they are language dependant
        if (LocationHelper.locationFileExists() && JOptionPane.showConfirmDialog(null,
            "You have changed your language."
                + StringLiterals.NEWLINE + "The locations queried from the Google Locations Geocode API may be language-specific (eg local city and country names)."
                + StringLiterals.NEWLINE + "If you want the cached languages to be deleted, click YES."
                + StringLiterals.NEWLINE + "But do note that querying the locations again may take some time and may bring you over the API limit.",
            "Local Locations may be different",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
            // Delete the cached locations now
            LocationHelper.deleteCachedLocations();
        }

        refreshPkmn();
    }

    private void refreshPkmn() {
        try {
            go.getInventories().updateInventories(true);
            PokemonGoMainWindow.getInstance().refreshTitle();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(this::refreshList);
        System.out.println("Done refreshing Pokémon list");
    }

    private void showFinishedText(final String message, final int size, final MutableInt success, final MutableInt skipped, final MutableInt err) {
        final String finishText = message
            + "\nPokémon total: " + size
            + "\nSuccessful: " + success.getValue()
            + (skipped.getValue() > 0 ? "\nSkipped: " + skipped.getValue() : "")
            + (err.getValue() > 0 ? "\nErrors: " + err.getValue() : "");

        if (config.getBool(ConfigKey.SHOW_BULK_POPUP)) {
            JOptionPane.showMessageDialog(null, finishText, "Finished Operation", JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println(finishText);
        }
    }

    private void renameSelected() {
        final ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.isEmpty()) {
            return;
        }

        final PokeHandler handler = new PokeHandler(selection);
        final String renamePattern = inputOperation(BatchOperation.RENAME, selection);

        final MutableInt err = new MutableInt(),
            skipped = new MutableInt(),
            success = new MutableInt(),
            total = new MutableInt(1);

        final BiConsumer<NicknamePokemonResponse.Result, Pokemon> perPokeCallback = (renameResult, pokemon) -> {
            System.out.println(String.format(
                "Doing Rename %d of %d",
                total.getValue(),
                selection.size()));
            total.increment();

            final PokeNick pokeNick = PokeHandler.generatePokemonNickname(renamePattern, pokemon);

            // We check if the Pokemon was skipped
            final boolean isSkipped = pokeNick.toString().equals(pokemon.getNickname())
                && renameResult.getNumber() == NicknamePokemonResponse.Result.UNSET_VALUE;
            if (isSkipped) {
                System.out.println(String.format(
                    "Skipped renaming %s, already named \"%s\"",
                    PokemonUtils.getLocalPokeName(pokemon),
                    pokemon.getNickname()));
                skipped.increment();
                return;
            }

            if (renameResult.getNumber() == NicknamePokemonResponse.Result.SUCCESS_VALUE) {
                success.increment();
                if (pokeNick.isTooLong()) {
                    System.out.println(String.format(
                        "WARNING: Nickname \"%s\" is too long. Get's cut to: \"%s\"",
                        pokeNick.fullNickname,
                        pokeNick.toString()));
                }
                System.out.println(String.format(
                    "Renaming %s from \"%s\" to \"%s\", Result: Success!",
                    PokemonUtils.getLocalPokeName(pokemon),
                    pokemon.getNickname(),
                    PokeHandler.generatePokemonNickname(renamePattern, pokemon)));
            } else {
                err.increment();
                System.out.println(String.format(
                    "Renaming %s failed! Code: %s; Nick: \"%s\"",
                    PokemonUtils.getLocalPokeName(pokemon),
                    renameResult.toString(),
                    PokeHandler.generatePokemonNickname(renamePattern, pokemon)));
            }

            // If not last element and API was queried, sleep until the next one
            if (!selection.get(selection.size() - 1).equals(pokemon)) {
                final int sleepMin = config.getInt(ConfigKey.DELAY_RENAME_MIN);
                final int sleepMax = config.getInt(ConfigKey.DELAY_RENAME_MAX);
                Utilities.sleepRandom(sleepMin, sleepMax);
            }
        };

        handler.bulkRenameWithPattern(renamePattern, perPokeCallback);

        try {
            go.getInventories().updateInventories(true);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText("Pokémon batch rename complete!", selection.size(), success, skipped, err);
    }

    private void transferSelected() {
        final ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.isEmpty()) {
            return;
        }

        if (!confirmOperation(BatchOperation.TRANSFER, selection)) {
            return;
        }

        final MutableInt err = new MutableInt(),
            skipped = new MutableInt(),
            success = new MutableInt(),
            total = new MutableInt(1);

        selection.forEach(poke -> {
            System.out.println(String.format("Doing Transfer %d of %d", total.getValue(), selection.size()));
            total.increment();
            if (poke.isFavorite()) {
                System.out.println(String.format(
                    "%s with %d CP is favorite, skipping.",
                    PokemonUtils.getLocalPokeName(poke),
                    poke.getCp()));
                skipped.increment();
                return;
            }

            if (!poke.getDeployedFortId().isEmpty()) {
                System.out.println(String.format(
                    GYM_SKIPPED_MESSAGE_UNFORMATTED,
                    PokemonUtils.getLocalPokeName(poke),
                    poke.getCp()));
                skipped.increment();
                return;
            }

            try {
                final int candies = poke.getCandy();
                final ReleasePokemonResponse.Result transferResult = poke.transferPokemon();

                if (transferResult == ReleasePokemonResponse.Result.SUCCESS) {
                    final int newCandies = poke.getCandy();
                    System.out.println(String.format(
                        "Transferring %s, Result: Success!",
                        PokemonUtils.getLocalPokeName(poke)));
                    System.out.println(String.format(
                        "Stat changes: (Candies : %d[+%d])",
                        newCandies,
                        newCandies - candies));
                    success.increment();
                } else {
                    System.out.println(String.format(
                        "Error transferring %s, result: %s",
                        PokemonUtils.getLocalPokeName(poke),
                        transferResult.toString()));
                    err.increment();
                }

                // If not last element, sleep until the next one
                if (!selection.get(selection.size() - 1).equals(poke)) {
                    final int sleepMin = config.getInt(ConfigKey.DELAY_TRANSFER_MIN);
                    final int sleepMax = config.getInt(ConfigKey.DELAY_TRANSFER_MAX);
                    Utilities.sleepRandom(sleepMin, sleepMax);
                }
            } catch (final Exception e) {
                err.increment();
                System.out.println(String.format(
                    "Error transferring %s! %s",
                    PokemonUtils.getLocalPokeName(poke),
                    Utilities.getRealExceptionMessage(e)));
            }
        });
        try {
            go.getInventories().updateInventories(true);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText("Pokémon batch transfer complete!", selection.size(), success, skipped, err);

    }

    private void evolveSelected() {
        final ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.isEmpty()) {
            return;
        }

        if (!confirmOperation(BatchOperation.EVOLVE, selection)) {
            return;
        }

        final MutableInt err = new MutableInt(),
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
                    GYM_SKIPPED_MESSAGE_UNFORMATTED,
                    PokemonUtils.getLocalPokeName(poke),
                    +poke.getCp()));
                skipped.increment();
                return;
            }

            try {
                final int candies = poke.getCandy();
                final int candiesToEvolve = poke.getCandiesToEvolve();
                final int cp = poke.getCp();
                final int hp = poke.getMaxStamina();
                boolean afterTransfer = false;

                // Check if user has enough candy, otherwise we don't need to call server
                if (candies < candiesToEvolve) {
                    err.increment();
                    System.out.println(String.format(
                        "Error. Not enough candy to evolve %s. %d available, %d needed.",
                        PokemonUtils.getLocalPokeName(poke),
                        candies,
                        candiesToEvolve));
                    return;
                }

                // Check if pokemon is already highest evolution, otherwise we don't need to call server
                if (!poke.canEvolve()) {
                    skipped.increment();
                    System.out.println(String.format(
                        "Skipped evolving %s. Is already highest evolution.",
                        PokemonUtils.getLocalPokeName(poke)));
                    return;
                }

                final EvolutionResult evolutionResultWrapper = poke.evolve();
                if (evolutionResultWrapper.isSuccessful()) {
                    final Pokemon newPoke = evolutionResultWrapper.getEvolvedPokemon();
                    int newCandies = newPoke.getCandy();
                    final int newCp = newPoke.getCp();
                    final int newHp = newPoke.getStamina();
                    int candyRefund = 1;
                    System.out.println(String.format(
                        "Evolving %s. Evolve result: %s",
                        PokemonUtils.getLocalPokeName(poke),
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
                            final int sleepMin = config.getInt(ConfigKey.DELAY_EVOLVE_MIN);
                            final int sleepMax = config.getInt(ConfigKey.DELAY_EVOLVE_MAX);
                            Utilities.sleepRandom(sleepMin, sleepMax);

                            final ReleasePokemonResponse.Result result = newPoke
                                .transferPokemon();
                            afterTransfer = true;
                            if (result == ReleasePokemonResponse.Result.SUCCESS) {
                                newCandies = newPoke.getCandy();
                                candyRefund++;
                            }
                            System.out.println(String.format(
                                "Transferring %s, Result: %s",
                                StringUtils.capitalize(newPoke.getPokemonId().toString().toLowerCase()),
                                result));
                            System.out.println(String.format(
                                "Stat changes: (Candies: %d[%d-%d+%d])",
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
                        PokemonUtils.getLocalPokeName(poke),
                        evolutionResultWrapper.getResult().toString()));
                }

                // If not last element, sleep until the next one
                if (!selection.get(selection.size() - 1).equals(poke)) {
                    int sleepMin;
                    int sleepMax;
                    if (afterTransfer) {
                        sleepMin = config.getInt(ConfigKey.DELAY_TRANSFER_MIN);
                        sleepMax = config.getInt(ConfigKey.DELAY_TRANSFER_MAX);
                    } else {
                        sleepMin = config.getInt(ConfigKey.DELAY_EVOLVE_MIN);
                        sleepMax = config.getInt(ConfigKey.DELAY_EVOLVE_MAX);
                    }
                    Utilities.sleepRandom(sleepMin, sleepMax);
                }
            } catch (final Exception e) {
                err.increment();
                System.out.println(String.format(
                    "Error evolving %s! %s",
                    PokemonUtils.getLocalPokeName(poke),
                    Utilities.getRealExceptionMessage(e)));
            }
        });
        try {
            go.getInventories().updateInventories(true);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText(String.format(
                "Pokémon batch evolve%s complete!",
                (config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE) ? "/transfer" : "")),
            selection.size(), success, skipped, err);
    }

    private void powerUpSelected() {
        final ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.isEmpty()) {
            return;
        }

        if (!confirmOperation(BatchOperation.POWER_UP, selection)) {
            return;
        }

        final MutableInt err = new MutableInt(),
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
                        GYM_SKIPPED_MESSAGE_UNFORMATTED,
                        PokemonUtils.getLocalPokeName(poke),
                        +poke.getCp()));
                    skipped.increment();
                    return;
                }

                final int stardust = go.getPlayerProfile().getCurrency(Currency.STARDUST);
                final int candies = poke.getCandy();
                final int cp = poke.getCp();
                final int hp = poke.getMaxStamina();
                final int stardustToPowerUp = poke.getStardustCostsForPowerup();
                final int candiesToPowerUp = poke.getCandyCostsForPowerup();

                // Check if user has enough candy and stardust, otherwise we don't need to call server
                if (candies < candiesToPowerUp || stardust < stardustToPowerUp) {
                    err.increment();
                    System.out.println(String.format(
                        "Error. Not enough candy/stardust to power up %s. "
                            + "Stardust: %d/%d, "
                            + "Candy: %d/%d",
                        PokemonUtils.getLocalPokeName(poke),
                        stardust, stardustToPowerUp,
                        candies, candiesToPowerUp));
                    return;
                }

                // Check we aren't at max level, otherwise we don't need to call server
                if (poke.getCp() >= poke.getMaxCpForPlayer()) {
                    skipped.increment();
                    System.out.println(String.format(
                        "Skipping power-up of %s. It is already MaxCP: %d",
                        PokemonUtils.getLocalPokeName(poke),
                        poke.getCp()));
                    return;
                }

                final UpgradePokemonResponse.Result upgradeResult = poke.powerUp();
                go.getPlayerProfile().updateProfile();
                if (upgradeResult == UpgradePokemonResponse.Result.SUCCESS) {
                    final int newCandies = poke.getCandy();
                    final int newCp = poke.getCp();
                    final int newHp = poke.getMaxStamina();
                    System.out.println(String.format(
                        "Powering Up %s, Result: Success!",
                        PokemonUtils.getLocalPokeName(poke)));

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
                        PokemonUtils.getLocalPokeName(poke),
                        upgradeResult.toString()));
                }

                // If not last element, sleep until the next one
                if (!selection.get(selection.size() - 1).equals(poke)) {
                    final int sleepMin = config.getInt(ConfigKey.DELAY_POWERUP_MIN);
                    final int sleepMax = config.getInt(ConfigKey.DELAY_POWERUP_MAX);
                    Utilities.sleepRandom(sleepMin, sleepMax);
                }
            } catch (final Exception e) {
                err.increment();
                System.out.println(String.format(
                    "Error powering up %s! %s",
                    PokemonUtils.getLocalPokeName(poke),
                    Utilities.getRealExceptionMessage(e)));
            }
        });
        try {
            go.getInventories().updateInventories(true);
            PokemonGoMainWindow.getInstance().refreshTitle();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText("Pokémon batch powerup complete!", selection.size(), success, skipped, err);

    }

    // feature added by Ben Kauffman
    private void toggleFavorite() {
        final ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.isEmpty()) {
            return;
        }

        if (!confirmOperation(BatchOperation.FAVORITE, selection)) {
            return;
        }

        final MutableInt err = new MutableInt(),
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
                final SetFavoritePokemonResponse.Result favoriteResult = poke
                    .setFavoritePokemon(!poke.isFavorite());
                System.out.println(String.format(
                    "Attempting to set favorite for %s to %b...",
                    PokemonUtils.getLocalPokeName(poke),
                    !poke.isFavorite()));
                go.getPlayerProfile().updateProfile();

                if (favoriteResult == SetFavoritePokemonResponse.Result.SUCCESS) {
                    System.out.println(String.format(
                        "Favorite for %s set to %b, Result: Success!",
                        PokemonUtils.getLocalPokeName(poke),
                        !poke.isFavorite()));
                    success.increment();
                } else {
                    err.increment();
                    System.out.println(String.format(
                        "Error toggling favorite for %s, result: %s",
                        PokemonUtils.getLocalPokeName(poke),
                        favoriteResult.toString()));
                }

                // If not last element, sleep until the next one
                if (!selection.get(selection.size() - 1).equals(poke)) {
                    final int sleepMin = config.getInt(ConfigKey.DELAY_FAVORITE_MIN);
                    final int sleepMax = config.getInt(ConfigKey.DELAY_FAVORITE_MAX);
                    Utilities.sleepRandom(sleepMin, sleepMax);
                }
            } catch (final Exception e) {
                err.increment();
                System.out.println(String.format(
                    "Error toggling favorite for %s! %s",
                    PokemonUtils.getLocalPokeName(poke),
                    Utilities.getRealExceptionMessage(e)));
            }
        });
        try {
            PokemonGoMainWindow.getInstance().refreshTitle();
        } catch (final Exception e) {
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

        final double ivLessThan = Double.parseDouble(ivTransfer.getText());
        if (ivLessThan > 100 || ivLessThan < 0) {
            System.out.println("Please select a valid IV value (0-100)");
            return;
        }
        pt.clearSelection();
        System.out.println("Selecting Pokemon with IV less than: " + ivTransfer.getText());

        for (int i = 0; i < pt.getRowCount(); i++) {
            final double pIv = (double) pt.getValueAt(i, PokeColumn.IV_RATING.id) * 100;
            //final double pIv = Double.parseDouble((String) pt.getValueAt(i, 3));
            if (pIv < ivLessThan) {
                pt.getSelectionModel().addSelectionInterval(i, i);
            }
        }
    }

    /**
     * Handles.. idk why we have this. @author Cryptic
     *
     * @param operation operation to be done
     * @param pokes     list of pokemons
     * @return rename pattern
     */
    private String inputOperation(final BatchOperation operation, final ArrayList<Pokemon> pokes) {
        JPanel panel;
        String message = "";
        String savedPattern = "";

        switch (operation) {
            case RENAME:
                panel = buildPanelForRename();
                savedPattern = config.getString(ConfigKey.RENAME_PATTERN);
                message = "Renaming " + pokes.size() + " Pokémon.";
                break;
            default:
                panel = buildPanelForOperation(operation, pokes);
                break;
        }

        final String input = (String) JOptionPane.showInputDialog(null, panel, message, JOptionPane.PLAIN_MESSAGE, null, null, savedPattern);
        if (input != null) {
            switch (operation) {
                case RENAME:
                    config.setString(ConfigKey.RENAME_PATTERN, input);
                default:
                    break;
            }
        }
        return input;
    }

    /**
     * Prompt for confirmation before doing the operation.
     *
     * @param operation operation to be done
     * @param pokes     list of pokemons
     * @return ok was selected
     */
    private boolean confirmOperation(final BatchOperation operation, final ArrayList<Pokemon> pokes) {
        final JPanel panel = buildPanelForOperation(operation, pokes);

        final int response = JOptionPane.showConfirmDialog(null, panel,
            String.format(
                "Please confirm %s of %d Pokémon",
                operation,
                pokes.size()),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        return response == JOptionPane.OK_OPTION;
    }

    /**
     * Internal function to build the panel for a batch operation.
     *
     * @param operation The operation.
     * @param pokes     List of Pokémon for that operation.
     * @return The panel.
     */
    private JPanel buildPanelForOperation(final BatchOperation operation, final ArrayList<Pokemon> pokes) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setAlignmentX(CENTER_ALIGNMENT);

        final JScrollPane scroll = new JScrollPane(innerPanel);
        scroll.setAlignmentX(CENTER_ALIGNMENT);

        // Auto-height? Resizable? Haha. Funny joke.
        // I hate swing. But we need to get around here some way.
        // So lets get dirty.
        // We take 20 px for each row, 5 px buffer, and cap that at may 400
        // pixel.
        final int height = Math.min(400, pokes.size() * 20 + 5);
        panel.setPreferredSize(new Dimension(500, height));

        pokes.forEach(p -> {
            String str = String.format("%s - CP: %d, IV: %s%%",
                PokemonUtils.getLocalPokeName(p),
                p.getCp(),
                Utilities.percentageWithTwoCharacters(PokemonCalculationUtils.ivRating(p)));

            switch (operation) {
                case EVOLVE:
                    str += " Cost: " + p.getCandiesToEvolve();
                    str += p.getCandiesToEvolve() > 1 ? " Candies" : " Candy";
                    break;
                case POWER_UP:
                    str += " Cost: " + p.getCandyCostsForPowerup();
                    str += p.getCandyCostsForPowerup() > 1 ? " Candies" : " Candy";
                    str += " " + p.getStardustCostsForPowerup() + " Stardust";
                    break;
                case RENAME:
                    for (final PokeHandler.ReplacePattern pattern : PokeHandler.ReplacePattern.values()) {
                        str += StringLiterals.PERCENTAGE + pattern.name().toLowerCase() + "% -> " + pattern.toString() + "\n";
                    }
                    break;
                case TRANSFER:
                    break;
                default:
                    break;
            }
            innerPanel.add(new JLabel(str));
        });
        panel.add(scroll);
        return panel;
    }

    /**
     * Build the Popup panel for Renaming.
     *
     * @return The panel.
     */
    private JPanel buildPanelForRename() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        final JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setAlignmentX(LEFT_ALIGNMENT);

        final JScrollPane scroll = new JScrollPane(innerPanel);
        scroll.setAlignmentX(LEFT_ALIGNMENT);

        panel.setPreferredSize(new Dimension(POPUP_WIDTH, POPUP_HEIGHT));

        panel.add(new JLabel("You can rename with normal text and patterns, or both combined."));
        panel.add(new JLabel("Patterns are going to be replaced with the Pokémons values."));
        panel.add(new JLabel("Existing patterns: (double click on item to copy)"));

        final JList<ReplacePattern> listPattern = new JList<>(ReplacePattern.values());
        listPattern.setCellRenderer(new ReplacePatternRenderer());

        listPattern.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                @SuppressWarnings("unchecked")
                final
                JList<ReplacePattern> theList = ((JList<ReplacePattern>) mouseEvent.getSource());
                final boolean isDoubleClick = mouseEvent.getClickCount() == 2;
                if (isDoubleClick) {
                    final int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        final ReplacePattern replacePattern = theList.getModel().getElementAt(index);
                        final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                        cb.setContents(new StringSelection(StringLiterals.PERCENTAGE + replacePattern.name().toLowerCase() + StringLiterals.PERCENTAGE), null);
                    }
                }
            }
        });

        innerPanel.add(listPattern);
        panel.add(scroll);
        return panel;
    }

    public ArrayList<Pokemon> getSelectedPokemon() {
        final ArrayList<Pokemon> pokes = new ArrayList<>();
        final PokemonTableModel model = (PokemonTableModel) pt.getModel();
        for (final int i : pt.getSelectedRows()) {
            final Pokemon poke = model.getPokemonByIndex(i);
            if (poke != null) {
                pokes.add(poke);
            }
        }
        return pokes;
    }

    public void refreshList() {
        final List<Pokemon> pokes = new ArrayList<>();
        final String search = searchBar.getText().replaceAll(StringLiterals.SPACE, "").replaceAll(StringLiterals.UNDERSCORE, "").replaceAll("snek", "ekans")
            .toLowerCase();
        final String[] terms = search.split(";");
        try {
            go.getInventories().getPokebank().getPokemons()
                .stream().filter(p -> p.getPokemonId().getNumber() <= PokemonId.MEW_VALUE)
                .forEach(poke -> {
                    final boolean useFamilyName = config.getBool(ConfigKey.INCLUDE_FAMILY);
                    String familyName = "";
                    if (useFamilyName) {
                        // Try translating family name
                        try {
                            final PokemonId familyPokemonId = PokemonId.valueOf(poke.getPokemonFamily().toString().replaceAll(StringLiterals.FAMILY_PREFIX, ""));
                            familyName = PokemonUtils.getLocalPokeName(familyPokemonId.getNumber());
                        } catch (final IllegalArgumentException e) {
                            familyName = poke.getPokemonFamily().toString();
                        }
                    }

                    String searchme = Utilities.concatString(',',
                            PokemonUtils.getLocalPokeName(poke),
                            ((useFamilyName) ? familyName : ""),
                            poke.getNickname(),
                            poke.getMeta().getType1().toString(),
                            poke.getMeta().getType2().toString(),
                            poke.getMove1().toString(),
                            poke.getMove2().toString(),
                            poke.getPokeball().toString());
                    searchme = searchme.replaceAll("_FAST", "").replaceAll(StringLiterals.FAMILY_PREFIX, "").replaceAll("NONE", "")
                            .replaceAll("ITEM_", "").replaceAll(StringLiterals.UNDERSCORE, "").replaceAll(StringLiterals.SPACE, "").toLowerCase();

                    for (final String s : terms) {
                        if (searchme.contains(s)) {
                            pokes.add(poke);
                            // Break, so that a Pokémon isn't added twice even if it
                            // matches more than one criteria
                            break;
                        }
                    }
                });
            pt.constructNewTableModel(("".equals(search) || "searchpokémon...".equals(search)
                ? go.getInventories().getPokebank().getPokemons() : pokes));

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provide custom formatting for the list of patterns.
     */
    private class ReplacePatternRenderer extends JLabel implements ListCellRenderer<ReplacePattern> {
        /**
         * Constructor to create a ReplacePatternRenderer.
         */
        ReplacePatternRenderer() {
            super();
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(final JList<? extends ReplacePattern> list, final ReplacePattern value,
                                                      final int index, final boolean isSelected, final boolean cellHasFocus) {
            //Get the selected index. (The index param isn't always valid, so just use the value.)
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            final String str = StringLiterals.PERCENTAGE + value.name().toLowerCase() + StringLiterals.PERCENTAGE + " -> " + value.toString() + StringLiterals.NEWLINE;
            setText(str);
            setFont(list.getFont());

            return this;
        }
    }
}
