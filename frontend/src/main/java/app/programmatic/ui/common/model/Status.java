package app.programmatic.ui.common.model;

public enum Status {
    ACTIVE('A'),
    INACTIVE('I'),
    DELETED('D');

    private final char letter;

    private Status(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static Status valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'A':
                return ACTIVE;
            case 'I':
                return INACTIVE;
            case 'D':
                return DELETED;
            default:
                if (!Character.isLetterOrDigit(letter)) {
                    return null;
                } else {
                    throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
                }
        }
    }
}