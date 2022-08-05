package com.foros.model.campaign;

/**
 * @author dmitry_antonov
 * @since 29.04.2008
 */
public enum CCGType {

    TEXT('T', "Text"),
    DISPLAY('D', "Display");

    private final char letter;
    private final String pageExtension;

    CCGType(char letter, String pageExtension) {
        this.letter = letter;
        this.pageExtension = pageExtension;
    }

    public char getLetter() {
        return letter;
    }

    public String getPageExtension() {
        return pageExtension;
    }

    public static CCGType valueOfString(String value) {
        char c = value != null && value.length() == 1 ? value.charAt(0) : 'D';
        return valueOf(c);
    }

    public static CCGType valueOf(char letter) {
        switch (letter) {
            case 'T': {
                return TEXT;
            }
            case 'D': {
                return DISPLAY;
            }
            default: {
                //default value
                return DISPLAY;
            }
        }
    }

    public String toString() {
        return Character.toString(letter);
    }

    public static CCGType valueOf(CampaignType campaignType) {
        if (campaignType == null) {
            return null;
        }
        switch (campaignType) {
            case DISPLAY:
                return DISPLAY;
            case TEXT:
                return TEXT;
            default:
                throw new IllegalArgumentException(campaignType.toString());
        }
    }
}
