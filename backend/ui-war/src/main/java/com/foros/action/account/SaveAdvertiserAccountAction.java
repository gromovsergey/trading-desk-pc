package com.foros.action.account;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.foros.model.account.AdvertiserAccount;

public class SaveAdvertiserAccountAction extends SaveAdvertiserAccountActionBase {

    public SaveAdvertiserAccountAction() {
        account = new AdvertiserAccount();
    }

    @Validations(
            conversionErrorFields = {
                    @ConversionErrorFieldValidator(fieldName = "selfServiceCommissionPercent", key = "errors.field.number"),
            }
    )
    public String update() {
        prepareTnsObjects();
        prepareCategories();
        prepareFlagsForSave();
        prepareContractDate(account);
        prepareFinancialSettings(account);

        accountService.updateStandaloneAdvertiserAccount(account);

        return SUCCESS;
    }

    public String create() {
        prepareTnsObjects();
        prepareCategories();
        prepareFlagsForSave();
        prepareContractDate(account);

        accountService.createStandaloneAdvertiserAccount(account);

        return SUCCESS;
    }

}
