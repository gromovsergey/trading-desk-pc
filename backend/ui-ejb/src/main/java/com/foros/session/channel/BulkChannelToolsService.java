package com.foros.session.channel;

import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.session.TooManyRowsException;
import com.foros.session.channel.service.AdvertisingChannelType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

@Local
public interface BulkChannelToolsService {

    Collection<? extends Channel> findForExport(Long accountId, AdvertisingChannelType channelType, Collection<Long> ids, int maxResultSize)
            throws TooManyRowsException;

    ValidationResultTO validateAll(AdvertisingChannelType channelType, List<? extends Channel> channels);

    Collection<Channel> getValidatedResults(String validationResultId);

    void createOrUpdateAll(AdvertisingChannelType channelType, String validationResultId);

    void setTriggers(Map<Long, BehavioralChannel> channels);

}
