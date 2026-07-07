package com.healthsync.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utility functions for Date-Time formatting and parsing.
 */
public final class DateUtil {
    private DateUtil() {}

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public static String formatIso(Instant instant) {
        return instant == null ? null : ISO_FORMATTER.format(instant);
    }

    public static Instant parseIso(String isoString) {
        return isoString == null ? null : Instant.parse(isoString);
    }

    public static Instant toUtcInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
}
