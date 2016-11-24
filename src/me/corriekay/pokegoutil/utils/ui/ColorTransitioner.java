package me.corriekay.pokegoutil.utils.ui;

import java.awt.Color;
import java.util.List;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.ColorHelper;

/**
 * A Transitioner that is initialized with a list of colors and points to make a color transition for a percentage value.
 */
public class ColorTransitioner {

    public final List<ColorPoint> colors;

    /**
     * Creates a ColorTransitioner from a list of color points.
     *
     * @param colorPoints The color points.
     */
    public ColorTransitioner(final List<ColorPoint> colorPoints) {
        if (colorPoints == null) {
            throw new NullPointerException("Colors can't be null");
        }
        if (colorPoints.size() == 0) {
            throw new IllegalArgumentException("ColorTransitioner must have at least one column");
        }

        // Sor the points from lowest to highest
        colorPoints.sort((left, right) -> Double.compare(left.point, right.point));

        if (colorPoints.get(0).point != 0 || colorPoints.get(colorPoints.size() - 1).point != 1) {
            throw new IllegalArgumentException("The first and the last color must be at point 0 and point 1.");
        }

        this.colors = colorPoints;
    }

    /**
     * Gets the transitioned color based on a percentage between 0 and 1.
     *
     * @param percentage The percentage.
     * @return The transitioned color.
     */
    public Color getColor(final double percentage) {
        final double transitionPoint = Utilities.limit(0.0, percentage, 1.0);
        if (colors.size() == 1) {
            return colors.get(0).color;
        }

        ColorPoint lower = colors.get(0);
        ColorPoint higher = colors.get(1);
        for (final ColorPoint colorPoint : colors) {
            if (colorPoint.point > transitionPoint) {
                // We found the higher point
                higher = colorPoint;
                break;
            }
            lower = colorPoint;
        }

        // We calculate the real percentage within the span
        final double spanned = (transitionPoint - lower.point) * (1 / (higher.point - lower.point));

        // Now we calculate the middle between the two given colors
        return new Color(
            (int) Math.round(lower.color.getRed() * (1 - spanned) + higher.color.getRed() * spanned),
            (int) Math.round(lower.color.getGreen() * (1 - spanned) + higher.color.getGreen() * spanned),
            (int) Math.round(lower.color.getBlue() * (1 - spanned) + higher.color.getBlue() * spanned),
            Utilities.limit(0, ConfigNew.getConfig().getInt(ConfigKey.COLOR_ALPHA), ColorHelper.MAX_COLOR)
        );
    }

    /**
     * A color point, that contains a color and a point between 0 and 1.
     */
    public static class ColorPoint {
        public final double point;
        public final Color color;

        /**
         * Creates a color point with a point and a color.
         *
         * @param point The point.
         * @param color The color.
         */
        public ColorPoint(final double point, final Color color) {
            if (color == null) {
                throw new NullPointerException("color can't be null");
            }
            if (point > 1 || point < 0) {
                throw new IndexOutOfBoundsException("Point of color must be between 0 and 1.");
            }

            this.point = point;
            this.color = color;
        }
    }
}
