package com.foros.action.reporting.conversions.treeFilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeService;

import java.util.List;

import javax.ejb.EJB;

public class CreativesTreeFilterAction extends ConversrionsReportAbstractTreeFilterAction {

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return campaignCreativeService.searchCreatives(ownerId);
    }

    @Override
    public String getParameterName() {
        return "creativeIds";
    }

    @Override
    protected int getLevel() {
        return 3;
    }

}
