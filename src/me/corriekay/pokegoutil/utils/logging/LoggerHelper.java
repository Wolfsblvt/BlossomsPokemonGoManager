package me.corriekay.pokegoutil.utils.logging;

public final class LoggerHelper {
    /** Prevent initializing this class. */
    private LoggerHelper() {
    }

    public static void logExceptionMessage(Exception e) {
        System.out.println("Exception " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }
}
