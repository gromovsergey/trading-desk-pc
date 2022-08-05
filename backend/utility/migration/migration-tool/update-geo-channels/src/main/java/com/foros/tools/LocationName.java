package com.foros.tools;

public class LocationName {
    private String countryCode;
    private String region;
    private String city;

    public LocationName(String countryCode, String region, String city) {
        this.countryCode = countryCode;
        this.region = region;
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocationName locationName = (LocationName) o;

        if (!countryCode.equals(locationName.countryCode)) {
            return false;
        }
        if (region != null ? !region.equals(locationName.region) : locationName.region != null) {
            return false;
        }
        if (city != null ? !city.equals(locationName.city) : locationName.city != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = countryCode.hashCode();
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + countryCode + "/" + region + "/" + city + ")";
    }
}
