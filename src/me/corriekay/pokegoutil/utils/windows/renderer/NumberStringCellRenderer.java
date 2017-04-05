package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.utils.Utilities;

/**
 * A cell renderer that displays Number_String values centered.
 */

public class NumberStringCellRenderer extends JLabel implements TableCellRenderer {

 	/**
     * Creates an instance of the ..., with string values centered.
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        if (value != null) {
        	DefaultCellRenderer NumberStringCellRenderer = new DefaultCellRenderer();
            NumberStringCellRenderer.setHorizontalAlignment( JLabel.CENTER );
            table.getColumnModel().getColumn(columnIndex).setCellRenderer( NumberStringCellRenderer );
        }
        return this;
    }

}
