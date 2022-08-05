package com.foros.action.reporting.conversions.treeFilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeGroupService;

import java.util.List;

import javax.ejb.EJB;

public class GroupsTreeFilterAction extends ConversrionsReportAbstractTreeFilterAction {

    @EJB
    private CampaignCreativeGroupService ccgService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return ccgService.searchGroups(ownerId);
    }

    @Override
    public String getParameterName() {
        return "groupIds";
    }

    @Override
    protected int getLevel() {
        return 2;
    }

}