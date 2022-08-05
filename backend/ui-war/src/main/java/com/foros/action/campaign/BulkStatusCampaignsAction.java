package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.session.campaign.CampaignService;

import java.util.Collection;

import javax.ejb.EJB;

public class BulkStatusCampaignsAction extends BaseActionSupport {
    @EJB
    private CampaignService campaignService;

    private Collection<Long> selectedCampaignIds;

    public String activateCampaigns() {
        campaignService.activateAll(selectedCampaignIds);
        return SUCCESS;
    }

    public String deactivateCampaigns() {
        campaignService.inactivateAll(selectedCampaignIds);
        return SUCCESS;
    }

    public String deleteCampaigns() {
        campaignService.deleteAll(selectedCampaignIds);
        return SUCCESS;
    }

    public Collection<Long> getSelectedCampaignIds() {
        return selectedCampaignIds;
    }

    public void setSelectedCampaignIds(Collection<Long> selectedCampaignIds) {
        this.selectedCampaignIds = selectedCampaignIds;
    }
}
