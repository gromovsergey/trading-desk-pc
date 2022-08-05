package com.foros.session.channel.service;

import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelRate;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.security.ActionType;
import com.foros.session.security.AuditService;
import com.foros.util.EntityUtils;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

public abstract class CmpChannelHelper {

    abstract protected EntityManager getEM();

    abstract protected AuditService getAuditService();

    public void submitToCmp(Channel channel) {
        Channel existingChannel = findInternal(channel.getId());

        EntityUtils.checkEntityVersion(existingChannel, channel.getVersion());

        ChannelRate channelRate = channel.getChannelRate();

        getAuditService().audit(existingChannel, ActionType.UPDATE);

        channelRate.setCurrency(existingChannel.getAccount().getCurrency());
        channelRate.setEffectiveDate(new Date(System.currentTimeMillis()));
        channelRate.setChannel(existingChannel);
        getEM().persist(channelRate);

        existingChannel.setChannelRate(channelRate);
        existingChannel.setVisibility(ChannelVisibility.CMP);
    }

    private Channel findInternal(Long id) {
        Channel channel = getEM().find(Channel.class, id);
        if (channel == null) {
            throw new EntityNotFoundException("Channel with id=" + id + " not found");
        }
        return channel;
    }
}
