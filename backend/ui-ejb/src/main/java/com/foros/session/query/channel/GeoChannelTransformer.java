package com.foros.session.query.channel;

import com.foros.jaxb.adapters.EntityLink;
import com.foros.model.Status;
import com.foros.model.channel.ApiGeoChannelTO;
import com.foros.model.channel.Coordinates;
import com.foros.model.channel.GeoType;
import com.foros.model.channel.Radius;
import com.foros.model.channel.RadiusUnit;
import com.foros.session.query.AbstractEntityTransformer;

import java.math.BigDecimal;
import java.util.Map;

public class GeoChannelTransformer extends AbstractEntityTransformer<ApiGeoChannelTO> {

    @Override
    protected ApiGeoChannelTO transform(Map<String, Object> values) {
        Long id = (Long) values.get("id");
        String name = (String) values.get("name");
        ApiGeoChannelTO geo = new ApiGeoChannelTO(id, name);
        geo.setStatus(Status.valueOf((char) values.get("status")));
        geo.setGeoType((GeoType) values.get("geoType"));
        geo.setCountry((String) values.get("countryCode"));
        geo.setParentChannel(new EntityLink((Long) values.get("parentChannelId")));
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude((BigDecimal) values.get("latitude"));
        coordinates.setLongitude((BigDecimal) values.get("longitude"));
        geo.setCoordinates(coordinates);
        Radius radius = new Radius();
        radius.setDistance((BigDecimal) values.get("distance"));
        radius.setRadiusUnit((RadiusUnit) values.get("radiusUnit"));
        geo.setRadius(radius);
        return geo;
    }
}
