package com.foros.action.channel.behavioral;

import com.foros.action.channel.MakePublicChannelActionSupport;
import com.foros.model.channel.BehavioralChannel;
import com.foros.session.channel.service.AdvertisingChannelSupport;
import com.foros.session.channel.service.BehavioralChannelService;

import javax.ejb.EJB;

public class MakePublicBehavioralChannelAction extends MakePublicChannelActionSupport<BehavioralChannel> {

    @EJB
    private BehavioralChannelService behavioralChannelService;

    public MakePublicBehavioralChannelAction() {
        super(new BehavioralChannel());
    }

    @Override
    protected AdvertisingChannelSupport<BehavioralChannel> channelService() {
        return behavioralChannelService;
    }
}
