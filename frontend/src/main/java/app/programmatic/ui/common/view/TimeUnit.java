package app.programmatic.ui.common.view;

public enum TimeUnit {
    SECOND('S', 1, "interval.second"),
    MINUTE('M', 60, "interval.minute"),
    HOUR('H', 60 * 60, "interval.hour"),
    DAY('D', 60 * 60 * 24, "interval.day"),
    WEEK('W', 60 * 60 * 24 * 7, "interval.week");

    private char unitValue;
    private int multiplier;
    private String messageKey;

    TimeUnit(char unitValue, int multiplier, String messageKey) {
        this.unitValue = unitValue;
        this.multiplier = multiplier;
        this.messageKey = messageKey;
    }

    public char getUnitValue() {
        return unitValue;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public static TimeUnit valueOf(char value) {
        for (TimeUnit timeUnit : values()) {
            if (timeUnit.getUnitValue() == value) {
                return timeUnit;
            }
        }
        throw new IllegalArgumentException("Unknown frequency cap field timeUnit: '" + value + "'");
    }

    public Long convertToSecondsOverflowSafe(Integer value) {
        if (value != null) {
            return ((long) value) * ((long) multiplier);
        }
        return null;
    }

    public Long convertToUnits(Long value) {
        if (value == null) {
            return value;
        }
        return value / multiplier;
    }

    public static TimeUnit determine(Long value) {
        if (value == null) {
            return DAY;
        }

        TimeUnit result = SECOND;
        for (TimeUnit timeUnit : values()) {
            if (value % timeUnit.multiplier == 0 && timeUnit.multiplier > result.multiplier) {
                result = timeUnit;
            }
        }
        return result;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
