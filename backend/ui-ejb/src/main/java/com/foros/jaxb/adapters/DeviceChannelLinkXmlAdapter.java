package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.channel.DeviceChannel;

public class DeviceChannelLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        DeviceChannel deviceChannel = new DeviceChannel();
        deviceChannel.setId(id);
        return deviceChannel;
    }

}
