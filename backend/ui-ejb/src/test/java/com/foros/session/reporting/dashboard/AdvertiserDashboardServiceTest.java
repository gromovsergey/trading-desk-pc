package com.foros.session.reporting.dashboard;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.session.campaign.AdvertiserDashboardService;
import com.foros.session.campaign.AdvertiserDashboardTO;
import com.foros.session.campaign.CampaignDashboardTO;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;

import group.Db;
import group.Report;
import java.util.Calendar;
import java.util.List;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class AdvertiserDashboardServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    AdvertiserDashboardService advertiserDashboardService;

    @Test
    public void testAdvertiserStats() {
        AccountDashboardParameters parameters = new AccountDashboardParameters();
        parameters.setAccountId(agencyAccountTF.createPersistent().getId());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        parameters.setDateRange(new DateRange(LocalDate.fromDateFields(calendar.getTime()), new LocalDate()));
        parameters.setWithActivityOnly(false);

        List<AdvertiserDashboardTO> result = advertiserDashboardService.getAdvertiserDashboardStats(parameters);

        assertNotNull(result);
    }

    @Test
    public void testCampaignStats() {
        AccountDashboardParameters parameters = new AccountDashboardParameters();
        parameters.setAccountId(advertiserAccountTF.createPersistent().getId());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        parameters.setDateRange(new DateRange(LocalDate.fromDateFields(calendar.getTime()), new LocalDate()));
        parameters.setWithActivityOnly(false);

        List<CampaignDashboardTO> result = advertiserDashboardService.getCampaignDashboardStats(parameters);

        assertNotNull(result);
    }
}
