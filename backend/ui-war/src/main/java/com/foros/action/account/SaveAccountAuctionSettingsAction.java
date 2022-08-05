package com.foros.action.account;

import com.foros.model.account.AccountAuctionSettings;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "maxEcpmShare", key = "errors.field.number"),
                @ConversionErrorFieldValidator(fieldName = "propProbabilityShare", key = "errors.field.number"),
                @ConversionErrorFieldValidator(fieldName = "randomShare", key = "errors.field.number"),
                @ConversionErrorFieldValidator(fieldName = "maxRandomCpm", key = "errors.field.number")
        }
)
public class SaveAccountAuctionSettingsAction extends EditAccountAuctionSettingsActionBase {

    public SaveAccountAuctionSettingsAction() {
        auctionSettings = new AccountAuctionSettings();
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
