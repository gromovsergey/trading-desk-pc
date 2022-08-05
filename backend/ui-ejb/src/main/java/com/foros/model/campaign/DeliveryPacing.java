package com.foros.model.campaign;

public enum DeliveryPacing {
    UNRESTRICTED('U', "Unrestricted"),
    FIXED('F', "Fixed"),
    DYNAMIC('D', "Dynamic");

    private final char letter;
    private final String name;

    private DeliveryPacing(char letter, String name) {
        this.letter = letter;
        this.name = name;
    }

    public char getLetter() {
        return letter;
    }

    public String getName() {
        return name;
    }

    public static DeliveryPacing valueOfString(String value) {
        char c = value != null && value.length() == 1 ? value.charAt(0) : 'U';
        return valueOf(c);
    }

    public static DeliveryPacing byName(String name) {
        return byName(name, false);
    }

    public static DeliveryPacing byNameIgnoreCase(String name) {
        return byName(name, true);
    }

    private static DeliveryPacing byName(String name, boolean ignoreCase) {
        for (DeliveryPacing dp : values()) {
            String dpName = dp.getName();
            if (ignoreCase ? dpName.equalsIgnoreCase(name) : dpName.equals(name)) {
                return dp;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }

    public static DeliveryPacing valueOf(char letter) {
        switch (letter) {
            case 'U':
                return UNRESTRICTED;
            case 'F':
                return FIXED;
            case 'D':
                return DYNAMIC;
            default:
                throw new IllegalArgumentException("Invalid letter: " + letter);
        }
    }
}
