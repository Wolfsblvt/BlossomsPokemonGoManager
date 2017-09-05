package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;

import me.corriekay.pokegoutil.utils.Utilities;

/**
 * A cell renderer that displays DPS values in the format "xx.xx".
 */

public class DpsCellRenderer extends NumberCellRenderer<DpsCellRenderer> {
    // add another decimal so it's consistent with how it was displayed with the move.
    private final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    /**
     * Creates an instance of the PercentageCellRenderer, with rating colors between 0 and 1.
     */
    public DpsCellRenderer() {
        super();
        withRatingColors(0, 1);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, value, isSelected);

        if (value != null) {
            final double dpsvalue = Double.valueOf(value.toString());
            setText(decimalFormatter.format(dpsvalue));
            setToolTipText(String.valueOf(dpsvalue));
        }

        return this;
    }

}
