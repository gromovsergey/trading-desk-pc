package com.foros.model.campaign;

/**
 * @author Vitaliy_Knyazev
 */
public enum TGTType {
    CHANNEL('C', "Channel"),
    KEYWORD('K', "Keyword");

    private final char letter;
    private final String name;

    TGTType(char letter, String name) {
        this.letter = letter;
        this.name = name;
    }

    public char getLetter() {
        return letter;
    }

    public String getName() {
        return name;
    }

    public static TGTType valueOfString(String value) {
        char c = value != null && value.length() == 1 ? value.charAt(0) : 'C';
        return valueOf(c);
    }

    public static TGTType valueOf(char letter) {
        switch (letter) {
            case 'C': {
                return CHANNEL;
            }
            case 'K': {
                return KEYWORD;
            }
        }

        return CHANNEL;
    }

    @Override
    public String toString() {
        return Character.toString(letter);
    }
}
