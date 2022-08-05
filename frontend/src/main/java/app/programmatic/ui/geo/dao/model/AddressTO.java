package app.programmatic.ui.geo.dao.model;

import java.math.BigDecimal;

public class AddressTO {
    private Long id;
    private String countryCode;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long radius = 1L;
    private RadiusUnit radiusUnits = RadiusUnit.km;

    public AddressTO() {
    }

    public AddressTO(Long id, String countryCode, String address, Long radius, RadiusUnit radiusUnits) {
        this.id = id;
        this.countryCode = countryCode;
        this.address = address;
        this.radius = radius;
        this.radiusUnits = radiusUnits;
    }

    public String getAddress() {
        return address;
    }


    public Address buildAddress() {
        Address address = new Address();
        address.setCountryCode(this.countryCode);
        address.setAddress(this.address);
        address.setLatitude(this.latitude);
        address.setLongitude(this.longitude);
        address.setRadius(this.radius);
        address.setRadiusUnits(this.radiusUnits);

        return address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    public Long getRadius() {
        return radius;
    }

    public void setRadius(Long radius) {
        this.radius = radius;
    }

    public RadiusUnit getRadiusUnits() {
        return radiusUnits;
    }

    public void setRadiusUnits(RadiusUnit radiusUnits) {
        this.radiusUnits = radiusUnits;
    }
}
