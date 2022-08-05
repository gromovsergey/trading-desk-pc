package com.foros.session.reporting.channelSites;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.channel.Channel;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.BehavioralChannelTestFactory;

import group.Db;
import group.Report;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class ChannelSitesReportServiceTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private BehavioralChannelTestFactory channelTF;

    @Autowired
    private ChannelSitesReportService reportService;

    @Test
    public void testProcessHtml() {
        Channel channel = channelTF.createPersistent();
        ChannelSitesReportParameters parameters = new ChannelSitesReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.setAccountId(channel.getAccount().getId());
        parameters.setChannelId(channel.getId());

        reportService.processHtml(parameters, new SimpleReportData());
    }
}
