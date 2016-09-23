package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class NumberCellRenderer extends DefaultCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int rowIndex, int columnIndex) {
        setNativeLookAndFeel(table, isSelected);
        setText(value.toString());
        return this;
    }

    @Override
    public void setNativeLookAndFeel(JTable table, boolean isSelected) {
        super.setNativeLookAndFeel(table, isSelected);
        setHorizontalAlignment(JLabel.RIGHT);
    }
}
