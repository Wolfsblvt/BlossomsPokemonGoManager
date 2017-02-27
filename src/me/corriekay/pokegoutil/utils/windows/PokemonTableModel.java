package me.corriekay.pokegoutil.utils.windows;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.mutable.MutableInt;

import com.pokegoapi.api.pokemon.Pokemon;

import me.corriekay.pokegoutil.data.enums.PokeColumn;

@SuppressWarnings( {"serial"})

public class PokemonTableModel extends AbstractTableModel {

    PokemonTable pt;

    private final ArrayList<Pokemon> pokeCol = new ArrayList<>();

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
        ClearTable();

        final MutableInt i = new MutableInt();

        pokes.forEach(p -> {
            pokeCol.add(i.getValue(), p);

            for (final PokeColumn column : PokeColumn.values()) {
                try {
                    column.data.add(i.getValue(), column.get(p));
                } catch (NullPointerException e) {
                    System.out.println("Error getting data for column: " + column.heading);
                    e.printStackTrace();
                }
            }

            i.increment();
        });

        fireTableDataChanged();
    }

    private void ClearTable() {
        pokeCol.clear();
        for (final PokeColumn column : PokeColumn.values()) {
            column.data.clear();
        }
    }

    public int getIndexByPokemon(final Pokemon p) {
        try {
            return pokeCol.indexOf(p);
        } catch (final Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public Pokemon getPokemonByIndex(final int i) {
        try {
            return pokeCol.get(pt.convertRowIndexToModel(i));
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Pokemon getPokemonByIndexNotConverting(final int i) {
        try {
            return pokeCol.get(i);
        } catch (final Exception e) {
            e.printStackTrace();
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
