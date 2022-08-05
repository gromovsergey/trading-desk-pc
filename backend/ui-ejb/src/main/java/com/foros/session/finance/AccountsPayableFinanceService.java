package com.foros.session.finance;

import com.foros.model.account.AccountsPayableFinancialSettings;

import java.util.Date;
import javax.ejb.Local;

@Local
public interface AccountsPayableFinanceService {

    void updateFinance(AccountsPayableFinancialSettings financialSettings);

    AccountsPayableFinancialSettings getFinancialSettings(long accountId);

    Date getBillingJobNextStartDate();
}
