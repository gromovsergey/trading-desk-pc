package com.foros.model.channel;

public enum BehavioralParametersUnits {

    MINUTES("min", 60, 60),
    HOURS("hour", 60 * 60, 24),
    DAYS("day", 24 * 60 * 60, 180);

    private final String name;
    private final long multiplier;
    private final long maxValue;

    private BehavioralParametersUnits(String name, long multiplier, long maxValue) {
        this.name = name;
        this.multiplier = multiplier;
        this.maxValue = maxValue * multiplier;
    }

    public String getName() {
        return name;
    }

    public long getMultiplier() {
        return multiplier;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public static BehavioralParametersUnits byName(String name) {
        for (BehavioralParametersUnits units : values()) {
            if (units.getName().equals(name)) {
                return units;
            }
        }
        throw new IllegalArgumentException("Illegal name given: '" + name + "'");
    }

    public String toString() {
        return getName();
    }

    public static BehavioralParametersUnits calculate(Long timeFrom, Long timeTo) {
        if (timeFrom != null && timeTo != null) {
            for (int i = values().length - 1; i >= 0; i--) {
                long multiplier = values()[i].getMultiplier();
                if (timeFrom % multiplier == 0 && timeTo % multiplier == 0) {
                    return values()[i];
                }
            }
        }
        return null;
    }
}
