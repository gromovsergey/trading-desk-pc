package com.foros.session.channel.geo;

import com.foros.model.Status;
import com.foros.model.channel.GeoType;
import com.foros.session.NamedTO;

public class GeoChannelTO extends NamedTO {
    private Status status;
    private String country;
    private String state;
    private String city;

    public GeoChannelTO(Long id, String name, Status status, GeoType geoType, String countryCode, String parent, GeoType parentGeoType) {
        super(id, name);
        this.status = status;
        this.country = countryCode;

        switch (geoType) {
            case STATE:
                this.state = name;
                break;
            case CITY:
                this.city = name;
                break;
        }

        if (parentGeoType == GeoType.STATE) {
            this.state = parent;
        }
    }

    public Status getStatus() {
        return status;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }
}
