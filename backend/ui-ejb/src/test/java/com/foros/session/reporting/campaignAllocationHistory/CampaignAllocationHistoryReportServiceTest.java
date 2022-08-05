package com.foros.session.reporting.campaignAllocationHistory;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.campaign.Campaign;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.test.factory.TextCampaignTestFactory;

import javax.ejb.EJB;

import group.Db;
import group.Report;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Report.class })
public class CampaignAllocationHistoryReportServiceTest extends AbstractServiceBeanIntegrationTest {
    @EJB
    private CampaignAllocationHistoryReportService reportService;

    @EJB
    private TextCampaignTestFactory campaignTF;

    @Test
    public void testProcess() throws Exception {
        Campaign campaign = campaignTF.createPersistent();

        reportService.processHtml(campaign.getId(), new SimpleReportData());
        reportService.processCsv(campaign.getId(), new NullOutputStream());
        reportService.processExcel(campaign.getId(), new NullOutputStream());
    }
}
