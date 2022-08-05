package com.foros.session.reporting.siteChannels;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.site.Tag;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;

import group.Db;
import group.Report;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class SiteChannelsReportServiceTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagTF;

    @Autowired
    private SiteChannelsReportService reportService;

    @Test
    public void testProcessHtml() {
        Tag tag = tagTF.createPersistent(siteTF.createPersistent());
        SiteChannelsReportParameters parameters = new SiteChannelsReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.setAccountId(tag.getSite().getAccount().getId());
        parameters.setSiteId(tag.getSite().getId());
        parameters.setTagId(tag.getId());

        reportService.processHtml(parameters, new SimpleReportData());
    }
}
