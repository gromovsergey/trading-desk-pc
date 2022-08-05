package com.foros.session.query.channel;

import java.util.Set;


public interface DiscoverChannelListsQuery extends ChannelQuery<DiscoverChannelListsQuery>{

    DiscoverChannelListsQuery orderByName();

    DiscoverChannelListsQuery language(String language);
    
    DiscoverChannelListsQuery restrictByAccountIds(Set<Long> accessAccountIds);
}
