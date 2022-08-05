package com.foros.session.channel.service;

import com.foros.model.channel.DiscoverChannel;
import com.foros.session.EntityTO;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.security.ManagerAccountTO;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

@Local
public interface DiscoverChannelService extends ChannelService<DiscoverChannel> {

    @Override
    Long create(DiscoverChannel channel);

    @Override
    Long update(DiscoverChannel channel);

    @Override
    DiscoverChannel find(Long channelId);

    DiscoverChannel findWithTriggers(Long channelId);

    List<ManagerAccountTO> getAvailableAccounts();

    /**
     * List of ISO 639 language codes sorted in alphabetical order.
     * @return sorted language list
     */
    List<String> getAvailableLanguages();

    boolean isBatchActionPossible(Collection<Long> ids, String action);
    
    List<EntityTO> findAvailableChannelLists(DiscoverChannel channel, String name, int maxResults);

    OperationsResult perform(Operations<DiscoverChannel> channelOperations) throws Exception;

    /**
     * To call from DiscoverListService ONLY
     * 
     * @param channel
     *            linked channel
     */
    void createChild(DiscoverChannel channel);

    /**
     * To call from DiscoverListService ONLY
     * 
     * @param channel
     *            linked channel
     */
    void updateChild(DiscoverChannel channel);
}
