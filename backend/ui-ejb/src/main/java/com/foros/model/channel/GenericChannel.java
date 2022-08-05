package com.foros.model.channel;

public class GenericChannel extends Channel {
    public GenericChannel() {
    }

    public GenericChannel(Long id) {
        setId(id);
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return null;
    }

    @Override
    public String getChannelType() {
        return null;
    }

}
