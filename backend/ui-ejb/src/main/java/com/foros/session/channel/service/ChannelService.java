package com.foros.session.channel.service;

import com.foros.model.channel.Channel;
import com.foros.service.ByIdLocatorService;

public interface ChannelService<C extends Channel> extends ByIdLocatorService<C>, CategoryOwnedChannelService {
    Long create(C channel);

    Long update(C channel);

    Long copy(Long channelId);

    C find(Long channelId);

    C view(Long channelId);

    // change status group
    void delete(Long channelId);

    void undelete(Long channelId);

    void activate(Long id);

    void inactivate(Long id);
}
