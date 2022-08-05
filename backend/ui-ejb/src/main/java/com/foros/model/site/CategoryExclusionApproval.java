package com.foros.model.site;

public enum CategoryExclusionApproval {
    ACCEPT('A', "Accept"),
    APPROVAL('P', "Approval"),
    REJECT('R', "Reject");
    
    private final char letter;
    private final String description;

    private CategoryExclusionApproval(char letter, String description) {
        this.letter = letter;
        this.description = description;
    }

    public char getLetter() {
        return letter;
    }

    public String getDescription() {
        return description;
    }

    public static CategoryExclusionApproval valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'A':
                return ACCEPT;
            case 'P':
                return APPROVAL;
            case 'R':
                return REJECT;
            case '\u0000':
                return null;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }
}
