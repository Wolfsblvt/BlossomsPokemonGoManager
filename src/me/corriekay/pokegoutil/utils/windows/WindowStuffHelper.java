package me.corriekay.pokegoutil.utils.windows;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Helper class that provides useful tools for everything concerning windows.
 */
public final class WindowStuffHelper {
    public static final JDialog ALWAYS_ON_TOP_PARENT = new JDialog();

    static {
        ALWAYS_ON_TOP_PARENT.setAlwaysOnTop(true);
    }

    /** Prevent initializing this class. */
    private WindowStuffHelper() {
    }

    /**
     * Fires the cell data changed event for given table and cell.
     *
     * @param table       The table.
     * @param rowIndex    The row index.
     * @param columnIndex The column index.
     */
    public static void fireCellChanged(final JTable table, final int rowIndex, final int columnIndex) {
        ((AbstractTableModel) table.getModel()).fireTableCellUpdated(rowIndex, columnIndex);
    }
}
