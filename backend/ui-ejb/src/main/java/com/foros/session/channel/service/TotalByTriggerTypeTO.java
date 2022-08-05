package com.foros.session.channel.service;

public class TotalByTriggerTypeTO {
    private int approved;
    private int declined;
    private int pending;
    private int negative;

    public int getTotal() {
        return approved + declined + pending + negative;
    }

    public boolean isSeveralStatuses() {
        int positive = getTotal() - negative;
        return approved != positive && declined != positive && pending != positive;
    }

    public char getSingleStatus() {
        if (approved > 0)
            return 'A';
        if (declined > 0)
            return 'D';
        return 'H';
    }

    public boolean isFullyNegative() {
        return negative == getTotal();
    }

    public int getApproved() {
        return approved;
    }

    public void addApproved(int approved) {
        this.approved += approved;
    }

    public int getDeclined() {
        return declined;
    }

    public void addDeclined(int declined) {
        this.declined += declined;
    }

    public int getPending() {
        return pending;
    }

    public void addPending(int pending) {
        this.pending += pending;
    }

    public int getNegative() {
        return negative;
    }

    public void addNegative(int negative) {
        this.negative += negative;
    }
}
