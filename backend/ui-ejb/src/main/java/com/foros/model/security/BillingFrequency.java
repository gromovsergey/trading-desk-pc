package com.foros.model.security;

public enum BillingFrequency {
    WEEKLY('W', "Weekly", 1, 7),
    BIWEEKLY('B', "Biweekly", 1, 14),
    MONTHLY('M', "Monthly", 1, 28);

    private final char letter;
    private final String description;
    private final int min;
    private final int max;

    private BillingFrequency (char letter, String description, int min, int max) {
        this.letter = letter;
        this.description = description;
        this.min = min;
        this.max = max;
    }

    public char getLetter() {
        return letter;
    }

    public String getDescription() {
        return description;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public static BillingFrequency valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'W':
                return WEEKLY;
            case 'B':
                return BIWEEKLY;
            case 'M':
                return MONTHLY;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }

    public static BillingFrequency getDefault() {
        return MONTHLY;
    }
}
