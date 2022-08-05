package com.foros.action.xml.sundry;

import com.foros.session.campaign.CampaignCreativeGroupService;

import javax.ejb.EJB;

public class CCGBatchStatusActionCheckXMLAction extends BatchActionCheckXMLActionBase {
    @EJB
    private CampaignCreativeGroupService ccgService;

    @Override
    protected boolean isBatchActionPossible(String action) {
        return ccgService.isBatchActionPossible(getIdsAsList(), action);
    }
}
