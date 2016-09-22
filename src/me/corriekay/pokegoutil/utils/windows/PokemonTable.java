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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

@SuppressWarnings("serial")
public class PokemonTable extends JTable {

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

        // Set the comparator for each column that is defined.
        for (final PokemonTableColumn column : PokemonTableColumn.values()) {
            trs.setComparator(column.id, column.getComparator());
        }

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
        Enumeration<TableColumn> enumerator = getColumnModel().getColumns();
        while (enumerator.hasMoreElements()) {
            TableColumn designColumn = enumerator.nextElement();
            PokemonTableColumn column = PokemonTableColumn.getForId(designColumn.getModelIndex());
            designColumn.setCellRenderer(column.getCellRenderer());
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
