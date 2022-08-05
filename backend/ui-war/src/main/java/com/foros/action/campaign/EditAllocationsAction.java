package com.foros.action.campaign;

import com.foros.framework.ReadOnly;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.restriction.annotation.Restrict;

public class EditAllocationsAction extends EditSaveAllocationsActionBase {
    @ReadOnly
    @Restrict(restriction="CampaignAllocation.createUpdate", parameters="find('Campaign',#target.id)")
    public String edit() {
        campaign = campaignService.find(id);
        campaignAllocations = campaignAllocationService.getCampaignAllocations(id);
        return SUCCESS;
    }

    @ReadOnly
    public String addAllocation() {
        campaignAllocations.add(new CampaignAllocation());
        currentAllocationIndex = campaignAllocations.size() - 1;
        return SUCCESS;
    }

    @ReadOnly
    public String deleteAllocation() {
        campaignAllocations.remove(currentAllocationIndex.intValue());
        return SUCCESS;
    }

    @ReadOnly
    public String changeOpportunity() {
        return SUCCESS;
    }
}
