package com.foros.util;

import java.util.Date;

/**
 * @author Vladimir
 */
public class DSTimeInterval implements Comparable<DSTimeInterval> {
    private int days;
    private int hours;
    private int minutes;
    private int seconds;

    private boolean isNegative = false;

    public DSTimeInterval(Long interval) {
        if (interval < 0) {
            interval = -interval;
            isNegative = true;
        }

        interval /= 1000;
        seconds = (int) (interval % 60);
        interval /= 60;
        minutes = (int) (interval % 60);
        interval /= 60;
        hours = (int) (interval % 24);
        days = (int) (interval / 24);
    }

    public DSTimeInterval(Date start, Date end) {
        this(end.getTime() - start.getTime());
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public int compareTo(DSTimeInterval dsTimeInterval) {
        if (dsTimeInterval == null) {
            return -1;
        }
        
        int thisSeconds = seconds + 60 * minutes + 60 * 60 * hours + 60 * 60 * 24 * days;
        int otherSeconds = dsTimeInterval.seconds + 60 * dsTimeInterval.minutes + 60 * 60 * dsTimeInterval.hours +
                60 * 60 * 24 * dsTimeInterval.days;

        return thisSeconds - otherSeconds;
    }
}
