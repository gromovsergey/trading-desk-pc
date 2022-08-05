package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.channel.Channel;
import com.foros.rs.client.model.advertising.channel.ChannelSelector;

public class AdvertisingChannelService extends EntityServiceSupport<Channel, ChannelSelector> {

    public AdvertisingChannelService(RsClient rsClient) {
        super(rsClient, "/channels/advertising");
    }
}