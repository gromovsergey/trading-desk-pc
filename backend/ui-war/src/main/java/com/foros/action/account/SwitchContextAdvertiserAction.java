package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.Account;
import com.foros.security.AccountRole;
import com.foros.session.NamedTO;
import com.foros.session.security.AccountStatsTO;
import com.foros.util.context.AdvertiserContext;
import com.foros.util.context.ContextBase;

import com.foros.web.taglib.RestrictionTools;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class SwitchContextAdvertiserAction extends SwitchContextActionBase<AccountStatsTO> {

    private boolean availableCreditUsed = false;

    public SwitchContextAdvertiserAction() {
    }

    @Override
    public AccountRole getAccountRole() {
        return AccountRole.ADVERTISER;
    }

    @Override
    public ContextBase getContext() {
        return getContexts().getAdvertiserContext();
    }

    @Override
    public ContextBase getSessionContext() {
        return getSessionContexts().getAdvertiserContext();
    }

    @Override
    @ReadOnly
    public String search() {
        //Below statement is added to handle user behaviour of using browser back button
        getSessionContext().clear();

        List<AccountStatsTO> accounts = accountService.searchAdvertiserAccounts(getName(), getAccountTypeId(),
                getInternalAccountId(), getCountryCode(), getAccountManagerId(), getTestOption(), getStatus().getDisplayStatuses());

        setEntities(accounts);

        for(AccountStatsTO to: accounts) {
            if(to.getCreditUsed()!=null && to.getCreditUsed().compareTo(BigDecimal.ZERO)!=0) {
                availableCreditUsed = true;
                break;
            }
        }

        return SUCCESS;
    }

    @Override
    protected void populateAccountTypes() {
        super.populateAccountTypes();
        List<NamedTO> agencyAccountTypes = accountTypeService.findIndexByRole(AccountRole.AGENCY.getName());
        getAccountTypes().addAll(agencyAccountTypes);
        Collections.sort(getAccountTypes());
    }

    public String getAdvertiserId() {
        AdvertiserContext advertiserContext = (AdvertiserContext) getSessionContext();
        if (advertiserContext.isAdvertiserSet()) {
            return advertiserContext.getAdvertiserId().toString();
        }
        return null;
    }

    public String getAgencyId() {
        AdvertiserContext advertiserContext = (AdvertiserContext) getSessionContext();
        if (advertiserContext.isAgencyContext()) {
            return advertiserContext.getAccountId().toString();
        }
        return null;
    }

    @Override
    public String successPath() {
        Account account = getSessionContext().getAccount();
        if (RestrictionTools.isPermitted("AdvertiserEntity.view", account)) {
            return "success.campaign";
        } else if (RestrictionTools.isPermitted("AdvertisingChannel.view", account)) {
            return "success.channel";
        } else {
            return "success.account";
        }
    }

    public boolean isAvailableCreditUsed() {
        return availableCreditUsed;
    }
}
