package com.foros.model.security;


public enum AdvExclusionsApprovalType {
    ACCEPTED('A'),
    REJECTED('R');

    private char letter;

    private AdvExclusionsApprovalType(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static AdvExclusionsApprovalType byLetter(char letter) {
        for (AdvExclusionsApprovalType type : values()) {
            if (type.getLetter() == letter) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
    }

    public static AdvExclusionsApprovalType valueOf(char letter) {
        switch (letter) {
            case 'A':
                return ACCEPTED;
            case 'R':
                return REJECTED;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }
}
