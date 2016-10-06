package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JTable;

import me.corriekay.pokegoutil.utils.ui.ColorTransitioner;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 *
 * @param <T> The type of the number cell renderer.
 */
public class NumberCellRenderer<T extends NumberCellRenderer<T>> extends DefaultCellRenderer {
    protected static final ColorTransitioner.ColorPoint[] RATING_COLORS = {
        new ColorTransitioner.ColorPoint(0, Color.RED),
        new ColorTransitioner.ColorPoint(0.33333, Color.RED),
        new ColorTransitioner.ColorPoint(0.66666, Color.YELLOW),
        new ColorTransitioner.ColorPoint(1, Color.GREEN),
    };

    private boolean withColors;
    private double minValue;
    private double maxValue;

    /**
     * Sets the rating colors for this cell renderer, based on a min and a max value.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The cell renderer itself.
     */
    public T withRatingColors(final double min, final double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Rating limits wrong. min has to be lower than max.");
        }

        this.withColors = true;
        this.minValue = min;
        this.maxValue = max;

        //noinspection unchecked - We have checked that T is always a NumberCellRenderer.
        return (T) this;
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, value, isSelected);
        setText(value.toString());

        return this;
    }

    @Override
    protected void setNativeLookAndFeel(final JTable table, final Object value, final boolean isSelected) {
        super.setNativeLookAndFeel(table, value, isSelected);
        setHorizontalAlignment(JLabel.RIGHT);

        if (withColors) {
            final double realValue = Double.valueOf(value.toString());
            final double percentage = (realValue - minValue) * (1 / (maxValue - minValue));
            setRatingColor(percentage);
        }
    }

    /**
     * Set the color for the cell based on the rating.
     *
     * @param rating The rating, from 0 to 1.
     */
    private void setRatingColor(final double rating) {
        final ColorTransitioner transitioner = new ColorTransitioner(Arrays.asList(RATING_COLORS));

        final Color background = transitioner.getColor(rating);
        this.setBackground(background);
    }
}
