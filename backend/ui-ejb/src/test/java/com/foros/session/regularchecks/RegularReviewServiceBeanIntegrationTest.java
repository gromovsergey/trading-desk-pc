package com.foros.session.regularchecks;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.security.AccountType;
import com.foros.model.time.TimeSpan;
import com.foros.model.time.TimeUnit;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;

import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class RegularReviewServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private AdvertiserAccountTypeTestFactory accountTypeTF;

    @Autowired
    private AdvertiserAccountTestFactory accountTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private RegularReviewServiceBean regularReviewService;

    @Test
    public void testUpdateChannelCheck() {
        AccountType accountType = accountTypeTF.createPersistent();
        AdvertiserAccount account = accountTF.createPersistent(accountType);
        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        channel.setInterval(1);
        channel.setCheckNotes("test");
        try {
            regularReviewService.updateChannelCheck(channel);
            fail("Must be failed because channel check is not set");
        } catch (Exception e) {
            // it's ok
        }

        entityManager.clear();

        accountTypeTF.refresh(accountType);
        accountType.setChannelCheck(true);
        TimeSpan ts1 = new TimeSpan(1L, TimeUnit.HOUR);
        TimeSpan ts2 = new TimeSpan(1L, TimeUnit.DAY);
        TimeSpan ts3 = new TimeSpan(7L, TimeUnit.DAY);
        accountType.setChannelFirstCheck(ts1);
        accountType.setChannelSecondCheck(ts2);
        accountType.setChannelThirdCheck(ts3);
        accountTypeTF.update(accountType);

        try {
            regularReviewService.updateChannelCheck(channel);
        } catch (Exception e) {
            fail("Must not be failed because channel check is set");
        }

        behavioralChannelTF.refresh(channel);
        assertTrue(channel.getLastCheckDate() != null);
    }

    @Test
    public void testUpdateCCGCheck() {
        AccountType accountType = accountTypeTF.create();
        accountType.setCampaignCheck(true);
        TimeSpan ts1 = new TimeSpan(1L, TimeUnit.HOUR);
        TimeSpan ts2 = new TimeSpan(1L, TimeUnit.DAY);
        TimeSpan ts3 = new TimeSpan(7L, TimeUnit.DAY);
        accountType.setCampaignFirstCheck(ts1);
        accountType.setCampaignSecondCheck(ts2);
        accountType.setCampaignThirdCheck(ts3);
        accountTypeTF.persist(accountType);

        AdvertiserAccount account = accountTF.createPersistent(accountType);
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent(displayCampaignTF.createPersistent(account));

        ccg.setInterval(1);
        ccg.setCheckNotes("test");

        // Must not be failed because campaign check is set
        regularReviewService.updateCCGCheck(ccg);
    }

    @Test
    public void testUpdateCCGCheckRestricted() {
        AccountType accountType = accountTypeTF.createPersistent();
        AdvertiserAccount account = accountTF.createPersistent(accountType);
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent(displayCampaignTF.createPersistent(account));

        ccg.setInterval(1);
        ccg.setCheckNotes("test");

        try {
            regularReviewService.updateCCGCheck(ccg);
            fail("Must be failed because campaign check is not set");
        } catch (Exception e) {
            // it's ok
        }
    }

    @Test
    public void testSearch() {
        assertNotNull(regularReviewService.searchCCGsForReview("RU", 0, 100));
        assertNotNull(regularReviewService.searchChannelsForReview("RU", 0, 100));
    }

}
