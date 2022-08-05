package com.foros.session.channel.service;

import com.foros.model.channel.ExpressionChannel;

import javax.ejb.Local;

@Local
public interface ExpressionChannelService extends ChannelService<ExpressionChannel>, AdvertisingChannelSupport<ExpressionChannel> {
    @Override
    Long create(ExpressionChannel channel);

    @Override
    Long update(ExpressionChannel channel);

    @Override
    void submitToCmp(ExpressionChannel channel);

    @Override
    ExpressionChannel find(Long channelId);

    Long createBulk(ExpressionChannel channel);

    Long updateBulk(ExpressionChannel channel);

    ExpressionChannel findForUpdate(Long id);
}
