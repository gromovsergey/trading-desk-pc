package com.foros.action.campaign.creative;

import com.foros.action.BaseActionSupport;
import com.foros.model.campaign.CampaignCreative;
import com.foros.session.campaign.CampaignCreativeService;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public abstract class CampaignCreativeActionSupport extends BaseActionSupport implements ModelDriven<CampaignCreative> {

    @EJB
    protected CampaignCreativeService campaignCreativeService;

    protected CampaignCreative campaignCreative;

    @Override
    public CampaignCreative getModel() {
        return campaignCreative;
    }

    public abstract boolean canUpdateWeight();
}
