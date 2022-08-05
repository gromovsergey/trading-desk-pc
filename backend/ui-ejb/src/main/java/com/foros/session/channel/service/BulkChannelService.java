package com.foros.session.channel.service;

import com.foros.model.channel.Channel;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;

import javax.ejb.Local;

@Local
public interface BulkChannelService {
    
    OperationsResult perform(Operations<Channel> channelOperations) throws Exception;

}
