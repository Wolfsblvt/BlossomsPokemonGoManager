package me.corriekay.pokegoutil.utils.helpers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public final class DateHelper {
    /** Prevent initializing this class. */
    private DateHelper() {
    }

    public static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String timeFormat = "HH:mm:ss";
    public static final ZoneId zoneId = ZoneId.systemDefault();

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timeFormat);

    public static LocalDateTime fromTimestamp(long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), zoneId);
    }

    public static String toString(LocalDateTime localDateTime) {
        return localDateTime.format(dateTimeFormatter);
    }

    public static LocalDateTime fromString(String localDateTimeString) {
        return LocalDateTime.parse(localDateTimeString, dateTimeFormatter);
    }

    public static String currentTime() {
        return LocalDateTime.now().format(timeFormatter);
    }
}
