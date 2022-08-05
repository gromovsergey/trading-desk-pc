package com.foros.model.campaign;

public enum CampaignAllocationStatus {
    ACTIVE('A'),
    ENDED('E');

    private char letter;

    CampaignAllocationStatus(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static CampaignAllocationStatus byLetter(char letter) {
        for (CampaignAllocationStatus type : values()) {
            if (type.getLetter() == letter) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
    }
}
