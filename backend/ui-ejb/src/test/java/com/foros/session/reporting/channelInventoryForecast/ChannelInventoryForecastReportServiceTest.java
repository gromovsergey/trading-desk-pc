package com.foros.session.reporting.channelInventoryForecast;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.Report;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;

import group.Db;

import java.util.Collection;
import java.util.LinkedList;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class ChannelInventoryForecastReportServiceTest  extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CreativeSizeTestFactory sizeTestFactory;

    @Autowired
    private BehavioralChannelTestFactory channelTestFactory;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTestFactory;

    @Autowired
    private ChannelInventoryForecastReportService reportService;

    @Test
    public void testProcessHtml(){
        Collection<Long> channelIds = new LinkedList<Long>();
        Collection<Long> sizeIds = new LinkedList<Long>();
        AdvertiserAccount account = advertiserAccountTestFactory.createPersistent();
        for (int i = 0; i < 5; i++) {
            channelIds.add(channelTestFactory.createPersistent(account).getId());
            sizeIds.add(sizeTestFactory.createPersistent().getId());
        }

        ChannelInventoryForecastReportParameters parameters = new ChannelInventoryForecastReportParameters();
        parameters.setChannelIds(channelIds);
        parameters.setCreativeSizeIds(sizeIds);
        parameters.setAccountId(account.getId());

        reportService.processHtml(parameters, new SimpleReportData());
    }
}
