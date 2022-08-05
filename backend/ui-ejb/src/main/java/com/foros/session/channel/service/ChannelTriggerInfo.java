package com.foros.session.channel.service;

public class ChannelTriggerInfo {
    private int negativePageCount;
    private int  negativeSearchCount;
    private int  negativeUrlCount;

    public ChannelTriggerInfo() {
    }

    public int getNegativePageCount() {
        return negativePageCount;
    }

    public int getNegativeSearchCount() {
        return negativeSearchCount;
    }

    public int getNegativeUrlCount() {
        return negativeUrlCount;
    }

    public void setNegativePageCount(int negativePageCount) {
        this.negativePageCount = negativePageCount;
    }

    public void setNegativeSearchCount(int negativeSearchCount) {
        this.negativeSearchCount = negativeSearchCount;
    }

    public void setNegativeUrlCount(int negativeUrlCount) {
        this.negativeUrlCount = negativeUrlCount;
    }

    @Override
    public String toString() {
        return "ChannelTriggerInfo [negativePageCount=" + negativePageCount
                + ", negativeSearchCount=" + negativeSearchCount
                + ", negativeUrlCount=" + negativeUrlCount + "]";
    }
}
