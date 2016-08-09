package me.corriekay.pokegoutil.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.WordUtils;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import com.pokegoapi.api.pokemon.PokemonMoveMeta;
import com.pokegoapi.api.pokemon.PokemonMoveMetaRegistry;

import POGOProtos.Enums.PokemonFamilyIdOuterClass.PokemonFamilyId;
import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass;
import POGOProtos.Networking.Responses.UpgradePokemonResponseOuterClass;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.GhostText;
import me.corriekay.pokegoutil.utils.JTableColumnPacker;
import me.corriekay.pokegoutil.utils.LDocumentListener;
import me.corriekay.pokegoutil.utils.PokeHandler;
import me.corriekay.pokegoutil.utils.PokemonCpUtils;

@SuppressWarnings("serial")
public class PokemonTab extends JPanel {

    private final PokemonGo go;
    private final PokemonTable pt = new PokemonTable();
    private final JTextField searchBar = new JTextField("");
    private final JTextField ivTransfer = new JTextField("", 20);
    static boolean tAfterE;

    public PokemonTab(PokemonGo go) {
        setLayout(new BorderLayout());
        this.go = go;
        JPanel topPanel = new JPanel(new GridBagLayout());
        JButton refreshPkmn, renameSelected, transferSelected, evolveSelected, powerUpSelected;
        refreshPkmn = new JButton("Refresh List");
        renameSelected = new JButton("Rename");
        transferSelected = new JButton("Transfer");
        evolveSelected = new JButton("Evolve");
        powerUpSelected = new JButton("Power Up");

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
        String[] locales = {"en", "de", "fr", "ru", "zh_CN", "zh_HK"};
        JComboBox<String> pokelang = new JComboBox<String>(locales);
        String locale = Config.getConfig().getString("options.lang", "en");
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

        LDocumentListener.addChangeListener(searchBar, e -> refreshList());
        new GhostText(searchBar, "Search Pokémon...");

        add(topPanel, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(pt);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp, BorderLayout.CENTER);
    }

