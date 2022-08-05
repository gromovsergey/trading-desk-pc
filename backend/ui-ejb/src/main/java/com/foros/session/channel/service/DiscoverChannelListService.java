package com.foros.session.channel.service;

import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;

import java.util.Collection;

import javax.ejb.Local;

@Local
public interface DiscoverChannelListService extends ChannelService<DiscoverChannelList> {
    @Override
    Long create(DiscoverChannelList channel);

    Long create(DiscoverChannelList channel, Collection<DiscoverChannel> channelsToLink);

    @Override
    Long update(DiscoverChannelList channel);

    Long update(DiscoverChannelList channel, Collection<DiscoverChannel> channelsToLink);

    @Override
    DiscoverChannelList find(Long channelId);

    void activate(Long discoverChannelListId, Long[] channelIds);

    void inactivate(Long discoverChannelListId, Long[] channelIds);

    void updateLinkedChannel(DiscoverChannel dc);

    void delete(Long discoverChannelListId, Long[] channelIds);

    Long link(Long discoverChannelListId, Collection<DiscoverChannel> channelsToLink);

    Long link(Long discoverChannelListId, DiscoverChannel channel, String singleBaseKeyword);

    void unlink(Long childId);

}
