package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.security.AccountRole;
import com.foros.session.security.AccountStatsTO;
import com.foros.util.context.ContextBase;
import com.foros.web.taglib.RestrictionTools;

import java.util.List;

public class SwitchContextPublisherAction extends SwitchContextActionBase<AccountStatsTO> {

    boolean availableCreditedImps = false;

    public SwitchContextPublisherAction() {
    }

    @Override
    public AccountRole getAccountRole() {
        return AccountRole.PUBLISHER;
    }

    @Override
    public ContextBase getContext() {
        return getContexts().getPublisherContext();
    }

    @Override
    public ContextBase getSessionContext() {
        return getSessionContexts().getPublisherContext();
    }

    @Override
    public String successPath() {
        if (RestrictionTools.isPermitted("PublisherEntity.view", getSessionContext().getAccount())) {
            return "success.site";
        } else {
            return "success.account";
        }
    }

    @Override
    @ReadOnly
    public String search() {
        //Below statement is added to handle user behaviour of using browser back button
        getSessionContext().clear();

        List<AccountStatsTO> accounts = accountService.searchPublisherAccounts(getName(), getAccountTypeId(),
                getInternalAccountId(), getCountryCode(), getAccountManagerId(), getTestOption(), getStatus().getDisplayStatuses());

        setEntities(accounts);

        for (AccountStatsTO to : accounts) {
            if (to.getCreditedImpressions() != null && to.getCreditedImpressions() > 0) {
                availableCreditedImps = true;
                break;
            }
        }

        return SUCCESS;
    }

    public boolean isAvailableCreditedImps() {
        return availableCreditedImps;
    }
}
