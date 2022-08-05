package com.foros.session.reporting.webwise;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.Report;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.ColocationTestFactory;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import group.Db;

import java.util.Collection;
import java.util.LinkedList;

@Category({ Db.class, Report.class })
public class WebwiseReportServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private ColocationTestFactory coloTF;

    @Autowired
    private WebwiseReportService reportService;

    @Test
    public void testProcessHtml() {
        Collection<Long> coloIds = new LinkedList<Long>();
        for (int i = 0; i < 5; i++) {
            coloIds.add(coloTF.createPersistent().getId());
        }
        WebwiseReportParameters params = new WebwiseReportParameters();
        params.setColocationIds(coloIds);
        params.setDateRange(new DateRange(new LocalDate(), new LocalDate()));

        reportService.processHtml(params, new SimpleReportData());
    }

}
