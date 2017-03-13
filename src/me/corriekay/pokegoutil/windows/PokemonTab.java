package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.ItemBag;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.request.RequestFailedException;

import POGOProtos.Enums.PokemonFamilyIdOuterClass.PokemonFamilyId;
import POGOProtos.Networking.Responses.GetInventoryResponseOuterClass.GetInventoryResponse;
import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse;
import POGOProtos.Networking.Responses.SetFavoritePokemonResponseOuterClass.SetFavoritePokemonResponse;
import POGOProtos.Networking.Responses.UpgradePokemonResponseOuterClass.UpgradePokemonResponse;
import me.corriekay.pokegoutil.data.enums.BatchOperation;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.EvolveHelper;
import me.corriekay.pokegoutil.utils.helpers.LocationHelper;
import me.corriekay.pokegoutil.utils.helpers.TriConsumer;
import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler.ReplacePattern;
import me.corriekay.pokegoutil.utils.pokemon.PokeNick;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCalculationUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.ui.IVTransferTextField;
import me.corriekay.pokegoutil.utils.ui.SearchBarTextField;
import me.corriekay.pokegoutil.utils.windows.PokemonTable;
import me.corriekay.pokegoutil.utils.windows.PokemonTableModel;

/**
 * The main PokemonTab.
 */
public class PokemonTab extends JPanel {

    public static final Map<Long, EvolveHelper> MAP_POKEMON_ITEM = new HashMap<Long, EvolveHelper>();
    private static final long serialVersionUID = -3356302801659055820L;
    private static PokemonGo go;
    
    // Used constants
    private static final String GYM_SKIPPED_MESSAGE_UNFORMATTED = "%s with %d CP is in gym, skipping.";
    private static final int WHEN_TO_SHOW_SELECTION_TITLE = 2;
    private static final int GRID_WIDTH = 3;
    private static final int POPUP_WIDTH = 500;
    private static final int POPUP_HEIGHT = 400;
    // Pokemon name language drop down
    private static final String[] LOCALES = {"en", "de", "fr", "ru", "zh_CN", "zh_HK", "ja"}; 
    private static final String[] SIZES = new String[] {"8", "10", "11", "12", "14", "16", "18"};

    private final PokemonTable pt;
    private final ConfigNew config = ConfigNew.getConfig();
    private List<Integer> selectedRowsList;

