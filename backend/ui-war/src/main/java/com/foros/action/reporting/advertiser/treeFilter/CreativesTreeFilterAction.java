package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import javax.ejb.EJB;
import java.util.List;

public class CreativesTreeFilterAction extends AdvertiserReportAbstractTreeFilterAction {

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return campaignCreativeService.searchCreatives(ownerId);
    }

    @Override
    public String getParameterName() {
        return "campaignCreativeIds";
    }

    @Override
    protected OlapDetailLevel.Filter getCurrentLevel() {
        return OlapDetailLevel.Filter.Creative;
    }
}
