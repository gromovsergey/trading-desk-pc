package com.foros.action.channel.behavioral;

import com.foros.action.channel.StatusChannelActionSupport;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.session.channel.service.ChannelService;

import javax.ejb.EJB;

public class StatusBehavioralChannelAction extends StatusChannelActionSupport {

    @EJB
    private BehavioralChannelService behavioralChannelService;

    @Override
    protected ChannelService channelService() {
        return behavioralChannelService;
    }
}
