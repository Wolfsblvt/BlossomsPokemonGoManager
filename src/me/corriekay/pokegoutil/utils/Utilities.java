package me.corriekay.pokegoutil.utils;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.InvalidProtocolBufferException;

public final class Utilities {


    public static final int PERCENTAGE_FACTOR = 100;

    /** Prevent initializing this class. */
    private Utilities() {
    }

    private static final Random random = new Random(System.currentTimeMillis());

    public static boolean isEven(long i) {
        return i % 2 == 0;
    }

    /**
     * Takes to numbers and creates a decimal percentage of it (like 0.7542).
     *
     * @param number  The real part.
     * @param maximum The maximum of the number.
     * @return The percentage value
     */
    public static double percentage(double number, double maximum) {
        return Math.min(number / maximum, maximum);
    }

    /**
     * Returns the percentage with two characters, based on a given double decimal number (like 0.7531).
     *
     * @param decimalNumber The given decimal number.
     * @return Percentage string.
     * @deprecated We don'T use this function anymore. The PokeHandler has its own function for replace patterns now.
     */
    @Deprecated
    public static String percentageWithTwoCharacters(double decimalNumber) {
        double percentage = decimalNumber * PERCENTAGE_FACTOR;
        return (percentage < PERCENTAGE_FACTOR) ? StringUtils.leftPad(String.valueOf(percentage), 2, '0') : "XX";
    }

    public static Color randomColor() {
        Color c = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 255);
        return c;
    }

    public static void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepRandom(int minMilliseconds, int maxMilliseconds) {
        try {
            int randomInt = getRandom(minMilliseconds, maxMilliseconds);
            System.out.println("Waiting " + (randomInt / 1000.0F) + " seconds.");
            TimeUnit.MILLISECONDS.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getRandom(int minMilliseconds, int maxMilliseconds) {
        int from = Math.max(minMilliseconds, maxMilliseconds);
        int to = Math.min(minMilliseconds, maxMilliseconds);
        return random.nextInt((from - to) + 1) + to;
    }

    public static String getRealExceptionMessage(Exception e) {
        String message = e.getMessage();
        if (e instanceof InvalidProtocolBufferException || "Contents of buffer are null".equals(message)) {
            message = "Server hasn't responded in time. "
                + "Seems to be busy. "
                + "The action may have been successful though. (" + message + ")";
        }
        return message;
    }

    public static String concatString(char delimeter, String... strings) {
        if (strings.length == 0) {
            return "";
        }

        String s = strings[0];
        for (int i = 1; i < strings.length; i++) {
            s += delimeter + strings[i];
        }
        return s;
    }
}
