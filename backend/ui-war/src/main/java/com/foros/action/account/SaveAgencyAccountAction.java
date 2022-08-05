package com.foros.action.account;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.foros.model.account.AgencyAccount;


public class SaveAgencyAccountAction extends SaveAccountActionBase<AgencyAccount> {

    public SaveAgencyAccountAction() {
        account = new AgencyAccount();
    }

    @Validations(
            conversionErrorFields = {
                    @ConversionErrorFieldValidator(fieldName = "selfServiceCommissionPercent", key = "errors.field.number"),
            }
    )
    public String update() {
        prepareFlagsForSave();
        prepareFinancialSettings(account);
        prepareContractDate(account);

        accountService.updateAgencyAccount(account);

        return SUCCESS;
    }

    public String create() {
        prepareFlagsForSave();
        prepareInitialFinancialSettings(account);
        prepareContractDate(account);

        accountService.createAgencyAccount(account);

        return SUCCESS;
    }
}
