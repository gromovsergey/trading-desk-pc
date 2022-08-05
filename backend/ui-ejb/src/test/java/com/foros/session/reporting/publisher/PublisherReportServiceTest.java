package com.foros.session.reporting.publisher;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.PublisherAccount;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.PublisherAccountTestFactory;

import group.Db;
import group.Report;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
@Ignore
public class PublisherReportServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private PublisherReportService publisherReportService;

    @Autowired
    private PublisherAccountTestFactory accountTF;

    // @Test
    // This test if for development only, because
    // - To test BI Cubes there is as special Bamboo task
    // - Bamboo script doesn't patch BI schema on unittest_ui_N database,
    //   as we want to keep unit tests' execution time minimal
    public void testProcessHtml() {
        PublisherAccount account = accountTF.createPersistent();
        SimpleReportData data = new SimpleReportData();
        PublisherReportParameters parameters = new PublisherReportParameters();

        parameters.setDateRange(new DateRange(new LocalDate().minusDays(300), new LocalDate()));
        parameters.setAccountId(account.getId());
        parameters.getColumns().add(PublisherMeta.DATE.getNameKey());
        parameters.getColumns().add(PublisherMeta.IMPRESSIONS.getNameKey());
        parameters.getColumns().add(PublisherMeta.REVENUE.getNameKey());
        parameters.getColumns().add(PublisherMeta.CLICKS.getNameKey());

        publisherReportService.processHtml(parameters, data);

        MetaData metaData = data.getMetaData();
        assertTrue(metaData instanceof ReportMetaData);
        ReportMetaData reportMetaData = (ReportMetaData) metaData;
        assertEquals(5, reportMetaData.getColumnsMeta().getColumnsWithDependencies().size());
        assertNotNull(data.getRows());
    }
}
