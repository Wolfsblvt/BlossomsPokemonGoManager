package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;

import me.corriekay.pokegoutil.utils.logging.LoggerHelper;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class PercentageCellRenderer extends NumberCellRenderer {

    private static final int PERCENTAGE_FACTOR = 100;

    private final DecimalFormat decimalFormatter = new DecimalFormat("#.00");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int rowIndex, int columnIndex) {
        setNativeLookAndFeel(table, isSelected);

        try {
            double percentage = Double.valueOf(value.toString());
            setText(decimalFormatter.format(percentage * PERCENTAGE_FACTOR));
            setToolTipText(String.valueOf(percentage));
        } catch (NumberFormatException e) {
            // We have a wrong number here? Strange :-/
            LoggerHelper.logExceptionMessage(e);
        }

        return this;
    }

}
