package me.corriekay.pokegoutil.utils.windows;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.data.enums.ColumnType;

/**
 * A class that holds data relevant for each column
 */
public enum PokemonTableColumn {
    ID(0, "Id", ColumnType.INT, new ArrayList<Integer>()),
    NICKNAME(1, "Nickname", ColumnType.STRING, new ArrayList<String>()),

    // TODO: Enter the missing columns

    CAUGHT_WITH(21, "Caught With", ColumnType.DATE, new ArrayList<String>());


    public final int id;
    public final String name;
    public final ColumnType columnType;
    public final ArrayList data;

    /**
     * Constructor to create the enum entries.
     *
     * @param id         The id of the column.
     * @param name       The name of the column.
     * @param columnType The type of the column.
     * @param data       The (empty) list as a placeholder for the data of that column.
     */
    PokemonTableColumn(int id, String name, ColumnType columnType, ArrayList data) {
        this.id = id;
        this.name = name;
        this.columnType = columnType;
        this.data = data;
    }

    /**
     * Returns the comparator for the given column, based on the column type.
     *
     * @return The comparator.
     */
    public Comparator getComparator() {
        return columnType.comparator;
    }

    /**
     * Returns the table cell renderer for the given column, based on the column type.
     *
     * @return The cell renderer.
     */
    public TableCellRenderer getCellRenderer() {
        return columnType.tableCellRenderer;
    }

    public static PokemonTableColumn getForId(int id) {
        for (final PokemonTableColumn column : PokemonTableColumn.values()) {
            if (column.id == id) {
                return column;
            }
        }
        // If not found, we throw an exception
        throw new NoSuchElementException("There is no column with id " + id);
    }
}
