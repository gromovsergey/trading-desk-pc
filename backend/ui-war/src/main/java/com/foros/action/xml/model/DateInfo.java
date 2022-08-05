package com.foros.action.xml.model;

/**
 * User: Nitin Afre
 * Date: Jul 13, 2009
 * Time: 4:33:36 PM
 */
public class DateInfo {
    private String datePart;
    private String timePart;

    public DateInfo(String datePart, String timePart) {
        this.datePart = datePart;
        this.timePart = timePart;
    }

    public String getDatePart() {
        return datePart;
    }

    public void setDatePart(String datePart) {
        this.datePart = datePart;
    }

    public String getTimePart() {
        return timePart;
    }

    public void setTimePart(String timePart) {
        this.timePart = timePart;
    }
}
