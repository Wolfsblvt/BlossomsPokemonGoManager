package me.corriekay.pokegoutil.utils;

/**
 * A collection of commonly used string literals.
 */
public final class StringLiterals {
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String PERCENTAGE = "%";
    public static final String NEWLINE = System.lineSeparator();
    
    //Default DeviceInfo config, just to codacy stop complain about repeting it 2 times
    public static final String APPLE = "Apple";

    // More uncommon ones
    public static final String FAMILY_PREFIX = "FAMILY_";

    /**
     * The Default constructor. Private to prevent initialization.
     */
    private StringLiterals() {
        // Prevent initializing this class
    }
}
