package com.foros.model.site;

public enum SiteCreativeApprovalStatus {
    APPROVED('A'),
    REJECTED('R'),
    PENDING('P'),
    CREATIVE_CATEGORY_APPROVED('C');

    private final char letter;

    private SiteCreativeApprovalStatus(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static SiteCreativeApprovalStatus valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'A':
                return APPROVED;
            case 'R':
                return REJECTED;
            case 'P':
                return PENDING;
            case 'C':
                return CREATIVE_CATEGORY_APPROVED;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }
}
