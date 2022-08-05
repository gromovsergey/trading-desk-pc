package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.device.DeviceChannel;
import com.foros.rs.client.model.device.DeviceChannelSelector;

public class DeviceChannelService extends ReadonlyServiceSupport<DeviceChannelSelector, DeviceChannel> {

    public DeviceChannelService(RsClient rsClient) {
        super(rsClient, "/channels/device");
    }
}
