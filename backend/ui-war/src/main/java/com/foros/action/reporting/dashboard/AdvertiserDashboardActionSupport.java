package com.foros.action.reporting.dashboard;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.ClobParam;
import com.foros.model.ClobParamType;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.session.account.AccountService;
import com.foros.session.admin.ClobParamService;
import com.foros.session.campaign.AdvertiserDashboardService;
import com.foros.session.campaign.DashboardTO;
import com.foros.session.reporting.ReportHelper;
import com.foros.session.reporting.dashboard.AccountDashboardParameters;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.util.DateHelper;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;

public abstract class AdvertiserDashboardActionSupport extends BaseActionSupport implements RequestContextsAware, ServletRequestAware, ModelDriven<AccountDashboardParameters> {
    protected HttpServletRequest request;

    @EJB
    protected AccountService accountService;

    @EJB
    protected AdvertiserDashboardService dashboardService;

    @EJB
    private ClobParamService clobParamService;

    protected AccountDashboardParameters parameters = new AccountDashboardParameters();
    private ClobParam clobParam;
    private DashboardTO total;

    @ReadOnly
    public ClobParam getAdvertiserNotice() {
        if (clobParam == null) {
            clobParam = clobParamService.find(getAccount().getInternalAccount().getId(), ClobParamType.ADVERTISER_NOTE);
        }
        return clobParam;
    }

    public abstract AdvertisingAccountBase getAccount();
    public abstract List<? extends DashboardTO> getResult();

    protected void initializeParameters() {
        if (parameters.getDateRange() == null) {
            TimeZone accountTimeZone = TimeZone.getTimeZone(getAccount().getTimezone().getKey());
            LocalDate begin = DateHelper.thisMonthBegin(accountTimeZone);
            LocalDate end = DateHelper.thisMonthEnd(accountTimeZone);
            parameters.setDateRange(new DateRange(begin, end));
            request.setAttribute("fastChangeId", "MTD");
        } else if (request.getParameter("fastChangeId") != null) {
            request.setAttribute("fastChangeId", request.getParameter("fastChangeId"));
        }
    }

    @Override
    public AccountDashboardParameters getModel() {
        return parameters;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getAccount());
    }

    protected void setTotal(DashboardTO total) {
        this.total = total;
    }

    public DashboardTO getTotal() {
        return total;
    }

    public boolean isShowUniqueUsers() {
        return ReportHelper.isLessThanMonth(
                parameters.getDateRange().getBegin().toDateTimeAtStartOfDay(), parameters.getDateRange().getEnd().toDateTimeAtStartOfDay());
    }

    public boolean isShowCreditUsed() {
        return total != null && total.getCampaignCreditUsed().compareTo(BigDecimal.ZERO) > 0;
    }
}
