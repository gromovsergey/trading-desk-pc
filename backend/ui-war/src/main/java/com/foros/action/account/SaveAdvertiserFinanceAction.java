package com.foros.action.account;

import com.foros.model.account.AdvertisingAccountBase;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Deprecated
/**
 * @deprecated OUI-28825
 */
public class SaveAdvertiserFinanceAction extends EditSaveAdvertiserFinanceActionBase {

    private AdvertisingAccountBase existingAccount;

    @Validations(
        conversionErrorFields = {
            @ConversionErrorFieldValidator(fieldName = "commissionPercent", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "mediaHandlingFeePercent", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "billingFrequencyOffset", key = "errors.field.integer"),
            @ConversionErrorFieldValidator(fieldName = "minInvoice", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "creditLimit", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "data.prepaidAmount", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "taxRatePercent", key = "errors.field.number")
        }
    )
    @Validate(validation = "AdvertisingAccount.updateFinance", parameters = "#target.model")
    public String update() {
        financialSettings.setAccount(getExistingAccount());

        advertisingFinanceService.updateFinance(financialSettings);

        return SUCCESS;
    }

    @Override
    public AdvertisingAccountBase getExistingAccount() {
        if (existingAccount != null) {
            return existingAccount;
        }

        existingAccount = (AdvertisingAccountBase) accountService.find(financialSettings.getAccountId());

        return existingAccount;
    }
}
