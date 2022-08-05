package com.foros.action.action.treefilter;

import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;

import com.foros.model.Status;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeGroupService;

public class GroupsTreeFilterAction extends AbstractConversionLinkTreeFilterAction {
    @EJB
    private CampaignCreativeGroupService ccgService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        List<TreeFilterElementTO> groups = ccgService.searchGroups(getOwnerId());
        Iterator<TreeFilterElementTO> iter = groups.iterator();
        while (iter.hasNext()) {
            TreeFilterElementTO treeFilterElementTO = iter.next();
            treeFilterElementTO.setHasChildren(false);
            if (treeFilterElementTO.getStatus() == Status.DELETED) {
                iter.remove();
            }
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
