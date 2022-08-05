package com.foros.model.security;

import java.util.ArrayList;
import java.util.List;

/**
 * User: paresh.morker
 * Date: Feb 25, 2009
 * Time: 7:54:33 PM
 */
public enum TextAdservingMode {
    ONE_TEXT_AD('O', "One text ad only"),
    ONE_TEXT_AD_PER_ADVERTISER('A', "One text ad per advertiser"),
    MULTIPLE_TEXT_ADS('M', "Multiple text ads");

    private final char letter;
    private final String description;

    private TextAdservingMode(char letter, String description) {
        this.letter = letter;
        this.description = description;
    }

    public char getLetter() {
        return letter;
    }

    public String getDescription() {
        return description;
    }

    public static TextAdservingMode valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'O':
                return ONE_TEXT_AD;
            case 'A':
                return ONE_TEXT_AD_PER_ADVERTISER;
            case 'M':
                return MULTIPLE_TEXT_ADS;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }

    public static List<TextAdservingMode> getAllModes() {
        List<TextAdservingMode> all = new ArrayList<TextAdservingMode>();
        all.add(ONE_TEXT_AD);
        all.add(ONE_TEXT_AD_PER_ADVERTISER);
        all.add(MULTIPLE_TEXT_ADS);

        return all;
    }
    
    public static List<TextAdservingMode> getTextAdservingModes(boolean allModes) {
        if (allModes) {
            return getAllModes();
        } else {
            List<TextAdservingMode> textAdservingModeList = new ArrayList<TextAdservingMode>();
            textAdservingModeList.add(TextAdservingMode.ONE_TEXT_AD);
            textAdservingModeList.add(TextAdservingMode.MULTIPLE_TEXT_ADS);

            return textAdservingModeList;
        }
    }
}
