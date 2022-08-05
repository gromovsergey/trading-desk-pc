package com.foros.session.channel.service;

import com.foros.model.channel.Channel;

import java.sql.Timestamp;
import java.util.Collection;

public interface AdvertisingChannelSupport<C extends Channel> {

    void makePublic(Long channelId, Timestamp version);

    void makePrivate(Long channelId, Timestamp version);

    void submitToCmp(C channel) throws Exception;

    void createOrUpdateAll(Long accountId, Collection<C> channels);
}
