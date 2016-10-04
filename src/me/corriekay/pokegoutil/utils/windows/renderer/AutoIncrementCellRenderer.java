package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;

import javax.swing.JTable;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class AutoIncrementCellRenderer extends NumberCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, isSelected);

        // The row number, one-based
        final int rowNumber = rowIndex + 1;
        setText(String.valueOf(rowNumber));

        return this;
    }
}
