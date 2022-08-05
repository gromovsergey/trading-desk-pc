package com.foros.session.reporting.campaignOverview;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.IspAccount;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.IspAccountTestFactory;

import group.Db;
import group.Report;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class CampaignOverviewReportServiceTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private IspAccountTestFactory testFactory;

    @Autowired
    private CampaignOverviewReportService reportService;

    @Test
    public void testProcessHtml() {
        CampaignOverviewReportParameters parameters = new CampaignOverviewReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        IspAccount ispAccount = testFactory.createPersistent();
        parameters.setAccountId(ispAccount.getId());
        parameters.setSegmentByProduct(true);
        parameters.setSegmentByVertical(true);

        SimpleReportData data = new SimpleReportData();
        reportService.processHtml(parameters, data);

        assertNotNull(data.getRows());
    }

}
