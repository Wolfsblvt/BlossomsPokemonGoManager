package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.util.concurrent.CompletableFuture;

import javax.swing.JTable;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class FutureCellRenderer extends DefaultCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, isSelected);
        // We set a default text. Must be this long so that the column width is set correctly
        setText("... Loading ...                              ");

        @SuppressWarnings("unchecked")
        final CompletableFuture<String> future = (CompletableFuture<String>) value;
        future.thenAcceptAsync(this::asyncSetValue);
        return this;
    }

    /**
     * Sets the text and tooltip when the value is resolved.
     *
     * @param textValue The text to set.
     */
    public void asyncSetValue(final String textValue) {
        setText(textValue);
        setToolTipText(textValue);
    }
}
