package me.corriekay.pokegoutil.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class Utilities {
    private Utilities() { /* Prevent initializing this class */ }

    private static final CharsetEncoder iso88591Encoder = Charset.forName("ISO-8859-1").newEncoder();
    private static final Random random = new Random(System.currentTimeMillis());

    public static boolean isEven(long i) {
        return i % 2 == 0;
    }

    public static boolean isLatin(String str) {
        return iso88591Encoder.canEncode(str);
    }

    /**
     * Rounds given decimal number (like 0.75345) to a percentage with two decimals (75.35)
     *
     * @param decimalNumber The decimal number
     * @return The percentage value
     */
    public static double percentage(double decimalNumber) {
        return Math.round(decimalNumber * 100 * 100) / 100.0;
    }

    public static String percentageWithTwoCharacters(double number, double maximum) {
        return percentageWithTwoCharacters(Math.min(number / maximum, maximum));
    }
    public static String percentageWithTwoCharacters(double decimalNumber) {
        long rounded = Math.round(percentage(decimalNumber));
        return (rounded < 100) ? StringUtils.leftPad(String.valueOf(rounded), 2, '0') : "XX";

    }

    public static Color randomColor() {
        Color c = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 255);
        return c;
    }

    public static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepRandom(int minMilliseconds, int maxMilliseconds) {
        int from = Math.max(minMilliseconds, maxMilliseconds);
        int to = Math.min(minMilliseconds, maxMilliseconds);
        try {
            int randomInt = random.nextInt((from - to) + 1) + to;
            System.out.println("Waiting " + (randomInt / 1000.0F) + " seconds.");
            TimeUnit.MILLISECONDS.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getRealExceptionMessage(Exception e) {
        String message = e.getMessage();
        if (e instanceof InvalidProtocolBufferException || "Contents of buffer are null".equals(message)) {
            message = "Server hasn't responded in time. Seems to be busy. The action may have been successful though. (" + message + ")";
        }
        return message;
    }
    
    public static String concatString(char delimeter, String ... strings){
        if(strings.length == 0){
            return "";
        }else{
            String s = strings[0];
            for(int i = 1; i < strings.length; i ++){
                s += delimeter + strings[i];
            }
            return s;
        }
    }
}
