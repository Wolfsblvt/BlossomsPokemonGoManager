package me.corriekay.pokegoutil.utils.windows;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.mutable.MutableInt;

import com.pokegoapi.api.pokemon.Pokemon;

import me.corriekay.pokegoutil.data.enums.PokeColumn;
import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;

/**
 * Extended from AbstractTableModel to provide useful methos to PokemonTabel.
 */
public class PokemonTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -1942850053142414172L;

    private PokemonTable pt;

    private final ArrayList<Pokemon> pokeCol = new ArrayList<>();

    /**
     * Default constructor.
     * @param pokes list of pokemons to display
     * @param pt the pokemonTabel instance
     */
    PokemonTableModel(final List<Pokemon> pokes, final PokemonTable pt) {
        this.pt = pt;

        updateTableData(pokes);
    }

    /**
     * Updates the table data with given pokemon list.
     *
     * @param pokes The list of pokemon that should be displayed
     */ 
    public void updateTableData(final List<Pokemon> pokes) {
        clearTable();

        final MutableInt i = new MutableInt();

        pokes.forEach(p -> {
            pokeCol.add(i.getValue(), p);
            for (final PokeColumn column : PokeColumn.values()) {
                // Reason: To avoid future problems inside whatever columns
                try {
                    column.data.add(i.getValue(), column.get(p));
                    //CHECKSTYLE:OFF
                } catch (NullPointerException e) {
                    //CHECKSTYLE:ON
                    System.out.println("Error getting data for column: " + column.heading + " and value: " + i.getValue());
                    ConsolePrintStream.printException(e);
                }
            }

            i.increment();
        });

        fireTableDataChanged();
    }

    /**
     * Clear the values from the table.
     */
    private void clearTable() {
        pokeCol.clear();
        for (final PokeColumn column : PokeColumn.values()) {
            column.data.clear();
        }
    }

    /**
     * Return the Index of the pokemon in the list.
     * @param p pokemon that should have the index returned
     * @return index of the pokemon p
     */
    public int getIndexByPokemon(final Pokemon p) {
        return pokeCol.indexOf(p);
    }
    
    /**
     * Return the pokemon by the index after converted by "convertRowIndexToModel".
     * @param index index of the pokemon that should be returned
     * @return the pokemon in the index i
     */
    public Pokemon getPokemonByIndex(final int index) {
        try {
            return pokeCol.get(pt.convertRowIndexToModel(index));
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Return the pokemon by the index without converting by "convertRowIndexToModel".
     * @param index index of the pokemon that should be returned
     * @return the pokemon in the index i
     */
    public Pokemon getPokemonByIndexNotConverting(final int index) {
        try {
            return pokeCol.get(index);
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String getColumnName(final int columnIndex) {
        return PokeColumn.getForId(columnIndex).heading;
    }

    @Override
    public int getColumnCount() {
        return PokeColumn.values().length;
    }

    @Override
    public int getRowCount() {
        return pokeCol.size();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        return PokeColumn.getForId(columnIndex).data.get(rowIndex);
    }
    
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return PokeColumn.getForId(columnIndex).columnType.tableCellEditor != null;
    }
}
