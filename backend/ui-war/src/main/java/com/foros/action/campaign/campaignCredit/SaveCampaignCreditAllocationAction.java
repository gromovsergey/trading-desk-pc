package com.foros.action.campaign.campaignCredit;

import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
        requiredFields = {
                @RequiredFieldValidator(fieldName = "advertiserId", key = "errors.field.required")
        },
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "allocatedAmount", key = "errors.field.number")
        }
)
public class SaveCampaignCreditAllocationAction extends EditSaveCampaignCreditAllocationActionBase {

    public SaveCampaignCreditAllocationAction() {
        campaignCreditAllocation = new CampaignCreditAllocation();
        campaignCreditAllocation.setCampaignCredit(new CampaignCredit());
    }

    @Validate(validation = "CampaignCreditAllocation.create", parameters = "#target.model")
    public String create() {
        campaignCreditAllocationService.create(campaignCreditAllocation);
        return SUCCESS;
    }
    @Validate(validation = "CampaignCreditAllocation.update", parameters = "#target.model")
    public String update() {
        campaignCreditAllocationService.update(campaignCreditAllocation);
        return SUCCESS;
    }
}
