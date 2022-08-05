package com.foros.session.campaign;

import com.foros.session.reporting.dashboard.AccountDashboardParameters;

import javax.ejb.Local;
import java.util.List;

@Local
public interface AdvertiserDashboardService {

    List<AdvertiserDashboardTO> getAdvertiserDashboardStats(final AccountDashboardParameters parameters);

    DashboardTO getAdvertiserDashboardTotal(final AccountDashboardParameters parameters);

    List<CampaignDashboardTO> getCampaignDashboardStats(final AccountDashboardParameters parameters);

    DashboardTO getCampaignDashboardTotal(final AccountDashboardParameters parameters);
}
