package com.foros.action.reporting.isp;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.colocation.ColocationService;
import com.foros.session.security.AccountTO;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;

public class ViewISPReportingAction extends BaseActionSupport implements RequestContextsAware {

    @EJB
    AccountService accountService;

    @EJB
    private ColocationService colocationService;

    private Long accountId;
    private Collection<AccountTO> accounts;
    private Collection<EntityTO> colocations;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'ISP'")
    public String view() throws Exception {
        if (accountId == null && SecurityContext.isInternal()) {
            List<AccountTO> tos = accountService.search(AccountRole.ISP);
            accounts = EntityUtils.sortByStatus(EntityUtils.applyStatusRules(tos, null, true));
            colocations = Collections.emptyList();
        } else {
            if (accountId == null) {
                accountId = accountService.getMyAccount().getId();
            }
            colocations = EntityUtils.sortByStatus(EntityUtils.applyStatusRules(
                    colocationService.getIndex(accountId), null, false));
        }
        return "success";
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (accountId != null) {
            Account account = accountService.find(accountId);
            contexts.switchTo(account);
        }
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Collection<AccountTO> getAccounts() {
        return accounts;
    }

    public Collection<EntityTO> getColocations() {
        return colocations;
    }
}
