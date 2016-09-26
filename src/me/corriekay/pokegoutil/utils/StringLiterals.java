package me.corriekay.pokegoutil.utils;

/**
 * A collection of commonly used string literals.
 */
public final class StringLiterals {
    // Common string literals
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String PERCENTAGE = "%";
    public static final String DOT = ".";
    public static final String NEWLINE = System.lineSeparator();

    // Situation specific string literals
    public static final String CONCAT = ", ";
    public static final String NO_VALUE_SIGN = "-";

    // More uncommon ones
    public static final String FAMILY_PREFIX = "FAMILY_";

    /** The Default constructor. Private to prevent initialization. */
    private StringLiterals() {
        // Prevent initializing this class
    }
}
