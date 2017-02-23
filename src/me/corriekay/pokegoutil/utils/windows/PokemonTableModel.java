package me.corriekay.pokegoutil.utils.windows;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.mutable.MutableInt;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;

import me.corriekay.pokegoutil.data.enums.PokeColumn;

@SuppressWarnings( {"serial", "rawtypes"})

public class PokemonTableModel extends AbstractTableModel {

    PokemonTable pt;

    private final ArrayList<Pokemon> pokeCol = new ArrayList<>();
    private final PokemonGo go;

    @Deprecated
    PokemonTableModel(final PokemonGo go, final List<Pokemon> pokes, final PokemonTable pt) {
        this.pt = pt;
        this.go = go;

        updateTableData(pokes);
    }

    /**
     * Updates the table data with given pokemon list.
     *
     * @param pokes The list of pokemon that should be displayed
     */
    @SuppressWarnings("unchecked")
    public void updateTableData(final List<Pokemon> pokes) {
        ClearTable();

        final MutableInt i = new MutableInt();

        pokes.forEach(p -> {
            pokeCol.add(i.getValue(), p);

            for (final PokeColumn column : PokeColumn.values()) {
                // Now lets add the value for that column
                column.data.add(i.getValue(), column.get(p));
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

    public Pokemon getPokemonByIndex(final int i) {
        try {
            return pokeCol.get(pt.convertRowIndexToModel(i));
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
}
