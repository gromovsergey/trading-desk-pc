package com.foros.session.reporting.custom;

public enum DetailLevel {
    date,
    dateAndHour,
    agency,
    advertiser,
    creativeGroup,
    campaignCreative,
    country,
    site,
    colocation;

    public String getNameKey() {
        return  "report.input.field.detailLevel." + name();
    }
}
