package me.corriekay.pokegoutil.utils.helpers;

import java.awt.Color;

import me.corriekay.pokegoutil.utils.Utilities;

/**
 * Helper class for managing colors.
 */
public final class ColorHelper {

    public static final int MAX_COLOR = 255;

    /** Prevent initializing this class. */
    private ColorHelper() {
    }

    /**
     * Gets a random color.
     * Notes to Developers: If there will be more color functions in the future, please extract them to a ColorHelper.
     *
     * @return A random color.
     */
    public static Color randomColor() {
        final Color color = new Color(Utilities.RANDOM.nextInt(MAX_COLOR), Utilities.RANDOM.nextInt(MAX_COLOR), Utilities.RANDOM.nextInt(MAX_COLOR), MAX_COLOR);
        return color;
    }

}
