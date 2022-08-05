package com.foros.session.reporting.conversionPixels;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.ActionTestFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import group.Db;
import group.Report;
import java.util.Arrays;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class ConversionPixelsReportServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private ConversionPixelsReportService service;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private ActionTestFactory actionTF;

    @Test
    public void testProcessHtml() {
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent();
        Action action = actionTF.createPersistent(advertiserAccount);
        commitChanges();

        ConversionPixelsReportParameters parameters = new ConversionPixelsReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate().minusDays(300), new LocalDate()));
        parameters.setAccountId(advertiserAccount.getId());
        parameters.setConversionIds(Arrays.asList(action.getId()));

        SimpleReportData data = new SimpleReportData();
        service.processHtml(parameters, data);

        MetaData metaData = data.getMetaData();
        assertTrue(metaData instanceof ReportMetaData);
        ReportMetaData reportMetaData = (ReportMetaData) metaData;
        assertEquals(7, reportMetaData.getColumnsMeta().getColumnsWithDependencies().size());
        assertNotNull(data.getRows());

        parameters.setShowResultsByDay(true);
        data = new SimpleReportData();
        service.processHtml(parameters, data);
        metaData = data.getMetaData();
        assertTrue(metaData instanceof ReportMetaData);
        reportMetaData = (ReportMetaData) metaData;
        assertEquals(8, reportMetaData.getColumnsMeta().getColumnsWithDependencies().size());
        assertNotNull(data.getRows());

    }
}
