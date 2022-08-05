package com.foros.model.security;

public enum AdvExclusionsType {

    DISABLED('D'),
    SITE_LEVEL('S'),
    SITE_AND_TAG_LEVELS('T');

    private final char letter;

    AdvExclusionsType(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static AdvExclusionsType byLetter(char letter) {
        for (AdvExclusionsType type : values()) {
            if (type.getLetter() == letter) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
    }

    public static AdvExclusionsType valueOf(char letter) {
        switch (letter) {
            case 'D':
                return DISABLED;
            case 'S':
                return SITE_LEVEL;
            case 'T':
                return SITE_AND_TAG_LEVELS;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }
}
