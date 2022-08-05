package com.foros.action.reporting.dashboard;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.ClobParam;
import com.foros.model.ClobParamType;
import com.foros.model.account.PublisherAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountService;
import com.foros.session.admin.ClobParamService;
import com.foros.session.reporting.dashboard.AccountDashboardParameters;
import com.foros.session.reporting.dashboard.PublisherDashboardService;
import com.foros.session.reporting.dashboard.PublisherDashboardTO;
import com.foros.session.reporting.dashboard.SiteDashboardTO;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.util.DateHelper;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;

public class SiteDashboardAction extends BaseActionSupport implements RequestContextsAware, ServletRequestAware, ModelDriven<AccountDashboardParameters>, PublisherSelfIdAware {
    private HttpServletRequest request;

    @EJB
    private AccountService accountService;

    @EJB
    private PublisherDashboardService dashboardService;

    @EJB
    private ClobParamService clobParamService;

    @EJB
    private CurrentUserService currentUserService;

    private AccountDashboardParameters parameters = new AccountDashboardParameters();
    private ClobParam clobParam;

    private PublisherAccount account;

    private List<SiteDashboardTO> result;

    private PublisherDashboardTO total;

    @ReadOnly
    public ClobParam getPublisherNotice() {
        if (clobParam == null) {
            clobParam = clobParamService.find(getAccount().getInternalAccount().getId(), ClobParamType.PUBLISHER_NOTE);
        }
        return clobParam;
    }

    public PublisherAccount getAccount() {
        if (account == null) {
            account = accountService.findPublisherAccount(parameters.getAccountId());
        }
        return account;
    }

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
    @ReadOnly
    @Restrict(restriction = "PublisherEntity.view", parameters = "#target.getAccount()")
    public String execute() {
        initializeParameters();
        result = dashboardService.generateSiteDashboard(parameters);
        return SUCCESS;
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
        contexts.getPublisherContext().switchTo(getAccount());
    }

    @Override
    public void setPublisherId(Long publisherId) {
        parameters.setAccountId(publisherId);
    }

    public List<SiteDashboardTO> getResult() {
        return result;
    }

    public PublisherDashboardTO getTotal() {
        if (total == null) {
            long creditedImps = 0;
            long imps = 0;
            BigDecimal ecpm = BigDecimal.ZERO;
            BigDecimal revenue = BigDecimal.ZERO;
            long requests = 0;
            long clicks = 0;
            BigDecimal ctr = BigDecimal.ZERO;
            for (PublisherDashboardTO to : result) {
                creditedImps += to.getCreditedImps();
                imps += to.getImps();
                revenue = revenue.add(to.getRevenue());
                requests += to.getRequests();
                clicks += to.getClicks();
            }

            if (imps != 0) {
                final int fractionDigits = getAccount().getCurrency().getFractionDigits();
                ecpm = revenue.multiply(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(imps), fractionDigits, RoundingMode.HALF_UP);
                ctr = BigDecimal.valueOf(clicks).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(imps), fractionDigits, RoundingMode.HALF_UP);
            }
            total = new SiteDashboardTO.Builder()
                    .creditedImps(creditedImps)
                    .imps(imps)
                    .ecpm(ecpm)
                    .revenue(revenue)
                    .requests(requests)
                    .clicks(clicks)
                    .ctr(ctr)
                    .build()
            ;
        }
        return total;
    }

    public boolean isAvailableCreditedImps() {
        return isInternal() && getTotal().getCreditedImps() > 0;
    }

    public boolean isClicksDataAvailable() {
        return isInternal() || getAccount().getAccountType().isClicksDataVisibleToExternal();
    }
}
