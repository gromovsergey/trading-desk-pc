package com.foros.action.reporting.profiling;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.IspSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.account.AccountService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

public class ViewProfilingReportingAction extends BaseActionSupport implements RequestContextsAware, IspSelfIdAware {

    @EJB
    AccountService accountService;

    private Long accountId;

    @ReadOnly
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#target.accountId")
    public String view() throws Exception {
        return SUCCESS;
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

    @Override
    public void setIspId(Long ispId) {
        accountId = ispId;
    }

}

