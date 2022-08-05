package com.foros.model.quicksearch;

public enum Type {
    AGENCY("agencies"),
    ADVERTISER,
    CAMPAIGN,
    CHANNEL,
    PUBLISHER,
    SITE;

    private static final String RES_PREFIX = "quicksearch.";

    private String pluralKey;

    private Type(String key) {
        this.pluralKey = RES_PREFIX + key;
    }

    private Type() {
        this.pluralKey = RES_PREFIX + name().toLowerCase() + "s";
    }

    public String getPluralKey() {
        return pluralKey;
    }
}
