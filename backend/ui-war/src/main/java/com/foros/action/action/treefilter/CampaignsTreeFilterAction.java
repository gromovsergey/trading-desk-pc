package com.foros.action.action.treefilter;

import java.util.List;

import javax.ejb.EJB;

import com.foros.model.Status;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignService;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;

public class CampaignsTreeFilterAction extends AbstractConversionLinkTreeFilterAction {
    @EJB
    private CampaignService campaignService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        List<TreeFilterElementTO> campaigns = campaignService.searchCampaigns(getOwnerId(), null, false);
        CollectionUtils.filter(campaigns, new Filter<TreeFilterElementTO>() {
            @Override
            public boolean accept(TreeFilterElementTO element) {
                return element.isHasChildren() && Status.DELETED != element.getStatus();
            }
        });

        return campaigns;
    }

    @Override
    public String getParameterName() {
        return "campaignIds";
    }

    @Override
    public boolean isRoot() {
        return true;
    }
}
