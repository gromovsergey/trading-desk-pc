package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.channel.Channel;
import com.foros.model.channel.GenericChannel;

public class ChannelLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        Channel channel = new GenericChannel();
        channel.setId(id);
        return channel;
    }

}