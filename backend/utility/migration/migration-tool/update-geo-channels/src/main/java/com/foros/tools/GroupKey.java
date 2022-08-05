package com.foros.tools;

public class GroupKey {

    private GeoCoordinates coordinates;
    private String countryCode;
    private String regionKey;
    private String region;

    public GroupKey(LocationName locationName, LocationDetail locationDetail) {
        this.coordinates = locationDetail.getCoordinates();
        this.countryCode = locationName.getCountryCode();
        this.regionKey = locationName.getRegion();// toLower
        this.region = locationDetail.getRegion();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GroupKey key = (GroupKey) o;

        if (!coordinates.equals(key.coordinates)) {
            return false;
        }
        if (!countryCode.equals(key.countryCode)) {
            return false;
        }
        if (regionKey != null ? !regionKey.equals(key.regionKey) : key.regionKey != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = coordinates.hashCode();
        result = 31 * result + countryCode.hashCode();
        result = 31 * result + (regionKey != null ? regionKey.hashCode() : 0);
        return result;
    }
}
