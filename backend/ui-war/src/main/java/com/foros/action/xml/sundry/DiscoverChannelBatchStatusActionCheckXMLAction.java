package com.foros.action.xml.sundry;

import com.foros.session.channel.service.DiscoverChannelService;

import javax.ejb.EJB;

public class DiscoverChannelBatchStatusActionCheckXMLAction extends BatchActionCheckXMLActionBase{
    @EJB
    private DiscoverChannelService discoverChannelService;

    @Override
    protected boolean isBatchActionPossible(String action) {
        return discoverChannelService.isBatchActionPossible(getIdsAsList(), action);
    }
}
