package com.foros.session.channel.service;

import com.foros.model.channel.AudienceChannel;

import javax.ejb.Local;


@Local
public interface AudienceChannelService extends ChannelService<AudienceChannel>, AdvertisingChannelSupport<AudienceChannel> {

    @Override
    Long create(AudienceChannel channel);

    @Override
    Long update(AudienceChannel channel);

    @Override
    AudienceChannel find(Long channelId);

    @Override
    AudienceChannel view(Long channelId);

    Long createBulk(AudienceChannel channel);

    Long updateBulk(AudienceChannel channel);

}
