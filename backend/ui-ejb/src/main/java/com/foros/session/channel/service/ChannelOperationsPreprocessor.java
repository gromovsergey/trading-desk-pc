package com.foros.session.channel.service;

import com.foros.model.channel.Channel;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;

import javax.persistence.EntityManager;

public abstract class ChannelOperationsPreprocessor {

    public void preProcess(Operations<? extends Channel> channelOperations) {
        for (Operation<? extends Channel> operation : channelOperations) {
            Channel channel = operation.getEntity();
            if (operation.getOperationType() == OperationType.UPDATE && channel.getId() != null) {
                // copy existing account & country to be able resolve name constraint violation later
                Channel existing = getEm().find(Channel.class, channel.getId());
                if (existing != null) {
                    channel.setAccount(existing.getAccount());
                    if (!channel.isChanged("country")) {
                        channel.setCountry(existing.getCountry());
                        channel.unregisterChange("country");
                    }
                }
            }
        }
    }

    protected abstract EntityManager getEm();

}
