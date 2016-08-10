package me.corriekay.pokegoutil.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class DateHelper {
    public static final String format = "yyyy-MM-dd HH:mm:ss";
    public static final ZoneId zoneId = ZoneId.systemDefault();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

    public static LocalDateTime fromTimestamp(long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), zoneId);
    }

    public static String toString(LocalDateTime localDateTime) {
        return localDateTime.format(formatter);
    }

    public static LocalDateTime fromString(String localDateTimeString) {
        return LocalDateTime.parse(localDateTimeString, formatter);
    }
}
