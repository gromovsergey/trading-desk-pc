package com.foros.session.reporting.custom;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.custom.olap.CustomOlapMeta;
import com.foros.session.reporting.custom.olap.CustomPredefinedOlapReportService;
import com.foros.session.reporting.parameters.ColumnOrderTO;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.session.reporting.parameters.Order;

import java.util.TimeZone;
import group.Db;
import group.Report;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
@Ignore
public class CustomPredefinedReportServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private CustomPredefinedOlapReportService customReportService;

    // @Test
    // This test if for development only, because
    // - To test BI Cubes there is as special Bamboo task
    // - Bamboo script doesn't patch BI schema on unittest_ui_N database,
    //   as we want to keep unit tests' execution time minimal
    public void testProcessHtml() {
        CustomReportParameters parameters = new CustomReportParameters();

        // output
        parameters.getOutputColumns().add(CustomOlapMeta.DATE.getNameKey());

        // metrics
        parameters.getMetricsColumns().add(CustomOlapMeta.INVENTORY_COST_GROSS.getNameKey());
        parameters.getMetricsColumns().add(CustomOlapMeta.CLICKS.getNameKey());

        // sort
        parameters.setSortColumn(new ColumnOrderTO(CustomOlapMeta.DATE.getNameKey(), Order.ASC));

        // date range
        parameters.setTimeZone(TimeZone.getTimeZone("GMT"));
        parameters.setDateRange(new DateRange(
                new LocalDate(System.currentTimeMillis() - 180 * 24 * 3600000L),
                new LocalDate(System.currentTimeMillis())));

        SimpleReportData data = new SimpleReportData();
        customReportService.processHtml(parameters, data, true);

        assertTrue(data.getRows().size() >= 0);
    }
}
