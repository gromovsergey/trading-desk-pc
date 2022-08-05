package com.foros.model;

public enum ApproveStatus {
    APPROVED('A'),
    DECLINED('D'),
    HOLD('H');
    
    private final char letter;

    private ApproveStatus(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static ApproveStatus valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'A':
                return APPROVED;
            case 'D':
                return DECLINED;
            case 'H':
            default:
                return HOLD;
        }
    }

    public String toString() {
        switch (letter) {
            case 'A':
                return "Approved";
            case 'D':
                return "Declined";
            case 'H':
                return "Awaiting Approval";
            case 'F':
                return "Approved with triggers"; // for past audit log messages
            case 'T':
                return "Pending Triggers Review"; // for past audit log messages
            default:
                return "Incorrect status";
        }
    }

    public String getResourceKey() {
        return "approval.description.status." + letter;
    }

}
