package com.foros.action.campaign.campaignCredit;

import com.foros.model.account.GenericAccount;
import com.foros.model.campaign.CampaignCredit;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "amount", key = "errors.field.number")
        }
)
public class SaveCampaignCreditAction extends EditSaveCampaignCreditActionBase {

    public SaveCampaignCreditAction() {
        campaignCredit = new CampaignCredit();
        campaignCredit.setAccount(new GenericAccount());
    }

    @Validate(validation = "CampaignCredit.create", parameters = "#target.prepareModel()")
    public String create() {
        campaignCreditService.create(campaignCredit);
        return SUCCESS;
    }

    @Validate(validation = "CampaignCredit.update", parameters = "#target.prepareModel()")
    public String update() {
        campaignCreditService.update(campaignCredit);
        return SUCCESS;
    }

    public CampaignCredit prepareModel() {
        return campaignCredit;
    }
}
