package com.foros.model.finance;

/**
 * @author olga_glukhova
 */
public enum FinanceStatus {
    OPEN('O', "Open"),
    DUMMY('D', "Uncompleted"),
    CLOSED('C', "Closed"),
    CANCELLED('X', "Cancelled"),
    GENERATED('G', "Generated"),
    PAID('P', "Paid");
    
    private final char letter;
    private final String description;

    private FinanceStatus(char letter, String description) {
        this.letter = letter;
        this.description = description;
    }

    public char getLetter() {
        return letter;
    }

    public String getDescription() {
        return description;
    }

    public static FinanceStatus valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'O':
                return OPEN;
            case 'D':
                return DUMMY;
            case 'C':
                return CLOSED;
            case 'X':
                return CANCELLED;
            case 'G':
                return GENERATED;
            case 'P':
                return PAID;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }
}
