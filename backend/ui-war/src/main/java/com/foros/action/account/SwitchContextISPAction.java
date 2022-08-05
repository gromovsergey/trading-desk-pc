package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.security.AccountRole;
import com.foros.session.security.AccountStatsTO;
import com.foros.util.context.ContextBase;

import java.util.List;

public class SwitchContextISPAction extends SwitchContextActionBase<AccountStatsTO> {

    public SwitchContextISPAction() {
    }

    @Override
    public AccountRole getAccountRole() {
        return AccountRole.ISP;
    }

    @Override
    public ContextBase getContext() {
        return getContexts().getIspContext();
    }

    @Override
    public ContextBase getSessionContext() {
        return getSessionContexts().getIspContext();
    }

    @Override
    public String successPath() {
        // For internal users some reports are always visible
        return "success.report";
    }

    @Override
    @ReadOnly
    public String search() {
        //Below statement is added to handle user behaviour of using browser back button
        getSessionContext().clear();

        List<AccountStatsTO> accounts = accountService.searchISPAccounts(getName(), getAccountTypeId(),
                getInternalAccountId(), getCountryCode(), getAccountManagerId(), getTestOption(), getStatus().getDisplayStatuses());

        setEntities(accounts);

        return SUCCESS;
    }
}
