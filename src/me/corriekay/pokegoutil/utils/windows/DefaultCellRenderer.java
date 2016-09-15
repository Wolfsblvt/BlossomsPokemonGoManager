package me.corriekay.pokegoutil.utils.windows;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Provide custom formatting for the moveset ranking columns while allowing sorting on original values.
 */

@SuppressWarnings("serial")
public class DefaultCellRenderer extends JLabel implements TableCellRenderer {

    /**
     * Creates an instance of the DefaultCellRenderer.
     */
    public DefaultCellRenderer() {
        // We don't need to do anything here. Just the default constructor.
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int rowIndex, int columnIndex) {
        setText(value.toString());
        setOpaque(true);
        setDefaultSelectionColors(table, isSelected, this);
        return this;
    }

    /**
     * Set the selection colors for the cell.
     *
     * @param table      The table.
     * @param isSelected If the cell is selected.
     * @param cell       The cell.
     */
    private void setDefaultSelectionColors(JTable table, boolean isSelected, JLabel cell) {
        if (isSelected) {
            cell.setBackground(table.getSelectionBackground());
            cell.setForeground(table.getSelectionForeground());
        } else {
            cell.setBackground(table.getBackground());
            cell.setForeground(table.getForeground());
        }
    }
}
