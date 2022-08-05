package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.discover.DiscoverChannel;
import com.foros.rs.client.model.discover.DiscoverChannelSelector;

public class DiscoverChannelService extends EntityServiceSupport<DiscoverChannel, DiscoverChannelSelector> {

    public DiscoverChannelService(RsClient rsClient) {
        super(rsClient, "/channels/discover");
    }
}
