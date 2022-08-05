package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import javax.ejb.EJB;
import java.util.List;

public class GeneralCampaignsTreeFilterAction extends AdvertiserReportAbstractTreeFilterAction {

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
    protected OlapDetailLevel.Filter getCurrentLevel() {
        return OlapDetailLevel.Filter.Campaign;
    }
}

