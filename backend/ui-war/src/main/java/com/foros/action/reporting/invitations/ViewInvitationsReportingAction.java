package com.foros.action.reporting.invitations;

import com.foros.action.BaseActionSupport;
import com.foros.util.helper.IndexHelper;
import com.foros.framework.ReadOnly;
import com.foros.model.account.IspAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.security.AccountTO;
import com.foros.util.context.Contexts;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

public class ViewInvitationsReportingAction extends BaseActionSupport implements ServletRequestAware {

    private HttpServletRequest request;
    private IspAccount account = new IspAccount();

    @Override
    public void setServletRequest(HttpServletRequest httpservletrequest) {
        this.request = httpservletrequest;
    }

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'invitations'")
    public String view() throws Exception {
        if (request.getRequestURI().contains("admin/isp/")) {
            Contexts.getContexts(request).getIspContext().switchTo(account.getId());
        } else if (SecurityContext.isInternal()) {
            List<AccountTO> accounts = IndexHelper.getAccountsList(AccountRole.ISP);
            if (accounts != null) {
                request.setAttribute("accounts", accounts);
            }
        } else {
            account.setId(Contexts.getContexts(request).getIspContext().getAccountId());
        }

        return "success";
    }

    public IspAccount getAccount() {
        return account;
    }

    public void setAccount(IspAccount account) {
        this.account = account;
    }
}
