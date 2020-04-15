package com.gzu.queswer.util;

public class DateUtil {
    private DateUtil() {
    }

    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }
}
