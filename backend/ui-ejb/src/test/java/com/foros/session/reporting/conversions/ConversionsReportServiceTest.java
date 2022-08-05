package com.foros.session.reporting.conversions;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.action.Action;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.ActionTestFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;

import group.Db;
import group.Report;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class ConversionsReportServiceTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private ConversionsReportService service;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private DisplayCampaignTestFactory campaignTF;

    @Autowired
    private DisplayCCGTestFactory ccgTF;

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTF;

    @Autowired
    private ActionTestFactory actionTF;

    @Test
    public void testProcessHtml() {
        AgencyAccount agencyAccount = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        Campaign campaign = campaignTF.createPersistent();
        CampaignCreativeGroup ccg = ccgTF.createPersistent(campaign);
        Creative creative = displayCreativeTF.createPersistent(advertiserAccount);
        Action action = actionTF.createPersistent(advertiserAccount);
        commitChanges();

        ConversionsReportParameters parameters = new ConversionsReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate().minusDays(300), new LocalDate()));
        parameters.setAccountId(agencyAccount.getId());
        parameters.getCampaignAdvertiserIds().add(advertiserAccount.getId());
        parameters.getCampaignIds().add(campaign.getId());
        parameters.getGroupIds().add(ccg.getId());
        parameters.getCreativeIds().add(creative.getId());
        parameters.getConversionIds().add(action.getId());
        parameters.setShowResultsByDay(true);
        for (DbColumn column : ConversionsMeta.META_BY_DATE.getColumns()) {
            parameters.getColumns().add(column.getNameKey());
        }

        SimpleReportData data = new SimpleReportData();
        service.processHtml(parameters, data);

        assertNotNull(data.getRows());
    }
}
