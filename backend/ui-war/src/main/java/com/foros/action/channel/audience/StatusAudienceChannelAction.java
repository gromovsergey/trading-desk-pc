package com.foros.action.channel.audience;

import com.foros.action.channel.StatusChannelActionSupport;
import com.foros.session.channel.service.AudienceChannelService;
import com.foros.session.channel.service.ChannelService;

import javax.ejb.EJB;

public class StatusAudienceChannelAction  extends StatusChannelActionSupport {

    @EJB
    private AudienceChannelService audienceChannelService;

    @Override
    protected ChannelService channelService() {
        return audienceChannelService;
    }
}
