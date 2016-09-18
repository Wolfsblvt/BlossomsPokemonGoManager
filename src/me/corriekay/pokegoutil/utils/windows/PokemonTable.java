package me.corriekay.pokegoutil.utils.windows;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.helpers.JTableColumnPacker;
import me.corriekay.pokegoutil.utils.pokemon.PokemonValueCache;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

@SuppressWarnings("serial")
public class PokemonTable extends JTable {

    // data types:
    // 0 String - Nickname
    // 1 Integer - Pokemon Number
    // 2 String - Type / Pokemon
    // 3 String(Percentage) - IV Rating
    // 4 Double - Level
    // 5 Integer - Attack
    // 6 Integer - Defense
    // 7 Integer - Stamina
    // 8 String - Type 1
    // 9 String - Type 2
    // 10 String - Move 1
    // 11 String - Move 2
    // 12 Integer - CP
    // 13 Integer - HP
    // 14 Integer - Max CP (Current)
    // 15 Integer - Max CP
    // 16 Integer - Max Evolved CP (Current)
    // 17 Integer - Max Evolved CP
    // 18 Integer - Candies of type
    // 19 String(Nullable Int) - Candies to Evolve
    // 20 Integer Star Dust to level
    // 21 String - Pokeball Type
    // 22 String(Date) - Caught at
    // 23 Boolean - Favorite
    // 24 Long - duelAbility
    // 25 Integer - gymOffense
    // 26 Integer - gymDefense
    // 27 String(Nullable Int) - CP Evolved
    // 28 String(Nullable Int) - Evolvable
    // 29 Long - duelAbility IV
    // 30 Double - gymOffense IV
    // 31 Long - gymDefense IV

    private final ConfigNew config = ConfigNew.getConfig();

    private int sortColIndex1, sortColIndex2;
    private SortOrder sortOrder1, sortOrder2;

    private PokemonTableModel ptm;

    public PokemonTable(final PokemonGo go) {
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setAutoResizeMode(AUTO_RESIZE_OFF);

        ptm = new PokemonTableModel(go, new ArrayList<>(), this);
        setModel(ptm);

        // Load sort configs
        sortColIndex1 = config.getInt(ConfigKey.SORT_COLINDEX_1);
        sortColIndex2 = config.getInt(ConfigKey.SORT_COLINDEX_2);
        try {
            sortOrder1 = SortOrder.valueOf(config.getString(ConfigKey.SORT_ORDER_1));
            sortOrder2 = SortOrder.valueOf(config.getString(ConfigKey.SORT_ORDER_2));
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            sortOrder1 = SortOrder.ASCENDING;
            sortOrder2 = SortOrder.ASCENDING;
        }

        final TableRowSorter<TableModel> trs = new TableRowSorter<>(ptm);
        final Comparator<Integer> cInt = Integer::compareTo;
        final Comparator<Float> cFloat = Float::compareTo;
        final Comparator<Double> cDouble = Double::compareTo;
        final Comparator<String> cDate = (date1, date2) -> DateHelper.fromString(date1)
            .compareTo(DateHelper.fromString(date2));
        final Comparator<String> cNullableInt = (s1, s2) -> {
            if ("-".equals(s1)) {
                s1 = "0";
            }
            if ("-".equals(s2)) {
                s2 = "0";
            }
            return Integer.parseInt(s1) - Integer.parseInt(s2);
        };
        final Comparator<Long> cLong = Long::compareTo;
        final Comparator<String> cPercentageWithTwoCharacters = (s1, s2) -> {
            final int i1 = ("XX".equals(s1)) ? 100 : Integer.parseInt(s1);
            final int i2 = ("XX".equals(s2)) ? 100 : Integer.parseInt(s2);
            return i1 - i2;
        };
        trs.setComparator(0, cInt);
        trs.setComparator(3, cPercentageWithTwoCharacters);
        trs.setComparator(4, cFloat);
        trs.setComparator(5, cInt);
        trs.setComparator(6, cInt);
        trs.setComparator(7, cInt);
        trs.setComparator(12, cInt);
        trs.setComparator(13, cInt);
        trs.setComparator(14, cInt);
        trs.setComparator(15, cInt);
        trs.setComparator(16, cInt);
        trs.setComparator(17, cInt);
        trs.setComparator(18, cInt);
        trs.setComparator(19, cNullableInt);
        trs.setComparator(20, cInt);
        trs.setComparator(22, cDate);
        trs.setComparator(24, cLong);
        trs.setComparator(25, cDouble);
        trs.setComparator(26, cLong);
        trs.setComparator(27, cNullableInt);
        trs.setComparator(28, cNullableInt);
        trs.setComparator(29, cLong);
        trs.setComparator(30, cDouble);
        trs.setComparator(31, cLong);
        setRowSorter(trs);
        final List<SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new SortKey(sortColIndex1, sortOrder1));
        sortKeys.add(new SortKey(sortColIndex2, sortOrder2));
        trs.setSortKeys(sortKeys);

        // Add listener to save those sorting values
        trs.addRowSorterListener(
            e -> {
                final RowSorter<TableModel> sorter = trs;
                if (sorter != null) {
                    @SuppressWarnings("unchecked")
                    final
                    List<SortKey> keys = (List<SortKey>) sorter.getSortKeys();
                    if (keys.size() > 0) {
                        final SortKey prim = keys.get(0);
                        sortOrder1 = prim.getSortOrder();
                        config.setString(ConfigKey.SORT_ORDER_1, sortOrder1.toString());
                        sortColIndex1 = prim.getColumn();
                        config.setInt(ConfigKey.SORT_COLINDEX_1, sortColIndex1);
                    }
                    if (keys.size() > 1) {
                        final SortKey sec = keys.get(1);
                        sortOrder2 = sec.getSortOrder();
                        config.setString(ConfigKey.SORT_ORDER_2, sortOrder2.toString());
                        sortColIndex2 = sec.getColumn();
                        config.setInt(ConfigKey.SORT_COLINDEX_2, sortColIndex2);
                    }
                }
            });

        // Add cell Renderers
        DefaultCellRenderer defaultCellRenderer = new DefaultCellRenderer();
        Enumeration<TableColumn> enumerator = getColumnModel().getColumns();
        while (enumerator.hasMoreElements()) {
            TableColumn column = enumerator.nextElement();
            switch (column.getModelIndex()) {
                // Custom cell renderers
                // Magic numbers pulled from max values of their respective columns
                // in the moveset rankings spreadsheet calculations
                // @see
                // https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
                case 24:
                    column.setCellRenderer(new MoveSetRankingRenderer(PokemonValueCache.getHighestStats().duelAbility));
                    break;
                case 25:
                    column.setCellRenderer(new MoveSetRankingRenderer((long) PokemonValueCache.getHighestStats().gymOffense));
                    break;
                case 26:
                    column.setCellRenderer(new MoveSetRankingRenderer(PokemonValueCache.getHighestStats().gymDefense));
                    break;

                default:
                    column.setCellRenderer(defaultCellRenderer);
                    break;
            }
        }
    }

    public void constructNewTableModel(final List<Pokemon> pokes) {
        ptm.ChangeTableData(pokes);
        pack();
    }

    private void pack() {
        for (int ii = 0; ii < ptm.getColumnCount(); ii++) {
            JTableColumnPacker.packColumn(this, ii, 4);
        }
    }
}
