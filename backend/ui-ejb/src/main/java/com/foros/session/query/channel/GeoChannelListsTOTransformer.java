package com.foros.session.query.channel;

import com.foros.model.Status;
import com.foros.model.channel.GeoType;
import com.foros.session.channel.geo.GeoChannelTO;
import com.foros.session.query.AbstractEntityTransformer;

import java.util.Map;

public class GeoChannelListsTOTransformer extends AbstractEntityTransformer<GeoChannelTO> {

    @Override
    protected GeoChannelTO transform(Map<String, Object> values) {
         GeoChannelTO result = new GeoChannelTO(
                 (Long) values.get("id"),
                 (String) values.get("name"),
                 Status.valueOf((char) values.get("status")),
                 (GeoType) values.get("geoType"),
                 (String) values.get("countryCode"),
                 (String) values.get("parent"),
                 (GeoType) values.get("parentGeoType")
        );

        return result;
    }
}
