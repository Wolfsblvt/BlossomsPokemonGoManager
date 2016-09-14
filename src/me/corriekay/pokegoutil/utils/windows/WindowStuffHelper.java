package me.corriekay.pokegoutil.utils.windows;

import javax.swing.*;

/**
 * Helper class that provides useful tools for everything concerning windows.
 */
public final class WindowStuffHelper {
    public static final JDialog alwaysOnTopParent = new JDialog();

    static {
        alwaysOnTopParent.setAlwaysOnTop(true);
    }

    /** Prevent initializing this class. */
    private WindowStuffHelper() {
    }
}
