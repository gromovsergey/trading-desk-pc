package com.foros.model.channel;

import java.io.Serializable;
import java.math.BigDecimal;

public class GeoChannelAddress implements Serializable {

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal radius;

    private RadiusUnit radiusUnits;

    public GeoChannelAddress() {
    }

    public GeoChannelAddress(String address, BigDecimal latitude, BigDecimal longitude, BigDecimal radius, RadiusUnit radiusUnits) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.radiusUnits = radiusUnits;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getRadius() {
        return radius;
    }

    public void setRadius(BigDecimal radius) {
        this.radius = radius;
    }

    public RadiusUnit getRadiusUnits() {
        return radiusUnits;
    }

    public void setRadiusUnits(RadiusUnit radiusUnits) {
        this.radiusUnits = radiusUnits;
    }
}