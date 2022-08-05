package com.foros.model.channel.trigger;

public enum TriggerChannelType {
    ADVERTISING("A"),
    DISCOVER("D"),
    SPECIAL("S");

    private String letter;

    private TriggerChannelType(String letter) {
        this.letter = letter;
    }

    public String getLetter() {
        return letter;
    }

    public static TriggerChannelType valueOfString(String value) {
        if (value == null || value.length() != 1) {
            throw new IllegalArgumentException("Illegal trigger channel type code: " + value + ". Valid: [A, D, S]");
        }
        return valueOf(value.charAt(0));
    }

    public static TriggerChannelType valueOf(char letter) {
        for (TriggerChannelType triggerChannelType : TriggerChannelType.values()) {
            if (triggerChannelType.getLetter().equals(Character.toString(letter))) {
                return triggerChannelType;
            }
        }
        throw new IllegalArgumentException("Illegal trigger channel type code: " + letter + ". Valid: [A, D, S]");
    }
}
