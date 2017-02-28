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

    private static final String COLUMN_SEPARATOR = ",";

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

        final TableRowSorter<TableModel> trs = new TableRowSorter<>(ptm);

        // Set the comparator for each column that is defined.
        for (final PokeColumn column : PokeColumn.values()) {
            trs.setComparator(column.id, column.getComparator());
        }

        setRowSorter(trs);

        // Load/restore sort order from config
        final List<SortKey> sortKeys = new ArrayList<>();
        recreateSortKeyFromConfig(sortKeys, ConfigKey.SORT_ENUM_1, ConfigKey.SORT_ORDER_1);
        recreateSortKeyFromConfig(sortKeys, ConfigKey.SORT_ENUM_2, ConfigKey.SORT_ORDER_2);
        trs.setSortKeys(sortKeys);

        // Add listener to save those sorting values
        trs.addRowSorterListener(
            e -> {
                @SuppressWarnings("unchecked")
                final List<SortKey> keys = (List<SortKey>) trs.getSortKeys();
                if (keys.size() > 0) {
                    final SortKey prim = keys.get(0);
                    config.setString(ConfigKey.SORT_ORDER_1, prim.getSortOrder().toString());
                    final String sortEnum = PokeColumn.getForHeading(ptm.getColumnName(prim.getColumn())).name();
                    config.setString(ConfigKey.SORT_ENUM_1, sortEnum);
                }
                if (keys.size() > 1) {
                    final SortKey sec = keys.get(1);
                    config.setString(ConfigKey.SORT_ORDER_2, sec.getSortOrder().toString());
                    final String sortEnum = PokeColumn.getForHeading(ptm.getColumnName(sec.getColumn())).name();
                    config.setString(ConfigKey.SORT_ENUM_2, sortEnum);
                }
            });

        // Add cell renderers
        for (final PokeColumn column : PokeColumn.values()) {
            columnModel.getColumn(column.id).setCellRenderer(column.getCellRenderer());
        }
        try {
            restoreColumnOrder();
        }
        catch(Exception exc) {
            System.out.println("Oooops, something went wrong with table order rearranging!");
            System.out.println(exc.toString());
        }
    }

    /**
     * Loads the column order settings from configuration and applies them to the table.
     */
    private void restoreColumnOrder() {
        List<String> myColumnEnumNames = new LinkedList<String>();
        final String columnOrder = config.getString(ConfigKey.POKEMONTABLE_COLUMNORDER);
        if (columnOrder != null && !columnOrder.isEmpty()) {
            myColumnEnumNames.addAll(Arrays.asList(columnOrder.split(COLUMN_SEPARATOR)));
        } else {
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

    /**
     * Stores the column order to configuration file, as a list of ENUM names
     * globbed together separated by COLUMN_SEPARATOR.
     */
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
        final String columnOrderEnums = String.join(COLUMN_SEPARATOR, enumNames);
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
     * Reads a sort column and order from config, creates a sortkey from that and adds
     * it to the given sortkey list. In case of problems with reading key info or converting
     * that into Table index etc., no key is created/added.
     *
     * @param sortKeys         The list into which to add the created sort key
     * @param sortcolumnCfgKey The config key to look up the sort column (Enum)
     * @param sortorderCfgKey  The config key to look up the sort order  (ASC/DESC)
     */
    private void recreateSortKeyFromConfig(final List<SortKey> sortKeys,
            final ConfigKey sortcolumnCfgKey, final ConfigKey sortorderCfgKey) {

        final String sortColumnEnumName = config.getString(sortcolumnCfgKey);
        final String sortOrderConfigValue = config.getString(sortorderCfgKey);
        if (sortColumnEnumName != null && sortOrderConfigValue != null) {
            try {
                // resolving Column's enum name to it's heading,
                // and then ask from the table for the index nr of that heading
                final PokeColumn sortColumn = PokeColumn.valueOf(sortColumnEnumName);
                final int sortColIndex = getIndexForColumnEnum(sortColumn);
                // ASCENDING or DESCENDING
                final SortOrder sortOrder = SortOrder.valueOf(sortOrderConfigValue);
                sortKeys.add(new SortKey(sortColIndex, sortOrder));
            } catch (final IllegalArgumentException e) {
                System.out.println("Error when restoring sort keys. Enum='"
                        + sortColumnEnumName + "', sort order='" + sortOrderConfigValue + "'");
                System.out.println("This sort key will not be restored.");
            }
        }
    }

    /**
     * Queries from the JTable/table model the index of a column for which
     * we know only the enum name.
     *
     * @param column The PokeColum enum instance
     * @return the wanted index
     */
    private int getIndexForColumnEnum(final PokeColumn column) {
        final TableColumn tableColumn = this.getColumn(column.heading);
        if (tableColumn != null) {
            return this.convertColumnIndexToView(tableColumn.getModelIndex());
        }
        throw new IllegalArgumentException("No TableColumn found for PokeColumn '" + column.name());
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
