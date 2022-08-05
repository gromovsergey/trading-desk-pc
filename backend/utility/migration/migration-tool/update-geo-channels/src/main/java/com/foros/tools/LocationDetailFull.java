package com.foros.tools;

public class LocationDetailFull {
    private LocationName name;
    private LocationDetail detail;
    private String aliases;

    public LocationDetailFull(LocationName name, LocationDetail detail, String aliases) {
        this.name = name;
        this.detail = detail;
        this.aliases = aliases;
    }

    public LocationName getName() {
        return name;
    }

    public LocationDetail getDetail() {
        return detail;
    }

    public String getAliases() {
        return aliases;
    }
}
