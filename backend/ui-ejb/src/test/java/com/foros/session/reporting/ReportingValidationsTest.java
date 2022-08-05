package com.foros.session.reporting;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.site.Tag;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;
import com.foros.session.reporting.inventoryEstimation.InventoryEstimationReportParameters;
import com.foros.session.reporting.invitations.InvitationsReportParameters;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.session.reporting.referrer.ReferrerReportParameters;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.IspAccountTestFactory;
import com.foros.test.factory.TagsTestFactory;

import group.Db;
import group.Report;
import group.Validation;
import java.math.BigDecimal;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class, Report.class })
public class ReportingValidationsTest extends AbstractValidationsTest {

    @Autowired
    private IspAccountTestFactory ispAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private TagsTestFactory tagsTF;

    @org.junit.Test
    public void testInvitationsReport() {
        IspAccount account = ispAccountTF.createPersistent();

        // all is OK
        InvitationsReportParameters parameters = goodInvitationsReportParameters(account);
        validate("Reporting.invitations", parameters);
        assertViolationsCount(0);

        // accountId missed
        parameters = goodInvitationsReportParameters(account);
        parameters.setAccountId(null);
        validate("Reporting.invitations", parameters);
        assertHasViolation("accountId");

        // account not found
        parameters = goodInvitationsReportParameters(account);
        parameters.setAccountId(Long.MAX_VALUE);
        validate("Reporting.invitations", parameters);
        assertHasViolation("accountId");

        // dateRange is missed
        parameters = goodInvitationsReportParameters(account);
        parameters.setDateRange(null);
        validate("Reporting.invitations", parameters);
        assertHasViolation("dateRange");

        // dateRange begin, end missed
        parameters = goodInvitationsReportParameters(account);
        parameters.setDateRange(new DateRange(null, null));
        validate("Reporting.invitations", parameters);
        assertHasViolation("dateRange.begin");
        assertHasViolation("dateRange.end");

        // dateRange invalid
        parameters = goodInvitationsReportParameters(account);
        parameters.setDateRange(new DateRange(new LocalDate(), (new LocalDate()).minusDays(1)));
        validate("Reporting.invitations", parameters);
        assertHasViolation("dateRange");
    }

    private InvitationsReportParameters goodInvitationsReportParameters(IspAccount account) {
        InvitationsReportParameters parameters = new InvitationsReportParameters();

        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.setAccountId(account.getId());
        parameters.setShowBrowserFamilies(true);

        return parameters;
    }

    @Test
    public void testTextAdvertiserReport() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        OlapAdvertiserReportParameters parameters;

        parameters = goodAdvertiserReportParameters(account);
        validate("Reporting.textAdvertiser", parameters);
        assertViolationsCount(0);

        // wrong report type
        parameters = goodAdvertiserReportParameters(account);
        parameters.setReportType(OlapDetailLevel.CreativeGroup);
        validate("Reporting.textAdvertiser", parameters);
        assertViolationsCount(1);
        assertHasViolation("reportType");

        // accountId missed
        parameters = goodAdvertiserReportParameters(account);
        parameters.setAccountId(null);
        validate("Reporting.textAdvertiser", parameters);
        assertHasViolation("accountId");

        // dateRange is missed
        parameters = goodAdvertiserReportParameters(account);
        parameters.setDateRange(null);
        validate("Reporting.textAdvertiser", parameters);
        assertViolationsCount(1);
        assertHasViolation("dateRange");
    }

    private OlapAdvertiserReportParameters goodAdvertiserReportParameters(AdvertiserAccount account) {
        OlapAdvertiserReportParameters parameters = new OlapAdvertiserReportParameters();
        parameters.setReportType(OlapDetailLevel.AdGroup);
        parameters.setAccountId(account.getId());
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.getColumns().add(OlapAdvertiserMeta.IMPRESSIONS.getNameKey());
        return parameters;
    }

    @Test
    public void testInventoryEstimationReport() {
        Tag tag = tagsTF.createPersistent();

        InventoryEstimationReportParameters parameters = new InventoryEstimationReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.setAccountId(tag.getAccount().getId());
        parameters.setSiteId(tag.getSite().getId());
        parameters.setTagId(tag.getId());
        parameters.setReservedPremium(BigDecimal.ONE);

        validate("Reporting.inventoryEstimation", parameters);
        assertViolationsCount(0);

        parameters.setTagId(0L);
        validate("Reporting.inventoryEstimation", parameters);
        assertViolationsCount(1);
        assertHasViolation("tagId");

        parameters.setSiteId(0L);
        validate("Reporting.inventoryEstimation", parameters);
        assertViolationsCount(1);
        assertHasViolation("siteId");

        parameters.setAccountId(0L);
        parameters.setSiteId(tag.getSite().getId());
        validate("Reporting.inventoryEstimation", parameters);
        assertViolationsCount(1);
        assertHasViolation("accountId");

        parameters.setReservedPremium(new BigDecimal("125.369"));
        validate("Reporting.inventoryEstimation", parameters);
        assertViolationsCount(3); // accountId , range from 0 to 100 , scale > 0
        assertHasViolation("reservedPremium");
    }

    @Test
    public void testReferrerReport() {
        Tag tag = tagsTF.createPersistent();

        ReferrerReportParameters parameters = new ReferrerReportParameters();
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.setAccountId(tag.getAccount().getId());
        parameters.setSiteId(tag.getSite().getId());
        parameters.setTagId(tag.getId());

        validate("Reporting.referrer", parameters);
        assertViolationsCount(0);

        parameters.setTagId(0L);
        validate("Reporting.referrer", parameters);
        assertViolationsCount(1);
        assertHasViolation("tagId");

        parameters.setTagId(tag.getId());
        parameters.setSiteId(0L);
        validate("Reporting.referrer", parameters);
        assertViolationsCount(1);
        assertHasViolation("siteId");

        tag = tagsTF.createPersistent();
        parameters.setSiteId(tag.getSite().getId());
        validate("Reporting.referrer", parameters);
        assertViolationsCount(1);
        assertHasViolation("siteId");
    }

}
