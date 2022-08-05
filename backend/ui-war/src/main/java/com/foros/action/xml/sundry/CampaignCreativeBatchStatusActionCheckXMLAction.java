package com.foros.action.xml.sundry;

import com.foros.session.campaign.CampaignCreativeService;

import javax.ejb.EJB;

public class CampaignCreativeBatchStatusActionCheckXMLAction extends BatchActionCheckXMLActionBase {
    @EJB
    private CampaignCreativeService campaignCreativeService;

    protected boolean isBatchActionPossible(String action) {
        return campaignCreativeService.isBatchActionPossible(getIdsAsList(), action);
    }
}
