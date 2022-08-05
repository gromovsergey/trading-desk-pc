package com.foros.model.channel;

import com.foros.model.channel.trigger.TriggerType;

public enum KeywordTriggerType {

    PAGE_KEYWORD('P', "page", TriggerType.PAGE_KEYWORD),
    SEARCH_KEYWORD('S', "search", TriggerType.SEARCH_KEYWORD);

    private final char letter;
    private final String name;
    private TriggerType triggerType;

    private KeywordTriggerType(char letter, String name, TriggerType triggerType) {
        this.letter = letter;
        this.name = name;
        this.triggerType = triggerType;
    }

    public char getLetter() {
        return letter;
    }

    public String getName() {
        return name;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public static KeywordTriggerType byName(String name) {
        for (KeywordTriggerType triggerType : values()) {
            if (triggerType.getName().equals(name)) {
                return triggerType;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }

    public static KeywordTriggerType byLetter(char letter) {
        for (KeywordTriggerType triggerType : values()) {
            if (triggerType.getLetter() == letter) {
                return triggerType;
            }
        }
        throw new IllegalArgumentException("Illegal keyword trigger type code: " + letter + ". Valid: [P, S]");
    }
}
