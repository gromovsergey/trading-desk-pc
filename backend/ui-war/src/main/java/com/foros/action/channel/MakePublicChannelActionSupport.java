package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.model.channel.Channel;
import com.foros.session.channel.service.AdvertisingChannelSupport;

import com.opensymphony.xwork2.ModelDriven;

public abstract class MakePublicChannelActionSupport<T extends Channel> extends BaseActionSupport implements ModelDriven<T> {
    protected T model;

    public MakePublicChannelActionSupport(T channel) {
        model = channel;
    }

    public String makePublic() {
        channelService().makePublic(model.getId(), model.getVersion());
        return SUCCESS;
    }

    public String makePrivate() {
        channelService().makePrivate(model.getId(), model.getVersion());
        return SUCCESS;
    }

    protected abstract AdvertisingChannelSupport<T> channelService();

    @Override
    public T getModel() {
        return model;
    }
}
