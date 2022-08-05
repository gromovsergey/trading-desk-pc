package com.foros.session.query.channel;

import java.util.Set;

public interface DiscoverChannelQuery extends ChannelQuery<DiscoverChannelQuery> {

    DiscoverChannelQuery restrictByInternalAccountIds(Set<Long> accessAccountIds);
    
}
