package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import javax.ejb.EJB;
import java.util.List;

public class GroupsTreeFilterAction extends AdvertiserReportAbstractTreeFilterAction {

    @EJB
    private CampaignCreativeGroupService ccgService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return ccgService.searchGroups(ownerId);
    }

    @Override
    public String getParameterName() {
        return "ccgIds";
    }

    @Override
    protected OlapDetailLevel.Filter getCurrentLevel() {
        return OlapDetailLevel.Filter.Group;
    }
}