package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.security.AccountRole;
import com.foros.session.security.AccountStatsTO;
import com.foros.util.context.ContextBase;
import com.foros.web.taglib.RestrictionTools;

import java.util.List;

public class SwitchContextCMPAction extends SwitchContextActionBase<AccountStatsTO> {

    public AccountRole getAccountRole() {
        return AccountRole.CMP;
    }

    @Override
    public ContextBase getContext() {
        return getContexts().getCmpContext();
    }

    @Override
    public ContextBase getSessionContext() {
        return getSessionContexts().getCmpContext();
    }

    @Override
    public String successPath() {
        if (RestrictionTools.isPermitted("AdvertisingChannel.view", getSessionContext().getAccount())) {
            return "success.channel";
        } else {
            return "success.account";
        }
    }

    @ReadOnly
    public String search() {
        //Below statement is added to handle user behaviour of using browser back button
        getSessionContext().clear();

        List<AccountStatsTO> accounts = accountService.searchCMPAccounts(getName(), getAccountTypeId(),
                getInternalAccountId(), getCountryCode(), getAccountManagerId(), getStatus().getDisplayStatuses());

        setEntities(accounts);

        return SUCCESS;
    }
}