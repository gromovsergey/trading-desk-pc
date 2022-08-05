package com.foros.model.security;


public enum InternalAccessType {
    USER_ACCOUNT('U'), MULTIPLE_ACCOUNTS('M'), ALL_ACCOUNTS('A');

    private char letter;

    private InternalAccessType(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static InternalAccessType byLetter(char letter) {
        for (InternalAccessType type : values()) {
            if (type.getLetter() == letter) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
    }
}
