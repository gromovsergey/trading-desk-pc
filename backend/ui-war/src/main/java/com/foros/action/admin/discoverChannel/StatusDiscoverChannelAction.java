package com.foros.action.admin.discoverChannel;

import com.foros.action.channel.StatusChannelActionSupport;
import com.foros.session.channel.service.ChannelService;
import com.foros.session.channel.service.DiscoverChannelService;

import javax.ejb.EJB;

public class StatusDiscoverChannelAction extends StatusChannelActionSupport {

    @EJB
    private DiscoverChannelService discoverChannelService;

    @Override
    protected ChannelService channelService() {
        return discoverChannelService;
    }

    @Override
    public String delete() {
        super.delete();
        return SUCCESS;
    }
}
