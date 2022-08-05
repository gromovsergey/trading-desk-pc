package com.foros.session.reporting.isp;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.isp.Colocation;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.ColocationTestFactory;

import group.Db;
import group.Report;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class ISPReportServiceTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private ColocationTestFactory colocationTF;

    @Autowired
    private ISPReportService reportService;

    @Test
    public void testProcessHtml() {
        Colocation colocation = colocationTF.createPersistent();
        ISPReportParameters parameters = new ISPReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.setReportType(ISPReportType.BY_DATE);
        parameters.setAccountId(colocation.getAccount().getId());

        SimpleReportData data = new SimpleReportData();
        reportService.processHtml(parameters, data);

        assertNotNull(data.getRows());
    }
}
