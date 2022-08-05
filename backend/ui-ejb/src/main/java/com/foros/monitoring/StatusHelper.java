package com.foros.monitoring;

public class StatusHelper {
    private static final long THRESHOLD = 30000;

    public static boolean isLastProcessedTimeInThreshold(long interval, long lastProcessedTime) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastProcessedTime > interval + THRESHOLD) {
            return false;
        }
        return true;
    }
}
