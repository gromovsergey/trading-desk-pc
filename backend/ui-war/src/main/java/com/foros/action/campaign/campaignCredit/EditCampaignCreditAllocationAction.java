package com.foros.action.campaign.campaignCredit;

import com.foros.framework.ReadOnly;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.restriction.annotation.Restrict;

public class EditCampaignCreditAllocationAction extends EditSaveCampaignCreditAllocationActionBase {

    private Long id;
    private Long campaignCreditId;

    @ReadOnly
    @Restrict(restriction="CampaignCredit.editAllocations", parameters="find('CampaignCredit',#target.campaignCreditId)")
    public String create() {
        campaignCreditAllocation = createEmptyAllocation(campaignCreditId);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="CampaignCredit.editAllocations", parameters="find('CampaignCreditAllocation',#target.id)")
    public String edit() {
        campaignCreditAllocation = campaignCreditAllocationService.find(id);
        return SUCCESS;
    }

    private CampaignCreditAllocation createEmptyAllocation(Long campaignCreditId) {
        CampaignCreditAllocation result = new CampaignCreditAllocation();
        CampaignCredit campaignCredit = campaignCreditService.find(campaignCreditId);
        result.setCampaignCredit(campaignCredit);
        return result;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCampaignCreditId(Long campaignCreditId) {
        this.campaignCreditId = campaignCreditId;
    }

    public Long getCampaignCreditId() {
        return campaignCreditId;
    }
}
