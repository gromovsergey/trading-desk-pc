package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.channel.GeoChannel;

public class GeoChannelLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        GeoChannel geoChannel = new GeoChannel();
        geoChannel.setId(id);
        return geoChannel;
    }
}
