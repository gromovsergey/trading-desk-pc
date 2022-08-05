package com.foros.session.reporting.channeltriggers;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.channel.BehavioralChannel;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.BehavioralChannelTestFactory;

import group.Db;
import group.Report;

import java.util.Arrays;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class ChannelTriggerReportServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private ChannelTriggerReportService channelReportsService;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Test public void dummy() { }

    // @Test
    // TODO move to Impala
    public void testProcessHtml() {
        BehavioralChannel channel = behavioralChannelTF.create();

        channel.getPageKeywords().setPositive("foo", "bar");
        channel.getSearchKeywords().setPositive("foo", "bar");
        channel.getUrls().setPositive("foo.com", "bar.com", "another.com");
        behavioralChannelTF.persist(channel);

        ChannelTriggersReportParameters parameters = new ChannelTriggersReportParameters();
        DateRange dateRange = new DateRange();
        dateRange.setBegin(new LocalDate());
        dateRange.setEnd(new LocalDate());
        parameters.setDateRange(dateRange);
        parameters.setAccountId(channel.getAccount().getId());
        parameters.setChannelIds(Arrays.asList(channel.getId()));

        ChannelTriggerReportData result = new ChannelTriggerReportData();
        channelReportsService.processHtml(parameters, "%d", result);

        assertNotNull(result.getPreparedParameters());

        checkResult(result.getUrls());
        checkResult(result.getPageKeywords());
        checkResult(result.getSearchKeywords());
    }

    private void checkResult(SimpleReportData reportData) {
        assertNotNull(reportData);
        assertNotNull(reportData.getHeaders());
        assertNotNull(reportData.getRows());
    }
}
