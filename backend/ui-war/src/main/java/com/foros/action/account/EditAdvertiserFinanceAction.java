package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.restriction.annotation.Restrict;

@Deprecated
/**
 * @deprecated OUI-28825
 */
public class EditAdvertiserFinanceAction extends EditSaveAdvertiserFinanceActionBase {

    @ReadOnly
    @Restrict(restriction = "AdvertisingAccount.updateFinance", parameters = "find('AdvertisingAccountBase', #target.prepareAccountId())")
    public String edit() {
        financialSettings = advertisingFinanceService.getFinancialSettings(financialSettings.getAccountId());

        return SUCCESS;
    }

    @Override
    public AdvertisingAccountBase getExistingAccount() {
        return financialSettings.getAccount();
    }

    public Long prepareAccountId() {
        // accountId is not set for myAccount interface
        if (financialSettings.getAccountId() == null) {
            financialSettings.setAccountId(accountService.getMyAccount().getId());
        }
        return financialSettings.getAccountId();
    }
}
