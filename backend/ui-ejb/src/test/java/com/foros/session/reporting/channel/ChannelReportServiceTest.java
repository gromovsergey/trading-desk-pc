package com.foros.session.reporting.channel;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.channel.BehavioralChannel;
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
public class ChannelReportServiceTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private ChannelReportService reportsService;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Test
    public void testProcessHtml() {
        ChannelReportParameters parameters = new ChannelReportParameters();

        DateRange dateRange = new DateRange();
        dateRange.setBegin(new LocalDate());
        dateRange.setEnd(new LocalDate());
        parameters.setDateRange(dateRange);

        BehavioralChannel channel = prepareChannel();
        parameters.setChannelId(channel.getId());

        parameters.getOutputCols().add(ChannelMeta.DATE.getNameKey());
        parameters.getMetricCols().add(ChannelMeta.MATCHED_KEYWORDS.getNameKey());
        parameters.getMetricCols().add(ChannelMeta.MATCHED_URL.getNameKey());
        parameters.getMetricCols().add(ChannelMeta.MATCHED_URL_KEYWORDS.getNameKey());

        SimpleReportData result = new SimpleReportData();
        reportsService.processHtml(parameters, result);

        assertNotNull(result.getPreparedParameters());

        assertNotNull(result);
        assertNotNull(result.getHeaders());
        assertNotNull(result.getRows());
    }

    private BehavioralChannel prepareChannel() {
        BehavioralChannel channel = behavioralChannelTF.create();
        channel.getPageKeywords().setPositive("foo", "bar");
        channel.getUrls().setPositive("foo.com", "bar.com", "another.com");
        behavioralChannelTF.persist(channel);
        return channel;
    }
}