    /**
     * Creates an instance of the PokemonTab.
     * @param goInstance The go api class.
     */
    public PokemonTab(final PokemonGo goInstance) {
        super();

        setLayout(new BorderLayout());
        go = goInstance;
        pt = new PokemonTable();
        addAndInitializeComponents();
       
        pt.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                // We need a break here. Cause otherwise mouse selection would trigger twice. (Yeah, that's swing)
                return;
            }
            if (event.getSource() == pt.getSelectionModel() && pt.getRowSelectionAllowed()) {
                if (pt.getSelectionModel().getAnchorSelectionIndex() > -1 && pt.getSelectionModel().getLeadSelectionIndex() > -1) {
                    selectedRowsList = Arrays.stream(pt.getSelectedRows()).boxed().collect(Collectors.toList());
                }
                final int selectedRows = pt.getSelectedRowCount();
                if (selectedRows >= WHEN_TO_SHOW_SELECTION_TITLE) {
                    PokemonGoMainWindow.getInstance().setTitle(selectedRows + " Pokémon selected");
                } else {
                    PokemonGoMainWindow.getInstance().refreshTitle();
                }
            }
        });

        //Try to apply the specified font size to table
        pt.applySpecifiedFontSize();
        
        pt.constructNewTableModel(go.getInventories().getPokebank().getPokemons());
    }
    
    /**
     * Handle component creation and initialization inside PokemonTab constructor.
     */
    private void addAndInitializeComponents() {
        final JPanel topPanel = new JPanel(new GridBagLayout());
        final JButton refreshPkmn = new JButton("Refresh List"),
            renameSelected = new JButton(BatchOperation.RENAME.toString()),
            transferSelected = new JButton(BatchOperation.TRANSFER.toString()),
            evolveSelected = new JButton(BatchOperation.EVOLVE.toString()),
            powerUpSelected = new JButton(BatchOperation.POWER_UP.toString()),
            toggleFavorite = new JButton(BatchOperation.FAVORITE.toString()),
            setFavoriteYes = new JButton(BatchOperation.SET_FAVORITE_YES.toString()),
            setFavoriteNo = new JButton(BatchOperation.SET_FAVORITE_NO.toString());
        final IVTransferTextField ivTransfer = new IVTransferTextField(pt::selectLessThanIv);
        final JButton transferIv = new JButton("Select Pokemon < IV");
        final JComboBox<String> pokelang = new JComboBox<String>(LOCALES);
        final JComboBox<String> fontSize = new JComboBox<>(SIZES);

        final GridBagConstraints gbc = new GridBagConstraints();
        
        topPanel.add(refreshPkmn, gbc);
        topPanel.add(renameSelected, gbc);
        topPanel.add(transferSelected, gbc);
        topPanel.add(evolveSelected, gbc);
        topPanel.add(powerUpSelected, gbc);
        topPanel.add(toggleFavorite, gbc);
        topPanel.add(setFavoriteYes, gbc);
        topPanel.add(setFavoriteNo, gbc);
        topPanel.add(ivTransfer, gbc);
        topPanel.add(transferIv, gbc);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = GRID_WIDTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(new SearchBarTextField(pt::filterTable), gbc);
        
        topPanel.add(pokelang);
        topPanel.add(fontSize);

        add(topPanel, BorderLayout.NORTH);
        final JScrollPane sp = new JScrollPane(pt);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp, BorderLayout.CENTER);
        
        refreshPkmn.addActionListener(l -> new OperationWorker<Object>(this::refreshPkmn).execute());
        renameSelected.addActionListener(l -> new OperationWorker<Object>(this::renameSelected).execute());
        transferSelected.addActionListener(l -> new OperationWorker<Object>(this::transferSelected).execute());
        evolveSelected.addActionListener(l -> new OperationWorker<Object>(this::evolveSelected).execute());
        powerUpSelected.addActionListener(l -> new OperationWorker<Object>(this::powerUpSelected).execute());
        toggleFavorite.addActionListener(l -> new OperationWorker<BatchOperation>(this::toggleFavorite, BatchOperation.FAVORITE).execute());
        setFavoriteYes.addActionListener(l -> new OperationWorker<BatchOperation>(this::toggleFavorite, BatchOperation.SET_FAVORITE_YES).execute());
        setFavoriteNo.addActionListener(l -> new OperationWorker<BatchOperation>(this::toggleFavorite, BatchOperation.SET_FAVORITE_NO).execute());
        transferIv.addActionListener(l -> new OperationWorker<String>(pt::selectLessThanIv, ivTransfer.getText()).execute());

        pokelang.setSelectedItem(config.getString(ConfigKey.LANGUAGE));
        pokelang.addActionListener(e -> new OperationWorker<String>(this::changeLanguage, (String) pokelang.getSelectedItem()).execute());

        fontSize.setSelectedItem(String.valueOf(pt.getFont().getSize()));
        fontSize.addActionListener(e -> new OperationWorker<Object>(() -> {
            final String innerSize = fontSize.getSelectedItem().toString();
            pt.setFont(pt.getFont().deriveFont(Float.parseFloat(innerSize)));
            config.setInt(ConfigKey.FONT_SIZE, Integer.parseInt(innerSize));
        }).execute());
    }

    /**
     * Change the language of the pokemons using the langCode.
     * @param langCode language to change
     */
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

    /**
     * Try to obtain the updated list of Pokemons from server.
     */
    private void refreshPkmn() {
        try {
            final GetInventoryResponse updateInventories = go.getInventories().updateInventories(true);
            go.getInventories().updateInventories(updateInventories);
        } catch (RequestFailedException e) {
            System.out.println("Error obtaining updated list from server");
            ConsolePrintStream.printException(e);
        }
        PokemonGoMainWindow.getInstance().refreshTitle();
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
        final ArrayList<Pokemon> selection = pt.getSelectedPokemon();
        if (selection.isEmpty()) {
            return;
        }

        final PokeHandler handler = new PokeHandler(selection);
        final String renamePattern = (String) JOptionPane.showInputDialog(null, buildPanelForRename(), 
                "Renaming " + selection.size() + " Pokémon.", JOptionPane.PLAIN_MESSAGE, null, null, 
                config.getString(ConfigKey.RENAME_PATTERN));
        if (renamePattern != null) {
            config.setString(ConfigKey.RENAME_PATTERN, renamePattern);
        }

        final MutableInt err = new MutableInt(),
            skipped = new MutableInt(),
            success = new MutableInt(),
            total = new MutableInt(1);

        final TriConsumer<NicknamePokemonResponse.Result, Pokemon, String> perPokeCallback = (renameResult, pokemon, previousNickname) -> {
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
                    previousNickname,
                    PokeHandler.generatePokemonNickname(renamePattern, pokemon)));
            } else {
                err.increment();
                System.out.println(String.format(
                    "Renaming %s failed! Code: %s; Nick: \"%s\"",
                    PokemonUtils.getLocalPokeName(pokemon),
                    previousNickname,
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

        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText("Pokémon batch rename complete!", selection.size(), success, skipped, err);
    }

    private void transferSelected() {
        final ArrayList<Pokemon> selection = pt.getSelectedPokemon();
        final ArrayList<Pokemon> finalSelection = new ArrayList<Pokemon>();
        if (selection.isEmpty()) {
            return;
        }

        if (!confirmOperation(BatchOperation.TRANSFER, selection)) {
            return;
        }

        final MutableInt err = new MutableInt(),
            skipped = new MutableInt(),
            success = new MutableInt(),
            total = new MutableInt(selection.size());

        selection.forEach(poke -> {
            if (poke.isFavorite()) {
                System.out.println(String.format(
                    "%s with %d CP is favorite, skipping.",
                    PokemonUtils.getLocalPokeName(poke),
                    poke.getCp()));
                skipped.increment();
                return;
            }

            if (poke.isDeployed()) {
                System.out.println(String.format(
                    GYM_SKIPPED_MESSAGE_UNFORMATTED,
                    PokemonUtils.getLocalPokeName(poke),
                    poke.getCp()));
                skipped.increment();
                return;
            }

            if (go.getPlayerProfile().getBuddy() != null 
                    && poke.getId() == go.getPlayerProfile().getBuddy().getPokemon().getId()) {
                System.out.println(String.format(
                        "%s with %d CP is buddy, skipping.",
                        PokemonUtils.getLocalPokeName(poke),
                        poke.getCp()));
                skipped.increment();
                return;
            }
            finalSelection.add(poke);
        });
        
        if (finalSelection.size() > 0) {
            System.out.println(String.format("Multi-Transfering %d pokemons, %d skipped", finalSelection.size(), skipped.intValue()));
            try {
                final Map<PokemonFamilyId, Integer> transferResult = go.getInventories().getPokebank().releasePokemon(finalSelection.toArray(new Pokemon[1]));
                transferResult.forEach((pokeFamilyID, candies) -> {
                    System.out.println(String.format(
                            "Transferred %s (Family): Candies : %d[+%d]",
                            StringUtils.capitalize(pokeFamilyID.toString().toLowerCase().replace("family_", "")),
                            go.getInventories().getCandyjar().getCandies(pokeFamilyID),
                            candies));
                });
                success.add(finalSelection.size());
            } catch (RequestFailedException e) {
                err.add(finalSelection.size());
                System.out.println(String.format(
                        "Error transferring pokemons! %s",
                        Utilities.getRealExceptionMessage(e)));
            }
        }
        selectedRowsList = null;
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText("Pokémon multi-transfer complete!", total.intValue(), success, skipped, err);
    }

    private void evolveSelected() {
        final ArrayList<Pokemon> selection = pt.getSelectedPokemon();
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

                final EvolutionResult evolutionResultWrapper;
                final EvolveHelper evolve = MAP_POKEMON_ITEM.get(poke.getId());
                if (evolve != null && evolve.isEvolveWithItem()) {
                    evolutionResultWrapper = poke.evolve(evolve.getItemToEvolveId());
                } else {
                    evolutionResultWrapper = poke.evolve();
                }
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
                                newCp, newCp - cp,
                                newHp, newHp - hp));
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
                            removeSelection(poke);
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
                            newCp, newCp - cp,
                            newHp, newHp - hp));
                    }
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
                    final int sleepMin;
                    final int sleepMax;
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
        selectedRowsList = null;
        PokemonGoMainWindow.getInstance().refreshTitle();
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText(String.format(
                "Pokémon batch evolve%s complete!",
                config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE) ? "/transfer" : ""),
            selection.size(), success, skipped, err);
    }

    private void powerUpSelected() {
        final ArrayList<Pokemon> selection = pt.getSelectedPokemon();
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
                            + "Stardust used %d[remaining: %d])",
                        newCandies, candies, candiesToPowerUp,
                        newCp, newCp - cp,
                        newHp, newHp - hp,
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
        PokemonGoMainWindow.getInstance().refreshTitle();
        SwingUtilities.invokeLater(this::refreshList);
        showFinishedText("Pokémon batch powerup complete!", selection.size(), success, skipped, err);

    }

    /**
     * To favorite or not the selected Pokemon's.
     * @author Ben Kauffman
     * @param operation which operation to do regarding favorite: Toogle, Set Yes or Set No 
     */
    private void toggleFavorite(final BatchOperation operation) {
        final ArrayList<Pokemon> selection = pt.getSelectedPokemon();
        if (!selection.isEmpty() && confirmOperation(operation, selection)) {
            final MutableInt err = new MutableInt(),
                    skipped = new MutableInt(),
                    success = new MutableInt(),
                    total = new MutableInt(1);

            selection.forEach(poke -> {
                try {
                    System.out.println(String.format(
                            "Setting favorite %d of %d",
                            total.getValue(),
                            selection.size()));
                    total.increment();
                    final boolean favorite;
                    if (operation.equals(BatchOperation.SET_FAVORITE_YES)) {
                        favorite = true;
                    } else if (operation.equals(BatchOperation.SET_FAVORITE_NO)) {
                        favorite = false;
                    } else {
                        favorite = !poke.isFavorite();
                    }
                    final SetFavoritePokemonResponse.Result favoriteResult = poke
                            .setFavoritePokemon(favorite);
                    System.out.println(String.format(
                            "Attempting to set favorite for %s to %b...",
                            PokemonUtils.getLocalPokeName(poke),
                            favorite));

                    if (favoriteResult == SetFavoritePokemonResponse.Result.SUCCESS) {
                        System.out.println(String.format(
                                "Favorite for %s set to %b, Result: Success!",
                                PokemonUtils.getLocalPokeName(poke),
                                favorite));
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

            PokemonGoMainWindow.getInstance().refreshTitle();
            SwingUtilities.invokeLater(this::refreshPkmn);
            showFinishedText(String.format("Pokémon batch \"%s\" complete!", operation), selection.size(), success, skipped,
                    err);
        }
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
        final int width = 500;
        panel.setPreferredSize(new Dimension(width, height));

        pokes.forEach(p -> {
            String str = String.format("%s - CP: %d, IV: %s%%",
                PokemonUtils.getLocalPokeName(p),
                p.getCp(),
                Utilities.percentageWithTwoCharacters(PokemonCalculationUtils.ivRating(p)));

            switch (operation) {
                case EVOLVE:
                    str += " Cost: " + p.getCandiesToEvolve();
                    str += p.getCandiesToEvolve() > 1 ? " Candies" : " Candy";
                    if (MAP_POKEMON_ITEM.containsKey(p.getId())) {
                        final EvolveHelper evolve = MAP_POKEMON_ITEM.get(p.getId());
                        if (evolve.isEvolveWithItem()) {
                            str += " Evolving to " + evolve.getPokemonToEvolve() + " using " + evolve.getItemToEvolve();
                        }
                    }
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
                final JList<ReplacePattern> theList = (JList<ReplacePattern>) mouseEvent.getSource();
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
    
    /**
     * Helper method to remove the pokemon from the selectedRowsList.
     * @param p pokemon that will be removed from the list
     */
    public void removeSelection(final Pokemon p) {
        final PokemonTableModel model = (PokemonTableModel) pt.getModel();
        final int index = model.getIndexByPokemon(p);
        if (index > 0 && selectedRowsList != null) {
            selectedRowsList.remove(index);
        }
    }

    public void refreshList() {
        pt.constructNewTableModel(go.getInventories().getPokebank().getPokemons());
        try {
            if (selectedRowsList != null) { 
                for (final Integer index : selectedRowsList) {
                    pt.addRowSelectionInterval(index, index);
                }
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static ItemBag getPlayerItems() {
        return go.getInventories().getItemBag();
    }

    public List<String> getColumnErrors() {
        return pt.getColumnErrors();
    }

    public void saveColumnOrder() {
        pt.saveColumnOrderToConfig();
    }

    public PokemonTable getPokemonTable() {
        return pt;
    }

    /**
     * Provide custom formatting for the list of patterns.
     */
    private class ReplacePatternRenderer extends JLabel implements ListCellRenderer<ReplacePattern> {

        private static final long serialVersionUID = -7057892212235868835L;

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
