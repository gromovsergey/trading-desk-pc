package com.foros.model.security;

import com.foros.model.channel.DeviceChannel;

import java.util.Set;

public interface DeviceTargetingEntity {

    public Set<DeviceChannel> getDeviceChannels();

}
