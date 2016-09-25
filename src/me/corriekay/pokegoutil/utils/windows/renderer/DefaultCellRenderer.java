package me.corriekay.pokegoutil.utils.windows.renderer;

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
        setNativeLookAndFeel(table, isSelected);
        setText(value.toString());
        return this;
    }

    /**
     * Sets the native look and feel for the TableCellRenderer.
     * This method should be called first in getTableCellRendererComponent() when extending this CellRenderer,
     * or should be overwritten and called then.
     *
     * @param table      The table.
     * @param isSelected If the cell is selected.
     */
    public void setNativeLookAndFeel(JTable table, boolean isSelected) {
        setOpaque(true);
        setDefaultSelectionColors(table, isSelected);
        setFont(table.getFont());
    }

    /**
     * Set the selection colors for the cell.
     *
     * @param table      The table.
     * @param isSelected If the cell is selected.
     */
    private void setDefaultSelectionColors(JTable table, boolean isSelected) {
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setForeground(table.getSelectionForeground());
        } else {
            this.setBackground(table.getBackground());
            this.setForeground(table.getForeground());
        }
    }
}
