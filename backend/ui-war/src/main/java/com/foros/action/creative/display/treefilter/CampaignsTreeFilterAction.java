package com.foros.action.creative.display.treefilter;

import com.foros.model.Status;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignService;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;

import java.util.List;

import javax.ejb.EJB;

public class CampaignsTreeFilterAction extends AbstractCreativeLinkTreeFilterAction {

    private boolean showDisplay;

    @EJB
    private CampaignService campaignService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        List<TreeFilterElementTO> campaigns = campaignService.searchCampaigns(getOwnerId(), isShowDisplay(), false);
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

    public boolean isShowDisplay() {
        return showDisplay;
    }

    public void setShowDisplay(boolean showDisplay) {
        this.showDisplay = showDisplay;
    }
}
