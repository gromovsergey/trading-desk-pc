package com.foros.model.security;

public enum ResultType {
    SUCCESS('S'),
    FAILURE('F');

    private final char letter;

    ResultType(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static ResultType valueOf(char letter) {
        switch (letter) {
            case 'S':
                return SUCCESS;
            case 'F':
                return FAILURE;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }
}
