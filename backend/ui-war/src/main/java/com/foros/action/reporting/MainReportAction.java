package com.foros.action.reporting;

import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.ServiceLocator;
import com.foros.session.reporting.ReportRestrictions;
import com.foros.session.birt.BirtReportService;
import com.foros.util.AccountUtil;
import com.foros.util.context.Contexts;

import java.security.AccessControlException;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

public class MainReportAction extends BaseActionSupport implements ServletRequestAware {
    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private ReportRestrictions reportRestrictions;

    @EJB
    private RestrictionService restrictionService;

    private HttpServletRequest request;
    private IdNameBean account = new IdNameBean();
    private boolean isInternal;

    public boolean getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }
    
    @Override
    public void setServletRequest(HttpServletRequest httpservletrequest){
        this.request = httpservletrequest;
    }

    @ReadOnly
    @Restrict(restriction = "Report.runAny")
    public String viewReports() throws Exception {
        String requestURI = request.getRequestURI();
        if(requestURI.contains("admin/publisher/")) {
            Contexts.getContexts(request).getPublisherContext().switchTo(AccountUtil.setExternalAccountId(this, "account.id"));
        } else if(requestURI.contains("admin/isp/")) {
            Contexts.getContexts(request).getIspContext().switchTo(AccountUtil.setExternalAccountId(this, "account.id"));
        } else if(requestURI.contains("admin/advertiser/")) {
            Contexts.getContexts(request).getAdvertiserContext().switchTo(AccountUtil.setExternalAccountId(this, "account.id"));
            AdvertisingAccountBase external = Contexts.getContexts(request).getAdvertiserContext().getAccount();
            boolean accessGranted = reportRestrictions.canRunAnyOf("advertiser", "conversions") || reportRestrictions.canRun("textAdvertising") && restrictionService.isPermitted("AdvertiserEntity.accessTextAd", external);
            if (!accessGranted) {
                throw new AccessControlException("Access is forbidden!");
            }
        } else if(requestURI.contains("admin/cmp/")) {
            Contexts.getContexts(request).getCmpContext().switchTo(AccountUtil.setExternalAccountId(this, "account.id"));
        }

        setIsInternal(currentUserService.isInternal());
        if (isInternal) {
            BirtReportService reportService = ServiceLocator.getInstance().lookup(BirtReportService.class);
            request.setAttribute("birtReports", reportService.getIndex());
        }
        return SUCCESS;
    }

    public IdNameBean getAccount() {
        return account;
    }

    public void setAccount(IdNameBean account) {
        this.account = account;
    }
}
