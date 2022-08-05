package com.foros.action.campaign;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.foros.model.campaign.Campaign;

import java.util.LinkedHashSet;

@Validations(
        customValidators = {
                @CustomValidator(type = "convtransform", key = "errors.field.number",
                        parameters = {@ValidationParameter(name = "fieldMask", value = "campaignAllocations\\[.+\\]\\.amount")})
        }
)
public class SaveAllocationsAction extends EditSaveAllocationsActionBase {
    public String update() {
        prepareModel();
        campaignAllocationService.updateCampaignAllocations(campaign);
        return SUCCESS;
    }

    public Campaign prepareModel() {
        campaign.setAllocations(new LinkedHashSet(campaignAllocations));
        return campaign;
    }
}
