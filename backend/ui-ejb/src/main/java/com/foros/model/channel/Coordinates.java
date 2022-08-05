package com.foros.model.channel;

import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlElement;

@Embeddable
public class Coordinates implements Serializable {

    @Column(name = "LATITUDE")
    @RequiredConstraint
    private BigDecimal latitude;

    @Column(name = "LONGITUDE")
    @RequiredConstraint
    private BigDecimal longitude;

    public Coordinates() {
    }

    public Coordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @XmlElement
    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    @XmlElement
    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coordinates (" + latitude + ", " + longitude + ")";
    }
}
