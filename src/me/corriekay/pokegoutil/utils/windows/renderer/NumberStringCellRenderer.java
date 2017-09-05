package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * A cell renderer that displays Number_String values centered.
 */

public class NumberStringCellRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = -8702214651553397401L;

    /**
     * Makes number_string values display centered.
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int rowIndex, final int columnIndex) {
        if (value != null) {
            final DefaultCellRenderer numberStringCellRenderer = new DefaultCellRenderer();
            numberStringCellRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(numberStringCellRenderer);
        }
        return this;
    }

}
