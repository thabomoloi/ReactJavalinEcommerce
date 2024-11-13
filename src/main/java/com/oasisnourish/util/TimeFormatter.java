package com.oasisnourish.util;

public class TimeFormatter {

    public static String format(long seconds) {
        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder readableTime = new StringBuilder();

        if (days > 0) {
            readableTime.append(days).append(" day").append(days != 1 ? "s" : "").append(", ");
        }
        if (hours > 0) {
            readableTime.append(hours).append(" hour").append(hours != 1 ? "s" : "").append(", ");
        }
        if (minutes > 0) {
            readableTime.append(minutes).append(" minute").append(minutes != 1 ? "s" : "").append(", ");
        }
        if (seconds > 0 || readableTime.length() == 0) { // Include seconds or if all other values are zero
            readableTime.append(seconds).append(" second").append(seconds != 1 ? "s" : "");
        }

        // Remove trailing comma and space if present
        if (readableTime.toString().endsWith(", ")) {
            readableTime.setLength(readableTime.length() - 2);
        }

        return readableTime.toString();
    }
}
