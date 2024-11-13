package com.oasisnourish.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeFormatterTest {

    @Test
    public void testFormat_WithOnlySeconds() {
        assertEquals("5 seconds", TimeFormatter.format(5));
        assertEquals("1 second", TimeFormatter.format(1));
    }

    @Test
    public void testFormat_WithMinutesAndSeconds() {
        assertEquals("1 minute, 30 seconds", TimeFormatter.format(90));
        assertEquals("2 minutes, 1 second", TimeFormatter.format(121));
    }

    @Test
    public void testFormat_WithHoursMinutesAndSeconds() {
        assertEquals("1 hour, 1 minute, 1 second", TimeFormatter.format(3661));
        assertEquals("2 hours, 30 minutes", TimeFormatter.format(9000));
    }

    @Test
    public void testFormat_WithDaysHoursMinutesAndSeconds() {
        assertEquals("1 day, 2 hours, 5 seconds", TimeFormatter.format(93605));
        assertEquals("3 days, 1 hour, 59 seconds", TimeFormatter.format(262859));
    }

    @Test
    public void testFormat_WithOnlyDays() {
        assertEquals("2 days", TimeFormatter.format(2 * 86400));
    }

    @Test
    public void testFormat_WithZeroSeconds() {
        assertEquals("0 seconds", TimeFormatter.format(0));
    }

    @Test
    public void testFormat_WithSingleAndPluralUnits() {
        assertEquals("1 day, 1 hour, 1 minute, 1 second", TimeFormatter.format(90061));
        assertEquals("2 days, 2 hours, 2 minutes, 2 seconds", TimeFormatter.format(180122));
    }
}
