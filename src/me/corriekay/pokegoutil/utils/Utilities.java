package me.corriekay.pokegoutil.utils;

import com.google.protobuf.InvalidProtocolBufferException;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class Utilities {
    private Utilities() { /* Prevent initializing this class */ }

    private static final Random random = new Random(System.currentTimeMillis());

    public static boolean isEven(long i) {
        return i % 2 == 0;
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
}
