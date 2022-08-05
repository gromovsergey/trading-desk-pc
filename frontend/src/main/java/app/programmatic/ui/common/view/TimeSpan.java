package app.programmatic.ui.common.view;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TimeSpan {
    private Long value;
    private TimeUnit unit;

    public TimeSpan() {
    }

    public TimeSpan(TimeSpan ts) {
        this.value = ts.value;
        this.unit = ts.unit;
    }

    public TimeSpan(Long value, TimeUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    @JsonIgnore
    public Long getValueInSeconds() {
        if (value == null || unit == null) {
            return null;
        }

        return unit.getMultiplier() * value;
    }

    public void setValueInSeconds(Long seconds) {
        if (seconds == null) {
            value = null;
            unit = null;
        }

        unit = TimeUnit.determine(seconds);
        value = unit.convertToUnits(seconds);
    }

    public static Integer getValueInSecondsInt(TimeSpan span) {
        if (span == null) {
            return null;
        }
        Long seconds = span.getValueInSeconds();
        return seconds == null ? null : seconds.intValue();
    }

    public static Long getValueInSeconds(TimeSpan span) {
        return span == null ? null : span.getValueInSeconds();
    }

    public static TimeSpan fromSeconds(Long seconds) {
        if (seconds == null) {
            return null;
        }

        TimeSpan timeSpan = new TimeSpan();
        timeSpan.setValueInSeconds(seconds);
        return timeSpan;
    }

    public static TimeSpan fromSeconds(Integer seconds) {
        if (seconds == null) {
            return null;
        }

        return fromSeconds(seconds.longValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimeSpan timeSpan = (TimeSpan) o;

        if (this.getValueInSeconds() != null && !this.getValueInSeconds().equals(timeSpan.getValueInSeconds())) {
            return false;
        }

        if (unit == null && timeSpan.unit != null) {
            return false;
        }

        if (value == null && timeSpan.unit != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        Long valueInSeconds = this.getValueInSeconds();
        if (valueInSeconds != null) {
            return 31 * valueInSeconds.hashCode();
        } else {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (unit != null ? unit.hashCode() : 0);
            return result;
        }
    }

    @Override
    public String toString() {
        return "TimeSpan[" + value + " " + unit + "(" + getValueInSeconds() + ")]";
    }
}
