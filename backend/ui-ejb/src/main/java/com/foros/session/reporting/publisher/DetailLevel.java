package com.foros.session.reporting.publisher;

public enum DetailLevel {
    date,
    site,
    siteTag,
    custom;

    public String getNameKey() {
        return  "report.input.field.detailLevel." + name();
    }
}
