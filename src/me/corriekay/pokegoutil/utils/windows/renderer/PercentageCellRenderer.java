package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;

import me.corriekay.pokegoutil.utils.Utilities;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class PercentageCellRenderer extends NumberCellRenderer<PercentageCellRenderer> {

    private final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    /**
     * Creates an instance of the PercentageCellRenderer, with rating colors between 0 and 1.
     */
    public PercentageCellRenderer() {
        super();
        withRatingColors(0, 1);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, value, isSelected);

        if (value != null) {
            final double percentage = Double.valueOf(value.toString());
            setText(decimalFormatter.format(percentage * Utilities.PERCENTAGE_FACTOR));
            setToolTipText(String.valueOf(percentage));
        }

        return this;
    }

}
