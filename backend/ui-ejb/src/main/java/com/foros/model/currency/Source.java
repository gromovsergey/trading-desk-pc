package com.foros.model.currency;


public enum Source {
    FEED('F', "Currency.source.feed"),
    MANUAL('M', "Currency.source.manual");
    
    private final char letter;
    private final String resourceKey;

    private Source(char letter, String resourceKey) {
        this.letter = letter;
        this.resourceKey = resourceKey;
    }

    public char getLetter() {
        return letter;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static Source valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'F':
                return FEED;
            case 'M':
                return MANUAL;
            default:
                if (!Character.isLetterOrDigit(letter)) {
                    return null;
                }
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }
}
