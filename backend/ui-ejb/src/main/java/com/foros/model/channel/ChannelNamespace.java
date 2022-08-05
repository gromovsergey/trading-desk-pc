package com.foros.model.channel;

public enum ChannelNamespace {
    ADVERTISING('A'), DISCOVER('D'), DISCOVER_LIST('L'), CATEGORY('C'), KEYWORD('K'), GEO('G'), DEVICE('V'), SPECIAL('S');

    private char letter;

    private ChannelNamespace(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static ChannelNamespace byLetter(char letter) {
        for (ChannelNamespace type : values()) {
            if (type.getLetter() == letter) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
    }
}
