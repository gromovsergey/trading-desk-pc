package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.context.RequestContexts;

public class EditAgencyAdvertiserAccountAction extends EditAdvertiserAccountActionBase implements AgencySelfIdAware {

    private AgencyAccount agencyAccount;

    public EditAgencyAdvertiserAccountAction() {
        account = new AdvertiserAccount();
    }

    @Override
    public AdvertiserAccount getExistingAccount() {
        if (account.getId() != null) {
            return super.getExistingAccount();
        }

        if (agencyAccount == null) {
            agencyAccount = accountService.viewAgencyAccount(account.getAgency().getId());
        }

        account.setAgency(agencyAccount);
        account.setCountry(agencyAccount.getCountry());

        return account;
    }

    @ReadOnly
    @Restrict(restriction = "AgencyAdvertiserAccount.update", parameters = "find('AdvertiserAccount',#target.model.id)")
    public String edit() {
        account = accountService.viewAdvertiserInAgencyAccount(account.getId());
        prepareContractDateForEdit();
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="AgencyAdvertiserAccount.create", parameters="find('Account',#target.model.agency.id)")
    public String create() {
        getExistingAccount();
        return SUCCESS;
    }

    public boolean isAgencyAdvertiserAccountRequest() {
        return true;
    }

    @Override
    public void setAgencyId(Long agencyId) {
         account.setAgency(new AgencyAccount(agencyId));
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (account.getId() != null) {
            super.switchContext(contexts);
        } else {
            contexts.getContext(account.getRole()).switchTo(account.getAgency().getId());
        }
    }
}
