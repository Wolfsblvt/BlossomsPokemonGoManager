package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.swing.JTable;

import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.ui.ColorTransitioner;
import me.corriekay.pokegoutil.utils.ui.ColorTransitioner.ColorPoint;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class PercentageCellRenderer extends NumberCellRenderer {

    private final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, isSelected);

        final double percentage = Double.valueOf(value.toString());
        setRatingColor(percentage);
        setText(decimalFormatter.format(percentage * Utilities.PERCENTAGE_FACTOR));
        setToolTipText(String.valueOf(percentage));

        return this;
    }

    /**
     * Set the color for the cell based on the rating.
     *
     * @param rating The rating, from 0 to 1.
     */
    protected void setRatingColor(final double rating) {
        final ColorPoint[] colors = {
            new ColorPoint(0, Color.RED),
            new ColorPoint(0.2, Color.RED),
            new ColorPoint(0.6, Color.YELLOW),
            new ColorPoint(1, Color.GREEN),
        };
        final ColorTransitioner transitioner = new ColorTransitioner(Arrays.asList(colors));

        final Color background = transitioner.getColor(rating);
        this.setBackground(background);
    }

}
