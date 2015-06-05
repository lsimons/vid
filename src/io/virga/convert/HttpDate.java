/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public final class HttpDate {
    private static final ConcurrentHashMap<Long, String> formatCache = new ConcurrentHashMap<>(100);
    private static final ConcurrentHashMap<String, Long> parseCache = new ConcurrentHashMap<>(100);
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final ThreadLocal<DateFormat> formatFormats = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        }
    };
    private static final ThreadLocal<DateFormat[]> parseFormats = new ThreadLocal<DateFormat[]>() {
        @Override
        protected DateFormat[] initialValue() {
            return new DateFormat[]{new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US), new SimpleDateFormat(
                    "EEE MMMM d HH:mm:ss yyyy", Locale.US), new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz",
                    Locale.US)};
        }
    };

    private static DateFormat getFormatter() {
        return formatFormats.get();
    }

    private static DateFormat[] getParsers() {
        return parseFormats.get();
    }

    public static final String format(long value) {
        Long longValue = new Long(value);
        String cachedDate = formatCache.get(longValue);
        if (cachedDate != null) {
            return cachedDate;
        }

        Date dateValue = new Date(value);
        String newDate = getFormatter().format(dateValue);
        updateCache(newDate, longValue);
        return newDate;
    }

    public static long parse(String value) {
        Long cachedDate = parseCache.get(value);
        if (cachedDate != null) {
            return cachedDate.longValue();
        }

        Date dateValue = null;
        for (DateFormat parser : getParsers()) {
            try {
                dateValue = parser.parse(value);
            } catch (ParseException e) {
                // ignore...
            }
        }
        if (dateValue == null) {
            return -1L;
        }
        long newDate = dateValue.getTime();
        updateCache(value, newDate);
        return newDate;
    }

    private static void updateCache(String key, Long value) {
        if (value == null) {
            return;
        }
        if (formatCache.size() >= 100) {
            formatCache.clear();
        }
        if (parseCache.size() >= 100) {
            parseCache.clear();
        }
        parseCache.put(key, value);
        formatCache.put(value, key);
    }
}
