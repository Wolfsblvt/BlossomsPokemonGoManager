package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Provide custom formatting for the moveset ranking columns while allowing sorting on original values.
 */
public class DefaultCellRenderer extends JLabel implements TableCellRenderer {
    // Padding left and right can be overwritten by custom renderer.
    protected final int paddingLeft = 5;
    protected final int paddingRight = 5;
    // Padding top and bottom can't be overwritten, cause rows should have the same height
    private final int paddingTop = 2;
    private final int paddingBottom = 2;


    /**
     * Creates an instance of the DefaultCellRenderer.
     */
    public DefaultCellRenderer() {
        // We don't need to do anything here. Just the default constructor.
        super();
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
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
    public void setNativeLookAndFeel(final JTable table, final boolean isSelected) {
        setOpaque(true);
        setDefaultSelectionColors(table, isSelected);
        setFont(table.getFont());
        setBorder(BorderFactory.createEmptyBorder(paddingTop, paddingLeft, paddingBottom, paddingRight));
    }

    /**
     * Set the selection colors for the cell.
     *
     * @param table      The table.
     * @param isSelected If the cell is selected.
     */
    private void setDefaultSelectionColors(final JTable table, final boolean isSelected) {
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setForeground(table.getSelectionForeground());
        } else {
            this.setBackground(table.getBackground());
            this.setForeground(table.getForeground());
        }
    }
}
