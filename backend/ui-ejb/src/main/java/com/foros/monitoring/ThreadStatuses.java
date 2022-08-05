package com.foros.monitoring;

public enum ThreadStatuses {
    NOT_ALIVE(0),
    ALIVE(1),
    NOT_NEEDED(2);

    final int numericStatus;

    ThreadStatuses(int numericStatus) {
        this.numericStatus = numericStatus;
    }

    public int getValue() {
        return numericStatus;
    }
}
