package com.foros.model.channel.trigger;

import com.foros.model.channel.BehavioralParameters;

import java.util.Collection;

public enum TriggerType {

    PAGE_KEYWORD('P'), SEARCH_KEYWORD('S'), URL('U'), URL_KEYWORD('R');

    private char letter;

    TriggerType(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public boolean equalsTo(Character triggerType) {
        return Character.valueOf(letter).equals(triggerType);
    }

    public static TriggerType byString(String value) {
        if (value == null || value.length() != 1) {
            throw new IllegalArgumentException("Illegal trigger type code: " + value + ". Valid: [P, S, U, R]");
        }
        return byCode(value.charAt(0));
    }

    public static TriggerType byCode(char letter) {
        for (TriggerType triggerType : TriggerType.values()) {
            if (triggerType.getLetter() == letter) {
                return triggerType;
            }
        }
        throw new IllegalArgumentException("Illegal trigger type code: " + letter + ". Valid: [P, S, U, R]");
    }

    public static BehavioralParameters findBehavioralParameters(Collection<BehavioralParameters> parameters, TriggerType triggerType) {
        for (BehavioralParameters params : parameters) {
            if (params != null && triggerType.equalsTo(params.getTriggerType())) {
                return params;
            }
        }
        return null;
    }
}
