package me.corriekay.pokegoutil.utils.windows;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import me.corriekay.pokegoutil.utils.logging.LoggerHelper;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class PercentageCellRenderer extends DefaultCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int rowIndex, int columnIndex) {
        setNativeLookAndFeel(table, isSelected);
        setHorizontalAlignment(JLabel.RIGHT);

        try {
            double percentage = Double.valueOf(value.toString());


        } catch (NumberFormatException e) {
            // We have a wrong number here? Strange :-/
            LoggerHelper.logExceptionMessage(e);
        }

        return this;
    }

}
