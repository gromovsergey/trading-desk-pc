package com.foros.model.channel.placementsBlacklist;

public enum BlacklistAction {
    ADD,
    REMOVE;

    public static BlacklistAction parse(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        return valueOf(stringValue.toUpperCase());
    }
}
