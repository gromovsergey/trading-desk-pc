package com.foros.session.reporting.inventoryEstimation;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.site.Tag;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.TagsTestFactory;

import group.Db;
import group.Report;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class InventoryEstimationReportServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private InventoryEstimationReportService reportsService;

    @Autowired
    private TagsTestFactory tagsTF;

    @Test
    public void testProcessHtml() {
        InventoryEstimationReportParameters parameters = new InventoryEstimationReportParameters();

        DateRange dateRange = new DateRange();
        dateRange.setBegin(new LocalDate());
        dateRange.setEnd(new LocalDate());
        parameters.setDateRange(dateRange);

        Tag tag = tagsTF.createPersistent();
        parameters.setAccountId(tag.getAccount().getId());
        parameters.setSiteId(tag.getSite().getId());
        parameters.setTagId(tag.getId());
        parameters.setReservedPremium(BigDecimal.ONE);

        SimpleReportData result = new SimpleReportData();
        reportsService.processHtml(parameters, result);

        assertNotNull(result.getPreparedParameters());

        assertNotNull(result);
        assertNotNull(result.getHeaders());
        assertNotNull(result.getRows());
    }

}
