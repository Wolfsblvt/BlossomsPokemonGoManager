package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class NumberCellRenderer extends DefaultCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, isSelected);
        setText(value.toString());
        return this;
    }

    @Override
    protected void setNativeLookAndFeel(final JTable table, final boolean isSelected) {
        super.setNativeLookAndFeel(table, isSelected);
        setHorizontalAlignment(JLabel.RIGHT);
    }
}
