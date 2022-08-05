package com.foros.session.reporting.channelUsage;

public enum DetailLevel {
    date, channel;

    public String getNameKey() {
        return "report.input.field.detailLevel." + name();
    }
}
