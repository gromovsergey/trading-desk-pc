package com.foros.action.account;

import com.foros.model.account.AccountsPayableAccountBase;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

public class SaveAccountsPayableFinanceAction extends EditSaveAccountsPayableFinanceActionBase {

    private AccountsPayableAccountBase existingAccount;

    @Validations(
        conversionErrorFields = {
            @ConversionErrorFieldValidator(fieldName = "commissionPercent", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "billingFrequencyOffset", key = "errors.field.integer"),
            @ConversionErrorFieldValidator(fieldName = "minInvoice", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "taxRatePercent", key = "errors.field.number")
        }
    )
    @Validate(validation = "AccountsPayableAccount.updateFinance", parameters = "#target.model")
    public String update() {
        accountsPayableFinanceService.updateFinance(financialSettings);

        return SUCCESS;
    }

    @Override
    public AccountsPayableAccountBase getExistingAccount() {
        if (existingAccount != null) {
            return existingAccount;
        }

        existingAccount = (AccountsPayableAccountBase) accountService.find(financialSettings.getAccountId());

        return existingAccount;
    }
}
