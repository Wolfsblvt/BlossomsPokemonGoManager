package me.corriekay.pokegoutil.utils.windows;

import javax.swing.*;

public final class WindowStuffHelper {
    public static final JDialog alwaysOnTopParent = new JDialog();

    static {
        alwaysOnTopParent.setAlwaysOnTop(true);
    }

    /* Prevent initializing this class */
    private WindowStuffHelper() {
    }
}
