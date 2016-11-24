package me.corriekay.pokegoutil.utils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * The main Utilities class.
 * Inside are utility global constants and basic utility functions that aren't enough to get extracted to its own file.
 * <p>
 * Dear DEVELOPER, if you decide to add another utility function here, please take a look if there are enough similar
 * ones so that they can be extracted in their own class. Also do take a look if there isn't already a specialized
 * utility class for what you want to add.
 */
public final class Utilities {

    public static final int PERCENTAGE_FACTOR = 100;
    public static final int CALCULATION_FACTOR_1000 = 1000;

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    /** Prevent initializing this class. */
    private Utilities() {
    }


    /**
     * Checks is a number is even.
     *
     * @param number The number.
     * @return True if Even, else false.
     */
    public static boolean isEven(final long number) {
        return number % 2 == 0;
    }

    /**
     * Limits given value between two other values if it is comparable.
     *
     * @param min   The minimum value.
     * @param value The value.
     * @param max   The maximum value.
     * @param <T>   The type of the value.
     * @return The value between those limits.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable> T limit(final T min, final T value, final T max) {
        if (value.compareTo(min) < 0) {
            return min;
        }
        if (value.compareTo(max) > 0) {
            return max;
        }
        return value;
    }

    /**
     * Takes two numbers and creates a decimal percentage of it (like 0.7542).
     * If the maximum is zero, then the percentage returned is 1.0, so highest possible.
     *
     * @param number  The real part.
     * @param maximum The maximum of the number.
     * @return The percentage value.
     */
    public static double percentage(final double number, final double maximum) {
        return (maximum != 0.0) ? number / maximum : 1.0;
    }

    /**
     * Returns the percentage with two characters, based on a given double decimal number (like 0.7531).
     *
     * @param decimalNumber The given decimal number.
     * @return Percentage string.
     * @deprecated We don'T use this function anymore. The PokeHandler has its own function for replace patterns now.
     */
    @Deprecated
    public static String percentageWithTwoCharacters(final double decimalNumber) {
        final int percentage = (int) Math.round(decimalNumber * PERCENTAGE_FACTOR);
        return (percentage < PERCENTAGE_FACTOR) ? StringUtils.leftPad(String.valueOf(percentage), 2, '0') : "XX";
    }

    /**
     * Let the thread sleep for a given amount of milliseconds.
     * IMPORTANT: Don't call from main thread of the UI, otherwise the UI will freeze.
     *
     * @param milliseconds The milliseconds to sleep.
     */
    public static void sleep(final int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Let the thread sleep for a random amount between two given millisecond numbers.
     * IMPORTANT: Don't call from main thread of the UI, otherwise the UI will freeze.
     *
     * @param minMilliseconds The minimum amount of milliseconds.
     * @param maxMilliseconds The maximum amount of milliseconds
     */
    public static void sleepRandom(final int minMilliseconds, final int maxMilliseconds) {
        try {
            final int randomInt = getRandom(minMilliseconds, maxMilliseconds);
            System.out.println("Waiting " + ((double) randomInt / CALCULATION_FACTOR_1000) + " seconds.");
            TimeUnit.MILLISECONDS.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a random number between two numbers.
     *
     * @param minimum The minimum number, inclusive.
     * @param maximum The maximum number, inclusive.
     * @return The random number.
     */
    public static int getRandom(final int minimum, final int maximum) {
        final int from = Math.min(minimum, maximum);
        final int to = Math.max(minimum, maximum);
        return RANDOM.nextInt((to - from) + 1) + from;
    }

    /**
     * Returns a random number between two numbers.
     *
     * @param minimum The minimum number, inclusive.
     * @param maximum The maximum number, inclusive.
     * @return The random number.
     */
    public static double getRandom(final double minimum, final double maximum) {
        final double from = Math.min(minimum, maximum);
        final double to = Math.max(minimum, maximum);
        return RANDOM.nextDouble() * (to - from) + from;
    }

    /**
     * Abstracts the exception message and makes a more readable exception out of it for edge cases.
     *
     * @param exception The exception.
     * @return The real exception message.
     */
    public static String getRealExceptionMessage(final Exception exception) {
        String message = exception.getMessage();
        if (exception instanceof InvalidProtocolBufferException || "Contents of buffer are null".equals(message)) {
            message = "Server hasn't responded in time. "
                + "Seems to be busy. "
                + "The action may have been successful though. (" + message + ")";
        }
        return message;
    }

    /**
     * Concat a given count of string with given delimiter.
     *
     * @param delimiter The delimiter to put between the strings.
     * @param strings   The strings that should be combined.
     * @return The combined string.
     */
    public static String concatString(final char delimiter, final String... strings) {
        if (strings.length == 0) {
            return "";
        }

        String combined = strings[0];
        for (int i = 1; i < strings.length; i++) {
            combined += delimiter + strings[i];
        }
        return combined;
    }
}
