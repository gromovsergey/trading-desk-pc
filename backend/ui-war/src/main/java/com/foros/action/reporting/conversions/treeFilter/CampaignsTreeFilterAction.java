package com.foros.action.reporting.conversions.treeFilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignService;

import java.util.List;

import javax.ejb.EJB;

public class CampaignsTreeFilterAction extends ConversrionsReportAbstractTreeFilterAction {

    @EJB
    private CampaignService campaignService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return campaignService.searchCampaigns(ownerId, null, true);
    }

    @Override
    public String getParameterName() {
        return "campaignIds";
    }

    @Override
    protected int getLevel() {
        return 1;
    }

}
