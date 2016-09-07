package me.corriekay.pokegoutil.utils.windows;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.utils.Utilities;

/**
 * Provide custom formatting for the moveset ranking columns while allowing sorting on original values
 */

@SuppressWarnings("serial")
public class DefaultCellRenderer extends JLabel implements TableCellRenderer {

    public DefaultCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int rowIndex, int vColIndex) {
        setText(value.toString());
        setOpaque(true);
        setDefaultSelectionColors(table, isSelected, this);
        return this;
    }

    private void setDefaultSelectionColors(JTable table, boolean isSelected, JLabel tcr) {
        if (isSelected) {
            tcr.setBackground(table.getSelectionBackground());
            tcr.setForeground(table.getSelectionForeground());
        } else {
            tcr.setBackground(table.getBackground());
            tcr.setForeground(table.getForeground());
        }
    }
}
