package com.foros.model.campaign;

public enum CampaignType {
    DISPLAY('D', "Display"), TEXT('T', "Text");

    private char letter;
    private String extention;

    CampaignType(char letter, String extention) {
        this.letter = letter;
        this.extention = extention;
    }

    public char getLetter() {
        return letter;
    }

    public String getLetterAsString() {
        return "" + letter;
    }

    public String getExtention() {
        return extention;
    }

    public static CampaignType byLetter(char letter) {
        for (CampaignType type : values()) {
            if (type.getLetter() == letter) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
    }
}
