package com.foros.tools;

import java.math.BigDecimal;

public class GeoCoordinates {
    private BigDecimal latitude;
    private BigDecimal longitude;

    public GeoCoordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoCoordinates(String latitude, String longitude) {
        this.latitude = new BigDecimal(latitude);
        this.longitude = new BigDecimal(longitude);
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GeoCoordinates that = (GeoCoordinates) o;

        if (latitude.compareTo(that.latitude) != 0) {
            return false;
        }
        if (longitude.compareTo(that.longitude) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        long result = latitude.multiply(BigDecimal.valueOf(10000)).longValueExact();
        result = 7919 * result + longitude.multiply(BigDecimal.valueOf(10000)).longValueExact();
        return (int) result;
    }

    @Override
    public String toString() {
        return "[" + latitude + ";" + longitude + "]";
    }
}
