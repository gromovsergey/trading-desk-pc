package com.foros.tools;

import java.util.SortedSet;

public class LocationDetail {

    public enum Status {
        NEW,
        UNCHANGED,
        UPDATED,
        DELETED
    }

    private GeoCoordinates coordinates;
    private String region;
    private Long channelId;
    private String name;
    private SortedSet<String> aliases;
    private Status status;

    public LocationDetail(GeoCoordinates coordinates, String region, Long channelId, String name, SortedSet<String> aliases) {
        this.coordinates = coordinates;
        this.region = region;
        this.channelId = channelId;
        this.name = name;
        this.aliases = aliases;
    }

    public LocationDetail(GeoCoordinates coordinates, String region, String name, Status status) {
        this.coordinates = coordinates;
        this.region = region;
        this.name = name;
        this.status = status;
    }

    public GeoCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getChannelId() {
        return channelId;
    }

    public SortedSet<String> getAliases() {
        return aliases;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "(" + coordinates + "/" + status + "/" + channelId + ")";
    }
}
