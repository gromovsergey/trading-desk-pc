package com.foros.model.channel;

import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlElement;

@Embeddable
public class Radius implements Serializable {

    @Column(name = "RADIUS")
    @RequiredConstraint
    @RangeConstraint(min = "1", max = "9999999999")
    private BigDecimal distance;

    @Column(name = "RADIUS_UNITS")
    @RequiredConstraint
    @Enumerated(EnumType.STRING)
    private RadiusUnit radiusUnit;

    public Radius() {
    }

    public Radius(BigDecimal distance, RadiusUnit radiusUnit) {
        super();
        this.distance = distance;
        this.radiusUnit = radiusUnit;
    }

    @XmlElement
    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal Distance) {
        this.distance = Distance;
    }

    @XmlElement(name = "unit")
    public RadiusUnit getRadiusUnit() {
        return radiusUnit;
    }

    public void setRadiusUnit(RadiusUnit radiusUnit) {
        this.radiusUnit = radiusUnit;
    }

    @Override
    public String toString() {
        return "Radius (" + distance + " ( in " + radiusUnit + ")";
    }

}
