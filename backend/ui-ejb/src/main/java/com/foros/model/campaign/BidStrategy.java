package com.foros.model.campaign;

public enum BidStrategy {
    MAXIMISE_REACH('R'),
    MINIMUM_CTR_GOAL('C');

    private final char letter;

    private BidStrategy(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static BidStrategy valueOf(char letter) {
        switch (letter) {
            case 'R':
                return MAXIMISE_REACH;
            case 'C':
                return MINIMUM_CTR_GOAL;
            default:
                throw new IllegalArgumentException("Invalid letter: " + letter);
        }
    }
}
