package com.foros.session.reporting.referrer;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.site.Site;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.SiteTestFactory;

import group.Db;
import group.Report;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class ReferrerReportServiceTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private ReferrerReportService reportService;

    // ToDo: Please turn on when impala source will be ready
    @Test
    public void testProcessHtml() {
        /*Site site = siteTF.createPersistent();
        ReferrerReportParameters parameters = new ReferrerReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.setAccountId(site.getAccount().getId());
        parameters.setSiteId(site.getId());

        reportService.processHtml(parameters, new SimpleReportData());*/
    }
}
