package com.foros.action.creative.display.treefilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeGroupService;

import java.util.List;

import javax.ejb.EJB;

public class GroupsTreeFilterAction extends AbstractCreativeLinkTreeFilterAction {

    @EJB
    private CampaignCreativeGroupService ccgService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        List<TreeFilterElementTO> groups = ccgService.searchGroups(getOwnerId());
        for (TreeFilterElementTO treeFilterElementTO : groups) {
            treeFilterElementTO.setHasChildren(false);
        }
        return groups;
    }

    @Override
    public String getParameterName() {
        return "groupIds";
    }

    @Override
    public boolean isRoot() {
        return false;
    }


}
