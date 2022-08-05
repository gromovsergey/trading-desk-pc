package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.geo.GeoChannel;
import com.foros.rs.client.model.geo.GeoChannelSelector;

public class GeoChannelService extends ReadonlyServiceSupport<GeoChannelSelector, GeoChannel> {
    public GeoChannelService(RsClient rsClient) {
        super(rsClient, "/channels/geo");
    }
}
