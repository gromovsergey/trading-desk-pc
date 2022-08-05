package com.foros.model;

import java.util.Collection;

public enum Status {
    ACTIVE('A', "Active"),
    INACTIVE('I', "Inactive"),
    DELETED('D', "Deleted"),
    PENDING('P', "Pending"),
    PENDING_INACTIVATION('E', "Pending");
    
    private final char letter;
    private final String description;

    private Status(char letter, String description) {
        this.letter = letter;
        this.description = description;
    }

    public char getLetter() {
        return letter;
    }

    public String getDescription() {
        return description;
    }

    public static Status valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'A':
                return ACTIVE;
            case 'I':
                return INACTIVE;
            case 'D':
                return DELETED;
            case 'P':
                return PENDING;
            case 'E':
                return PENDING_INACTIVATION;
            default:
                if (!Character.isLetterOrDigit(letter)) {
                    return null;
                } else {
                    throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
                }
        }
    }
    
    public Status combine(Status other) {
        switch (this) {
        case ACTIVE:
        case PENDING:
        case PENDING_INACTIVATION:
            if (other == ACTIVE) return this;
            else return other;
        case INACTIVE:
            if (other == ACTIVE || other == PENDING) return this;
            else return other;
        default:
            return this;
        }
    }

    public static Character[] getStatusCodes(Collection<Status> statuses) {
        Character[] result = new Character[statuses.size()];

        int index = 0;
        for (Status status : statuses) {
            result[index++] = status.getLetter();
        }

        return result;
    }
}
