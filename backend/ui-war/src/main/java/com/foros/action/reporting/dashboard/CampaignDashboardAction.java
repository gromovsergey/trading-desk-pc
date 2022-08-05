package com.foros.action.reporting.dashboard;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.campaign.CampaignDashboardTO;

import java.util.List;

public class CampaignDashboardAction extends AdvertiserDashboardActionSupport implements AdvertiserSelfIdAware {

    private AdvertiserAccount account;
    private List<CampaignDashboardTO> stats;

    @Override
    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "#target.getAccount()")
    public String execute() {
        initializeParameters();
        stats = dashboardService.getCampaignDashboardStats(parameters);
        setTotal(dashboardService.getCampaignDashboardTotal(parameters));
        return SUCCESS;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        parameters.setAccountId(advertiserId);
    }

    private Long getAgencyAccountId() {
        AdvertiserAccount acc = getAccount();
        return acc.isInAgencyAdvertiser() ? acc.getAgency().getId() : acc.getId();
    }

    @Override
    public AdvertiserAccount getAccount() {
        if (account == null) {
            account = accountService.findAdvertiserAccount(parameters.getAccountId());
        }
        return account;
    }

    @Override
    public List<CampaignDashboardTO> getResult() {
        return stats;
    }
}
