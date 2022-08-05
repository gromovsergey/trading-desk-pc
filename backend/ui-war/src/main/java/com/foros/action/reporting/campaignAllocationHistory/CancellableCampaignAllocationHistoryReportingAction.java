package com.foros.action.reporting.campaignAllocationHistory;

import com.foros.action.reporting.CancellablePageReportingAction;
import com.foros.framework.ReadOnly;
import com.foros.model.campaign.Campaign;
import com.foros.session.campaign.CampaignService;

import javax.ejb.EJB;

public class CancellableCampaignAllocationHistoryReportingAction extends CancellablePageReportingAction {

    @EJB
    private CampaignService campaignService;
    private Campaign campaign = new Campaign();

    @ReadOnly
    @Override
    public String showCancellableView() {
        campaign = campaignService.find(campaign.getId());
        return super.showCancellableView();
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaignId(Long id) {
        getCampaign().setId(id);
    }
}
