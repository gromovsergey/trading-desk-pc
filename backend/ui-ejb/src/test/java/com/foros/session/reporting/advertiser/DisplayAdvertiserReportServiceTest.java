package com.foros.session.reporting.advertiser;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.Account;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;
import com.foros.session.reporting.advertiser.olap.OlapDisplayAdvertiserReportService;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.AgencyAccountTestFactory;

import group.Db;
import group.Report;
import java.util.HashSet;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
@Ignore
public class DisplayAdvertiserReportServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private OlapDisplayAdvertiserReportService service;

    @Autowired
    private AgencyAccountTestFactory accountTF;

    // @Test
    // This test if for development only, because
    // - To test BI Cubes there is as special Bamboo task
    // - Bamboo script doesn't patch BI schema on unittest_ui_N database,
    //   as we want to keep unit tests' execution time minimal
    public void testProcessHtml() {
        Account account = accountTF.createPersistent();

        OlapAdvertiserReportParameters parameters = new OlapAdvertiserReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate().minusDays(300), new LocalDate()));
        parameters.setAccountId(account.getId());
        parameters.setReportType(OlapDetailLevel.Advertiser);
        parameters.setCostAndRates(OlapAdvertiserReportParameters.CostAndRates.BOTH);
        parameters.setColumns(new HashSet<String>() {{
            add(OlapAdvertiserMeta.ADVERTISER.getNameKey());
            add(OlapAdvertiserMeta.IMPRESSIONS.getNameKey());
        }});

        SimpleReportData data = new SimpleReportData();
        service.processHtml(parameters, data);

        MetaData metaData = data.getMetaData();
        assertTrue(metaData instanceof ReportMetaData);
        ReportMetaData reportMetaData = (ReportMetaData) metaData;
        assertEquals(3, reportMetaData.getColumnsMeta().getColumnsWithDependencies().size());
        assertNotNull(data.getRows());
    }
}
