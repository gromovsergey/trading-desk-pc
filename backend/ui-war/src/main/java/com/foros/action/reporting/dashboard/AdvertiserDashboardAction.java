package com.foros.action.reporting.dashboard;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.model.account.AgencyAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.campaign.AdvertiserDashboardTO;
import com.foros.util.context.RequestContexts;
import com.foros.util.context.SessionContexts;

import java.util.List;

public class AdvertiserDashboardAction extends AdvertiserDashboardActionSupport implements AgencySelfIdAware {

    private AgencyAccount account;
    private List<AdvertiserDashboardTO> stats;

    @Override
    @ReadOnly
    @Restrict(restriction = "AdvertisingAccount.viewList")
    public String execute() {
        initializeParameters();
        stats = dashboardService.getAdvertiserDashboardStats(parameters);
        setTotal(dashboardService.getAdvertiserDashboardTotal(parameters));
        return SUCCESS;
    }

    @Override
    public void setAgencyId(Long agencyId) {
        parameters.setAccountId(agencyId);
    }

    public void setAdvertiserId(Long advertiserId) {
        setAgencyId(advertiserId);
    }

    @Override
    public AgencyAccount getAccount() {
        if (account == null) {
            account = accountService.findAgencyAccount(parameters.getAccountId());
        }
        return account;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        super.switchContext(contexts);
        SessionContexts.getSessionContexts(request).getAdvertiserContext().clearAgencyContext();
    }

    @Override
    public List<AdvertiserDashboardTO> getResult() {
        return stats;
    }
}
