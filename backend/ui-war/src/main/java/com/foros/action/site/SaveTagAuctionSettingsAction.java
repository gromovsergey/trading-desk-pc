package com.foros.action.site;

import com.foros.model.site.TagAuctionSettings;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

public class SaveTagAuctionSettingsAction extends TagAuctionSettingsActionBase {

    public SaveTagAuctionSettingsAction() {
        auctionSettings = new TagAuctionSettings();
    }

    @Validations(
            conversionErrorFields = {
                    @ConversionErrorFieldValidator(fieldName = "maxEcpmShare", key = "errors.field.number"),
                    @ConversionErrorFieldValidator(fieldName = "propProbabilityShare", key = "errors.field.number"),
                    @ConversionErrorFieldValidator(fieldName = "randomShare", key = "errors.field.number")
            }
    )
    public String save() {
        auctionSettings.setId(getId());
        auctionSettingsService.update(auctionSettings);
        return SUCCESS;
    }
}
