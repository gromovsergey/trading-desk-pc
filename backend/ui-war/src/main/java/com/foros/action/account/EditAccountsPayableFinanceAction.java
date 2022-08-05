package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.AccountsPayableAccountBase;

public class EditAccountsPayableFinanceAction extends EditSaveAccountsPayableFinanceActionBase {

    @ReadOnly
    public String edit() {
        // accountId is not set for myAccount interface
        if (financialSettings.getAccountId() == null) {
            financialSettings.setAccountId(accountService.getMyAccount().getId());
        }

        financialSettings = accountsPayableFinanceService.getFinancialSettings(financialSettings.getAccountId());

        financialSettings.setBankName(null);
        financialSettings.setBankSortCode(null);
        financialSettings.setBankBranchName(null);
        financialSettings.setBankAccountName(null);
        financialSettings.setBankAccountIban(null);
        financialSettings.setBankAccountNumber(null);
        financialSettings.setBankBicCode(null);
        financialSettings.setBankCurrency(null);

        return SUCCESS;
    }

    @Override
    public AccountsPayableAccountBase getExistingAccount() {
        return financialSettings.getAccount();
    }
}
