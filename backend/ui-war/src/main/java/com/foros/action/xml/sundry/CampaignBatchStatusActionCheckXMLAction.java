package com.foros.action.xml.sundry;

import com.foros.session.campaign.CampaignService;

import javax.ejb.EJB;

public class CampaignBatchStatusActionCheckXMLAction extends BatchActionCheckXMLActionBase {
    @EJB
    private CampaignService campaignService;

    @Override
    protected boolean isBatchActionPossible(String action) {
        return campaignService.isBatchActionPossible(getIdsAsList(), action);
    }
}