    private void changeLanguage(String langCode) {
        Config.getConfig().setString("options.lang", langCode);
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

    private void renameSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() == 0) return;
        String renamePattern = inputOperation("Rename", selection);
        if (!"".equals(renamePattern)) {
            MutableInt err = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
            PokeHandler handler = new PokeHandler(selection);

            BiConsumer<NicknamePokemonResponse.Result, Pokemon> perPokeCallback = (result, pokemon) -> {
                System.out.println("Doing Rename " + total.getValue() + " of " + selection.size());
                total.increment();
                if (result.getNumber() == NicknamePokemonResponse.Result.SUCCESS_VALUE) {
                    success.increment();
                    System.out.println("Renaming " + PokeHandler.getLocalPokeName(pokemon) + " from \"" + pokemon.getNickname() + "\" to \"" + PokeHandler.generatePokemonNickname(renamePattern, pokemon) + "\", Result: Success!");
                } else {
                    err.increment();
                    System.out.println("Renaming " + PokeHandler.getLocalPokeName(pokemon) + " failed! Code: " + result.toString() + "; Nick: " + PokeHandler.generatePokemonNickname(renamePattern, pokemon));
                }
            };

            handler.bulkRenameWithPattern(renamePattern, perPokeCallback);

            try {
                go.getInventories().updateInventories(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(this::refreshList);
            JOptionPane.showMessageDialog(null,
                    "Pokémon batch rename complete!\nPokémon total: " + selection.size() + "\nSuccessful Renames: "
                            + success.getValue() + (err.getValue() > 0 ? "\nErrors: " + err.getValue() : ""));
        }
    }

    private void transferSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() == 0) return;
        if (confirmOperation("Transfer", selection)) {
            MutableInt err = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
            selection.forEach(poke -> {
                System.out.println("Doing Transfer " + total.getValue() + " of " + selection.size());
                total.increment();
                if (poke.isFavorite()) {
                    System.out.println(PokeHandler.getLocalPokeName(poke) + " with " + poke.getCp() + "cp is favorite, not transferring.");
                    err.increment();
                    return;
                }
                try {
                    int candies = poke.getCandy();
                    ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result result = poke.transferPokemon();

                    if (result == ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result.SUCCESS) {
                        int newCandies = poke.getCandy();
                        System.out.println("Transferring " + PokeHandler.getLocalPokeName(poke) + ", Result: Success!");
                        System.out.println("Stat changes: (Candies : " + newCandies + "[+" + (newCandies - candies) + "])");
                        success.increment();
                    } else {
                        System.out.println(
                                "Error transferring " + PokeHandler.getLocalPokeName(poke) + ", result: " + result);
                        err.increment();
                    }
                } catch (Exception e) {
                    err.increment();
                    System.out.println("Error transferring " + PokeHandler.getLocalPokeName(poke) + "! " + e.getMessage());
                }
            });
            try {
                go.getInventories().updateInventories(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(this::refreshList);
            JOptionPane.showMessageDialog(null,
                    "Pokémon batch transfer complete!\nPokémon total: " + selection.size() + "\nSuccessful Transfers: "
                            + success.getValue() + (err.getValue() > 0 ? "\nErrors: " + err.getValue() : ""));
        }
    }

    private void evolveSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() > 0) {
            if (confirmOperation("Evolve", selection)) {
                MutableInt err = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
                selection.forEach(poke -> {
                    System.out.println("Doing Evolve " + total.getValue() + " of " + selection.size());
                    total.increment();
                    try {
                        int candies = poke.getCandy();
                        int candiesToEvolve = poke.getCandiesToEvolve();
                        int cp = poke.getCp();
                        int hp = poke.getMaxStamina();
                        EvolutionResult er = poke.evolve();
                        if (er.isSuccessful()) {
                            Pokemon newPoke = er.getEvolvedPokemon();
                            int newCandies = newPoke.getCandy();
                            int newCp = newPoke.getCp();
                            int newHp = newPoke.getStamina();
                            System.out.println(
                                    "Evolving " + PokeHandler.getLocalPokeName(poke) + ". Evolve result: Success!");
                            if (tAfterE) {
                                ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result result = newPoke.transferPokemon();
                                System.out.println("Transferring " + StringUtils.capitalize(newPoke.getPokemonId().toString().toLowerCase()) + ", Result: " + result);
                                System.out.println("Stat changes: (Candies: " + newCandies + "[" + candies + "-" + candiesToEvolve + "]");
                            } else {
                                System.out.println("Stat changes: (Candies: " + newCandies + "[" + candies + "-" + candiesToEvolve + "], CP: " + newCp + "[+" + (newCp - cp) + "], HP: " + newHp + "[+" + (newHp - hp) + "])");
                            }
                            go.getInventories().updateInventories(true);
                            success.increment();
                        } else {
                            err.increment();
                            System.out.println("Error evolving " + PokeHandler.getLocalPokeName(poke) + ", result: " + er.toString());
                        }
                    } catch (Exception e) {
                        err.increment();
                        System.out.println("Error evolving " + PokeHandler.getLocalPokeName(poke) + "! " + e.getMessage());
                    }
                });
                try {
                    go.getInventories().updateInventories(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(this::refreshList);
                if (tAfterE)
                    JOptionPane.showMessageDialog(null, "Pokemon batch evolve complete!\nPokemon total: " + selection.size() + "\nSuccessful evolves/transfers: " + success.getValue() + (err.getValue() > 0 ? "\nErrors: " + err.getValue() : ""));
                else
                    JOptionPane.showMessageDialog(null, "Pokemon batch evolve complete!\nPokemon total: " + selection.size() + "\nSuccessful evolves: " + success.getValue() + (err.getValue() > 0 ? "\nErrors: " + err.getValue() : ""));
            }
        }
    }

    private void powerUpSelected() {
        ArrayList<Pokemon> selection = getSelectedPokemon();
        if (selection.size() > 0) {
            if (confirmOperation("PowerUp", selection)) {
                MutableInt err = new MutableInt(), success = new MutableInt(), total = new MutableInt(1);
                selection.forEach(poke -> {
                    try {
                        System.out.println("Doing Power Up " + total.getValue() + " of " + selection.size());
                        total.increment();
                        int candies = poke.getCandy();
                        int cp = poke.getCp();
                        int hp = poke.getMaxStamina();
                        int stardustUsed = poke.getStardustCostsForPowerup();
                        int candiesToPowerUp = poke.getCandyCostsForPowerup();
                        UpgradePokemonResponseOuterClass.UpgradePokemonResponse.Result result = poke.powerUp();
                        go.getPlayerProfile().updateProfile();
                        if (result == UpgradePokemonResponseOuterClass.UpgradePokemonResponse.Result.SUCCESS) {
                            int newCandies = poke.getCandy();
                            int newCp = poke.getCp();
                            int newHp = poke.getMaxStamina();
                            System.out.println(
                                    "Powering Up " + PokeHandler.getLocalPokeName(poke) + ", Result: Success!");
                            System.out.println("Stat changes: (Candies : " + newCandies + "[" + candies + "-" + candiesToPowerUp + "], CP: " + newCp + "[+" + (newCp - cp) + "], HP: " + newHp + "[+" + (newHp - hp) + "]) Stardust used " + stardustUsed + "[remaining: " + go.getPlayerProfile().getCurrency(Currency.STARDUST) + "]");
                            success.increment();
                        } else {
                            err.increment();
                            System.out.println(
                                    "Error powering up " + PokeHandler.getLocalPokeName(poke) + ", result: " + result.toString());
                        }
                    } catch (Exception e) {
                        err.increment();
                        System.out.println("Error powering up " + PokeHandler.getLocalPokeName(poke) + "! " + e.getMessage());
                    }
                });
                try {
                    go.getInventories().updateInventories(true);
                    PokemonGoMainWindow.window.refreshTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(this::refreshList);
                JOptionPane.showMessageDialog(null,
                        "Pokémon batch powerup complete!\nPokémon total: " + selection.size()
                                + "\nSuccessful powerups: " + success.getValue()
                                + (err.getValue() > 0 ? "\nErrors: " + err.getValue() : ""));
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
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panel.setMaximumSize(panel.getSize());

        pokes.forEach(p -> {
            String str = PokeHandler.getLocalPokeName(p) + " - CP: " + p.getCp() + ", IV: "
                    + (Math.round(p.getIvRatio() * 10000) / 100) + "%";
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

    private ArrayList<Pokemon> getSelectedPokemon() {
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

    private void refreshList() {
        List<Pokemon> pokes = new ArrayList<>();
        String search = searchBar.getText().replaceAll(" ", "").replaceAll("_", "").replaceAll("snek", "ekans").toLowerCase();
        try {
            go.getInventories().getPokebank().getPokemons().forEach(poke -> {
                String searchme = PokeHandler.getLocalPokeName(poke) + "" + poke.getPokemonFamily() + poke.getNickname()
                        + poke.getMeta().getType1() + poke.getMeta().getType2() + poke.getMove1() + poke.getMove2()
                        + poke.getPokeball();
                searchme = searchme.replaceAll("_FAST", "").replaceAll("FAMILY_", "").replaceAll("NONE", "").replaceAll("ITEM_", "").replaceAll("_", "").replaceAll(" ", "").toLowerCase();
                if (searchme.contains(search)) {
                    pokes.add(poke);
                }
            });
            pt.constructNewTableModel(go, (search.equals("") || search.equals("searchpokémon...")
                    ? go.getInventories().getPokebank().getPokemons() : pokes));
            for (int i = 0; i < pt.getModel().getColumnCount(); i++) {
                JTableColumnPacker.packColumn(pt, i, 4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class PokemonTable extends JTable {

        /**
         * data types:
         * 0 String - Nickname
         * 1 Integer - Pokemon Number
         * 2 String - Type / Pokemon
         * 3 Double - IV %
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
         * 19 Integer - Candies to Evolve
         * 20 Integer - Star Dust to level
         * 21 String - Pokeball Type
         * 22 LocalDateTime - Caught at
         */
        int sortColIndex = 0;
        SortOrder so = SortOrder.ASCENDING;

        private PokemonTable() {
            setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            setAutoResizeMode(AUTO_RESIZE_OFF);
        }

        private void constructNewTableModel(PokemonGo go, List<Pokemon> pokes) {
            PokemonTableModel ptm = new PokemonTableModel(go, pokes, this);
            setModel(ptm);
            TableRowSorter<TableModel> trs = new TableRowSorter<>(getModel());
            Comparator<Integer> c = (i1, i2) -> Math.round(i1 - i2);
            Comparator<Double> cDouble = (d1, d2) -> (int) (d1 - d2);
            trs.setComparator(0, c);
            trs.setComparator(3, cDouble);
            trs.setComparator(4, cDouble);
            trs.setComparator(5, c);
            trs.setComparator(6, c);
            trs.setComparator(7, c);
            trs.setComparator(12, c);
            trs.setComparator(14, c);
            trs.setComparator(15, c);
            trs.setComparator(16, c);
            trs.setComparator(17, c);
            trs.setComparator(18, c);
            //TODO: needs to be fixed/debugged
            trs.setComparator(19, (e1, e2) -> {
                if (e1.equals("-"))
                    e1 = "0";
                if (e2.equals("-"))
                    e2 = "0";
                return Math.round(Integer.getInteger((String) e1) - Integer.getInteger((String) e2));
            });
            trs.setComparator(20, c);
            trs.setComparator(22, (date1, date2) -> (((LocalDateTime) date1).compareTo((LocalDateTime) date2)));
            setRowSorter(trs);
            trs.toggleSortOrder(sortColIndex);
            List<SortKey> sortKeys = new ArrayList<>();
            sortKeys.add(new SortKey(sortColIndex, so));
            trs.setSortKeys(sortKeys);
        }
    }

    private static class PokemonTableModel extends AbstractTableModel {

        PokemonTable pt;

        private final ArrayList<Pokemon> pokeCol = new ArrayList<>();
        private final ArrayList<Integer> numIdCol = new ArrayList<>();//0
        private final ArrayList<String> nickCol = new ArrayList<>(),//1
                speciesCol = new ArrayList<>();//2
        private final ArrayList<Double> ivCol = new ArrayList<>(),//3
                levelCol = new ArrayList<>();//4
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
        private final ArrayList<String> pokeballCol = new ArrayList<>();//21
        private final ArrayList<LocalDateTime> caughtCol = new ArrayList<>();//22

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
                ivCol.add(i.getValue(), Math.round(p.getIvRatio() * 10000) / 100.00);
                cpCol.add(i.getValue(), p.getCp());
                atkCol.add(i.getValue(), p.getIndividualAttack());
                defCol.add(i.getValue(), p.getIndividualDefense());
                stamCol.add(i.getValue(), p.getIndividualStamina());
                type1Col.add(i.getValue(), StringUtils.capitalize(p.getMeta().getType1().toString().toLowerCase()));
                type2Col.add(i.getValue(), StringUtils.capitalize(p.getMeta().getType2().toString().toLowerCase().replaceAll("none", "")));

                PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(p.getMove1());
                PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(p.getMove2());
                Double dps1 = (double) pm1.getPower() / (double) pm1.getTime() * 1000;
                Double dps2 = (double) pm2.getPower() / (double) (pm2.getTime() + 500) * 1000;
                if (p.getMeta().getType1().equals(pm1.getType()) || p.getMeta().getType2().equals(pm1.getType()))
                    dps1 = dps1 * 1.25;
                if (p.getMeta().getType1().equals(pm2.getType()) || p.getMeta().getType2().equals(pm2.getType()))
                    dps2 = dps2 * 1.25;

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
                } else if (highestFamilyMeta == null) {
                    System.out.println("Error: Cannot find meta data for " + highestFamilyId.name());
                } else {
                    int attack = highestFamilyMeta.getBaseAttack() + p.getIndividualAttack();
                    int defense = highestFamilyMeta.getBaseDefense() + p.getIndividualDefense();
                    int stamina = highestFamilyMeta.getBaseStamina() + p.getIndividualStamina();
                    maxEvolvedCpCurrentCol.add(i.getValue(), PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel));
                    maxEvolvedCpCol.add(i.getValue(), PokemonCpUtils.getMaxCp(attack, defense, stamina));
                }

                try {
                    candiesCol.add(i.getValue(), p.getCandy());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (p.getCandiesToEvolve() != 0)
                    candies2EvlvCol.add(i.getValue(), String.valueOf(p.getCandiesToEvolve()));
                else
                    candies2EvlvCol.add(i.getValue(), "-");
                dustToLevelCol.add(i.getValue(), p.getStardustCostsForPowerup());
                pokeballCol.add(i.getValue(), WordUtils.capitalize(p.getPokeball().toString().toLowerCase().replaceAll("item_", "").replaceAll("_", " ")));
                caughtCol.add(i.getValue(), LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getCreationTimeMs()), ZoneId.systemDefault()));
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
                default:
                    return "UNKNOWN?";
            }
        }

        @Override
        public int getColumnCount() {
            return 23;
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
                default:
                    return null;
            }
        }
    }
}
