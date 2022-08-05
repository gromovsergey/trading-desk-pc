package com.foros.session.regularchecks;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
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
import group.Validation;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class RegularReviewValidationsTest extends AbstractValidationsTest {
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
    private RegularReviewServiceBean regularReviewServiceBean;

    @Test
    public void testValidateInterval() throws Exception {
        AccountType accountType = accountTypeTF.create();
        accountType.setChannelCheck(true);
        TimeSpan ts1 = new TimeSpan(1L, TimeUnit.SECOND);
        TimeSpan ts2 = new TimeSpan(1L, TimeUnit.HOUR);
        TimeSpan ts3 = new TimeSpan(1L, TimeUnit.DAY);
        accountType.setChannelFirstCheck(ts1);
        accountType.setChannelSecondCheck(ts2);
        accountType.setChannelThirdCheck(ts3);
        accountTypeTF.persist(accountType);

        AdvertiserAccount account = accountTF.createPersistent(accountType);
        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        channel.setInterval(1);
        regularReviewServiceBean.updateChannelCheck(channel);

        entityManager.flush();
        entityManager.clear();

        channel.setInterval(null);
        validate("RegularReview.updateCheck", channel);
        assertViolationsCount(1);
        assertHasViolation("interval");

        channel.setInterval(4);
        validate("RegularReview.updateCheck", channel);
        assertViolationsCount(1);
        assertHasViolation("interval");

        channel.setInterval(3);
        validate("RegularReview.updateCheck", channel);
        assertViolationsCount(1);
        assertHasViolation("interval");

        channel.setInterval(2);
        validate("RegularReview.updateCheck", channel);
        assertViolationsCount(0);
    }

    @Test
    public void testValidateCheckNotes() throws Exception {
        AccountType accountType = createAccountType();

        AdvertiserAccount account = accountTF.createPersistent(accountType);
        Campaign campaign = displayCampaignTF.createPersistent(account);
        CampaignCreativeGroup group = displayCCGTF.createPersistent(campaign);
        group.setInterval(1);
        regularReviewServiceBean.updateCCGCheck(group);

        entityManager.flush();
        entityManager.clear();

        group.setCheckNotes(StringUtils.repeat("控", 4000));
        validate("RegularReview.updateCheck", group);
        assertViolationsCount(0);

        group.setCheckNotes(StringUtils.repeat("a", 4000));
        validate("RegularReview.updateCheck", group);
        assertViolationsCount(0);

        group.setCheckNotes(StringUtils.repeat("a", 4001));
        validate("RegularReview.updateCheck", group);
        assertViolationsCount(1);
        assertHasViolation("checkNotes");

    }

    private AccountType createAccountType() {
        AccountType accountType = accountTypeTF.create();

        accountType.setChannelCheck(true);
        TimeSpan ts1 = new TimeSpan(1L, TimeUnit.SECOND);
        TimeSpan ts2 = new TimeSpan(1L, TimeUnit.HOUR);
        TimeSpan ts3 = new TimeSpan(1L, TimeUnit.DAY);
        accountType.setChannelFirstCheck(ts1);
        accountType.setCampaignFirstCheck(ts1);
        accountType.setChannelSecondCheck(ts2);
        accountType.setCampaignSecondCheck(ts2);
        accountType.setChannelThirdCheck(ts3);
        accountType.setCampaignThirdCheck(ts3);

        accountTypeTF.persist(accountType);

        return accountType;
    }
}