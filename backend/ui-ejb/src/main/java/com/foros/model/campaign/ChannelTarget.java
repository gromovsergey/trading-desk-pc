package com.foros.model.campaign;

public enum ChannelTarget {
    NOT_SET('N'),
    UNTARGETED('U'),
    TARGETED('T');

    private final char letter;

    private ChannelTarget(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static ChannelTarget valueOf(char letter) {
        switch (letter) {
            case 'N':
                return NOT_SET;
            case 'U':
                return UNTARGETED;
            case 'T':
                return TARGETED;
            case '\u0000':
                return null;
            default:
                return NOT_SET;
        }
    }
}
