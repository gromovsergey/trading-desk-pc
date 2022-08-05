package com.foros.session.channel.triggerQA;

public enum TriggerQAType {

    KEYWORD('K'), URL('U');

    private char letter;

    TriggerQAType(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static TriggerQAType valueByLetter(char letter) {
        for (TriggerQAType triggerType : TriggerQAType.values()) {
            if (triggerType.getLetter() == letter) {
                return triggerType;
            }
        }
        throw new IllegalArgumentException("Illegal trigger type code: " + letter + ". Valid: [K, U]");
    }
}
