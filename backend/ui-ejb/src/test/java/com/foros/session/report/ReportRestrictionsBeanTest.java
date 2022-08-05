package com.foros.session.report;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.Account;
import com.foros.model.account.PublisherAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.security.AccountType;
import com.foros.session.reporting.Report;
import com.foros.session.reporting.ReportRestrictions;
import com.foros.session.reporting.inventoryEstimation.InventoryEstimationReportParameters;
import com.foros.test.factory.BehavioralChannelTestFactory;

import group.Db;
import group.Restriction;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class, Report.class })
public class ReportRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private ReportRestrictions reportRestrictions;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Override
    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, true);
    }

    @Test
    public void testCanRunGeneralReports() throws Exception {
        Callable custom = generateCall("custom");
        Callable conversionPixels = generateCall("conversionPixels");
        Callable siteChannels = generateCall("siteChannels");
        Callable referrer = generateCall("referrer");
        Callable audit = generateCall("audit");
        Callable channelSites = generateCall("channelSites");

        expectResult(advertiserAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(publisherAllAccess1, false);

        doCheck(custom);
        doCheck(conversionPixels);
        doCheck(siteChannels);
        doCheck(audit);
        doCheck(channelSites);

        expectResult(publisherAllAccess1, true);
        doCheck(referrer);
    }

    @Test
    public void testCanRunChannelUsageReport() throws Exception {
        Callable callable = generateCall("channelUsage");
        expectResult(advertiserAllAccess1, false);
        expectResult(cmpAllAccess1, true);
        expectResult(ispAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        doCheck(callable);

    }

    @Test
    public void testCanRunISPReports() throws Exception {
        Callable webwise = generateCall("webwise");
        Callable isp = generateCall("ISP");
        Callable invitations = generateCall("invitations");

        expectResult(advertiserAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, true);
        expectResult(publisherAllAccess1, false);

        doCheck(webwise);
        doCheck(isp);
        doCheck(invitations);
    }

    @Test
    public void testCanRunPublisherReports() throws Exception {
        Callable publisher = generateCall("publisher");

        expectResult(advertiserAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(publisherAllAccess1, true);

        doCheck(publisher);
    }

    @Test
    public void testCanRunAdvertiserReports() throws Exception {
        Callable advertiser = generateCall("advertiser");
        Callable conversions = generateCall("conversions");

        expectResult(advertiserAllAccess1, true);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(publisherAllAccess1, false);

        doCheck(advertiser);
        doCheck(conversions);
    }

    @Override
    @org.junit.Before
    public void setUp() throws Exception {
        super.setUp();
        AccountType accountType = ispAllAccess1.getUser().getAccount().getAccountType();
        accountType.setAdvancedReportsFlag(true);
        getEntityManager().merge(accountType);
        commitChanges();
    }

    @Test
    public void testCanRunAdvancedISPReports() throws Exception {
        final Account account = ispAllAccess1.getUser().getAccount();
        account.getAccountType().setAdvancedReportsFlag(true);

        Callable report = new Callable("AdvancedISPReports.run", null) {
            @Override
            public boolean call() {
                return reportRestrictions.canRunAdvancedISPReports(account.getId());
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, true);
        expectResult(ispNoAccess, false);
        expectResult(publisherAllAccess1, false);

        doCheck(report);

        account.getAccountType().setAdvancedReportsFlag(false);

        expectResult(ispAllAccess1, false);

        doCheck(report);
    }

    @Test
    public void testCanRunChannelReports() throws Exception {
        Callable channel = generateCall("channel");

        expectResult(advertiserAllAccess1, true);
        expectResult(agencyAllAccess1, true);
        expectResult(cmpAllAccess1, true);
        expectResult(ispAllAccess1, false);
        expectResult(publisherAllAccess1, false);

        doCheck(channel);
    }

    @Test
    public void testViewChannelTriggersReport() throws Exception {
        Account account = advertiserAllAccess1.getUser().getAccount();
        final BehavioralChannel ch = behavioralChannelTF.createPersistent(account);
        entityManager.flush();

        Callable withChannel = new Callable("predefined_report_channelTriggers", "run") {
            @Override
            public boolean call() {
                return reportRestrictions.canViewChannelTriggersReport(ch);
            }
        };

        Callable withoutChannel = new Callable("predefined_report_channelTriggers", "run") {
            @Override
            public boolean call() {
                return reportRestrictions.canViewChannelTriggersReport(null);
            }
        };

        expectResult(advertiserAllAccess1, true);
        expectResult(cmpAllAccess1, false);
        expectResult(internalAllAccess, true);

        doCheck(withChannel);

        expectResult(advertiserAllAccess1, true);
        expectResult(cmpAllAccess1, true);
        expectResult(internalAllAccess, true);

        doCheck(withoutChannel);
    }

    @Test
    public void testCanRunInventoryEstimationReport() throws Exception {
        final PublisherAccount publisherAccount = (PublisherAccount) publisherAllAccess1.getUser().getAccount();
        final InventoryEstimationReportParameters parameters = new InventoryEstimationReportParameters();
        parameters.setAccountId(publisherAccount.getId());

        Callable inventoryEstimationCallable = new Callable("predefined_report_inventoryEstimation", "run") {
            @Override
            public boolean call() {
                return reportRestrictions.canRunInventoryEstimationReport(parameters);
            }
        };

        expectResult(advertiserAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(internalAllAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(inventoryEstimationCallable);

        AccountType accountType = publisherAccount.getAccountType();
        accountType.setPublisherInventoryEstimationFlag(true);
        persist(accountType);

        resetExpectationsToDefault();
        expectResult(advertiserAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(internalAllAccess, true);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherNoAccess, false);

        doCheck(inventoryEstimationCallable);
    }

    private Callable generateCall(final String reportName) {
        return new Callable("predefined_report_" + reportName, "run") {
            @Override
            public boolean call() {
                return reportRestrictions.canRun(reportName);
            }
        };
    }
}
