package com.foros.action.channel.audience;

import com.foros.action.channel.MakePublicChannelActionSupport;
import com.foros.model.channel.AudienceChannel;
import com.foros.session.channel.service.AdvertisingChannelSupport;
import com.foros.session.channel.service.AudienceChannelService;

import javax.ejb.EJB;

public class MakePublicAudienceChannelAction  extends MakePublicChannelActionSupport<AudienceChannel> {

    @EJB
    private AudienceChannelService audienceChannelService;

    public MakePublicAudienceChannelAction() {
        super(new AudienceChannel());
    }

    @Override
    protected AdvertisingChannelSupport<AudienceChannel> channelService() {
        return audienceChannelService;
    }
}
