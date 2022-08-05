package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.channel.triggerQA.TriggerQASelector;
import com.foros.rs.client.model.triggerQA.QATrigger;

public class TriggerQAService extends EntityServiceSupport<QATrigger, TriggerQASelector> {

    public TriggerQAService(RsClient rsClient) {
        super(rsClient, "/channels/triggerQA");
    }
}