package com.foros.model.channel;

import com.foros.jaxb.adapters.EntityLink;
import com.foros.model.Status;
import com.foros.session.NamedTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "geoChannel")
@XmlType(propOrder = {
        "id",
        "name",
        "status",
        "country",
        "parentChannel",
        "geoType",
        "coordinates",
        "radius"
})
@XmlAccessorType(XmlAccessType.NONE)
public class ApiGeoChannelTO extends NamedTO {

    private Status status;

    private String country;

    private EntityLink parentChannel;

    private GeoType geoType;

    private Coordinates coordinates;

    private Radius radius;


    public ApiGeoChannelTO(Long id, String name) {
        super(id, name);
    }

    public ApiGeoChannelTO() {
    }

    @Override
    @XmlElement
    public Long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @XmlElement
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @XmlElement
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @XmlElement
    public EntityLink getParentChannel() {
        return parentChannel;
    }

    public void setParentChannel(EntityLink parentChannel) {
        this.parentChannel = parentChannel;
    }

    @XmlElement
    public GeoType getGeoType() {
        return geoType;
    }

    public void setGeoType(GeoType geoType) {
        this.geoType = geoType;
    }

    @XmlElement
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @XmlElement
    public Radius getRadius() {
        return radius;
    }

    public void setRadius(Radius radius) {
        this.radius = radius;
    }


}
