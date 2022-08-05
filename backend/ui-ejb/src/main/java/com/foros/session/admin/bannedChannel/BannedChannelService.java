package com.foros.session.admin.bannedChannel;

import javax.ejb.Local;

import com.foros.model.channel.BannedChannel;

@Local
public interface BannedChannelService {

    void update(BannedChannel channel);

    BannedChannel getNoAdvBannedChannel();

    BannedChannel getNoTrackBannedChannel();

    BannedChannel findById(Long channelId);
}
