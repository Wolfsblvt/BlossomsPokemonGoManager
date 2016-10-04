package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;

import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.logging.LoggerHelper;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class PercentageCellRenderer extends NumberCellRenderer {

    private final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, isSelected);

        try {
            final double percentage = Double.valueOf(value.toString());
            setText(decimalFormatter.format(percentage * Utilities.PERCENTAGE_FACTOR));
            setToolTipText(String.valueOf(percentage));
        } catch (NumberFormatException e) {
            // We have a wrong number here? Strange :-/
            LoggerHelper.logExceptionMessage(e);
        }

        return this;
    }

}
