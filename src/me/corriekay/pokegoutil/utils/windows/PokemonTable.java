package me.corriekay.pokegoutil.utils.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;

import me.corriekay.pokegoutil.data.enums.PokeColumn;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.helpers.JTableColumnPacker;

/**
 * The Pokémon Table. Extended JTable which displays all Pokémon and does the needed
 * configuration for that.
 * <p>
 * Added things are row sorter, column comparators, listener and the cell renderers.
 */
public class PokemonTable extends JTable {

    // Global statics
    public static final int COLUMN_MARGIN = 3;
    public static final int ROW_HEIGHT_PADDING = ConfigNew.getConfig().getInt(ConfigKey.ROW_PADDING);

    private final ConfigNew config = ConfigNew.getConfig();

    private PokemonTableModel ptm;

    private List columnErrors = new LinkedList<String>();

    /**
     * Constructs the PokemonTable, sets the model and defines all preset stuff.
     *
     * @param go The go instance of the Pogo API
     */
    public PokemonTable(final PokemonGo go) {
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setRowHeight(getRowHeight() + ROW_HEIGHT_PADDING * 2);

        ptm = new PokemonTableModel(go, new ArrayList<>(), this);
        setModel(ptm);

        // Load sort configs
        final int sortColIndex1 = config.getInt(ConfigKey.SORT_COLINDEX_1);
        final int sortColIndex2 = config.getInt(ConfigKey.SORT_COLINDEX_2);
        SortOrder sortOrder1;
        SortOrder sortOrder2;
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
        for (final PokeColumn column : PokeColumn.values()) {
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
                @SuppressWarnings("unchecked")
                final List<SortKey> keys = (List<SortKey>) trs.getSortKeys();
                if (keys.size() > 0) {
                    final SortKey prim = keys.get(0);
                    config.setString(ConfigKey.SORT_ORDER_1, prim.getSortOrder().toString());
                    config.setInt(ConfigKey.SORT_COLINDEX_1, prim.getColumn());
                }
                if (keys.size() > 1) {
                    final SortKey sec = keys.get(1);
                    config.setString(ConfigKey.SORT_ORDER_2, sec.getSortOrder().toString());
                    config.setInt(ConfigKey.SORT_COLINDEX_2, sec.getColumn());
                }
            });

        // Add cell renderers
        for (final PokeColumn column : PokeColumn.values()) {
            columnModel.getColumn(column.id).setCellRenderer(column.getCellRenderer());
        }
        try {
            rearrangeColumnOrder();
        }
        catch(Exception exc) {
            System.out.println("Oooops, something went wrong with table order rearranging!");
            System.out.println(exc.toString());
        }
    }

    private void rearrangeColumnOrder()
    {
        List<String> myColumnEnumNames = new LinkedList<String>();
        String config = ConfigNew.getConfig().getString(ConfigKey.POKEMONTABLE_COLUMNORDER);
        if (config != null && ! config.isEmpty()) {
            myColumnEnumNames.addAll(Arrays.asList(config.split(",")));
        }
        else {
            myColumnEnumNames.addAll(Stream.of(PokeColumn.values())
                    .map(Enum::toString).collect(Collectors.toList()));
        }

        int newIndex = 0;
        for (String enumName : myColumnEnumNames) {
            try {
                PokeColumn pokeColumn = PokeColumn.valueOf(enumName);
                TableColumn c = this.getColumn(pokeColumn.heading);
                if (c != null) {
                    int currentIndex = this.convertColumnIndexToView(c.getModelIndex());
                    if (currentIndex != newIndex) {
                        this.getColumnModel().moveColumn(currentIndex, newIndex);
                    }
                    newIndex++;
                }
            }
            catch(IllegalArgumentException exc) {
                columnErrors.add(enumName);
            }
        }
    }

    public void saveColumnOrderToConfig() {
        List<String> enumNames = new LinkedList<String>();
        Enumeration<TableColumn> e = this.getColumnModel().getColumns();
        while(e.hasMoreElements()) {
            String columnHeading = e.nextElement().getHeaderValue().toString();
            try {
                enumNames.add(PokeColumn.getForHeading(columnHeading).toString());
            }
            catch (IllegalArgumentException exc)
            {
                // can this happen in production use?
            }
        }
        String columnOrderEnums = String.join(",", enumNames);
        ConfigNew.getConfig().setString(ConfigKey.POKEMONTABLE_COLUMNORDER, columnOrderEnums);
    }

    /**
     *
     * @return Returns the list of columns which were requested in columns order
     *         in configuration file, but not found.
     */
    public List<String> getColumnErrors() {
        return columnErrors;
    }

    /**
     * Reconstructs the table model with new list of Pokémon.
     * Updates the data and repacks the columns.
     *
     * @param pokes A list of Pokémon to display.
     */
    public void constructNewTableModel(final List<Pokemon> pokes) {
        ptm.updateTableData(pokes);
        pack();
    }

    /**
     * Packs the tables.
     */
    private void pack() {
        for (int i = 0; i < ptm.getColumnCount(); i++) {
            JTableColumnPacker.packColumn(this, i, COLUMN_MARGIN);
        }
    }
}
