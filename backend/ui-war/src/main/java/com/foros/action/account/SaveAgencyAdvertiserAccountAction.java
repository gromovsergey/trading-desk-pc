package com.foros.action.account;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.util.context.RequestContexts;

public class SaveAgencyAdvertiserAccountAction extends SaveAdvertiserAccountActionBase {

    private AgencyAccount agencyAccount;

    public SaveAgencyAdvertiserAccountAction() {
        account = new AdvertiserAccount();
    }

    @Override
    public AdvertiserAccount getExistingAccount() {
        if (account.getId() != null) {
            return super.getExistingAccount();
        }

        account.setAgency(getAgencyAccount(account.getAgency().getId()));
        account.setCountry(account.getAgency().getCountry());

        return account;
    }

    public String update() {

        if (isInternal() || getExistingAccount().getAgency().isSelfServiceFlag()) {
            prepareTnsObjects();
        }
        prepareCategories();
        prepareFinancialSettings(account);

        accountService.updateAdvertiser(account);

        return SUCCESS;
    }

    public String create() {
        prepareTnsObjects();
        prepareCategories();
        prepareInitialFinancialSettings(account);

        account.setAgency(getAgencyAccount(account.getAgency().getId()));
        account.setCurrency(account.getAgency().getCurrency());

        accountService.addAdvertiser(account);

        return SUCCESS;
    }

    public boolean isAgencyAdvertiserAccountRequest() {
        return true;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (account.getId() != null) {
            super.switchContext(contexts);
        } else {
            contexts.getContext(account.getRole()).switchTo(account.getAgency().getId());
        }
    }

    private AgencyAccount getAgencyAccount(Long agencyId) {
        if (agencyAccount == null) {
            agencyAccount = accountService.viewAgencyAccount(agencyId);
        }

        return agencyAccount;
    }
}
