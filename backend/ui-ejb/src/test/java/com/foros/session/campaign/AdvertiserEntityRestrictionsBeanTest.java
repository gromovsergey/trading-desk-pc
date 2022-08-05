package com.foros.session.campaign;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.Account;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.RateType;
import com.foros.model.security.AccountType;
import com.foros.model.security.AccountTypeCCGType;
import com.foros.test.factory.ActionTestFactory;
import com.foros.test.factory.TextCCGTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.validation.ValidationContext;

import group.Db;
import group.Restriction;

import java.math.BigDecimal;
import java.util.HashSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class AdvertiserEntityRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @Autowired
    private TextCampaignTestFactory campaignTF;

    @Autowired
    private TextCCGTestFactory ccgTestFactory;

    @Autowired
    private ActionTestFactory actionTestFactory;

    private Account account;

    @Test
    public void testIsTextAdvertisingAllowed() {
        Campaign campaign = campaignTF.createPersistent();
        account = campaign.getAccount();
        AccountType accountType = account.getAccountType();

        accountType.setAllowTextKeywordAdvertisingFlag(true);
        assert (advertiserEntityRestrictions.canAccessTextAd(account));

        accountType.setAllowTextKeywordAdvertisingFlag(false);
        accountType.setCcgTypes(new HashSet<AccountTypeCCGType>());
        assertFalse(advertiserEntityRestrictions.canAccessTextAd(account));
    }

    @Test
    public void testCanResetCtr() {
        final Campaign campaign = campaignTF.createPersistent();
        Callable callCanResetCtr = new Callable("advertiser_entity", "reset_ctr_date") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canResetCtr(campaign);
            }
        };
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        doCheck(callCanResetCtr);

        final CampaignCreativeGroup group = ccgTestFactory.createPersistent(campaign);
        callCanResetCtr = new Callable("advertiser_entity", "reset_ctr_date") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canResetCtr(group);
            }
        };

        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        doCheck(callCanResetCtr);

        CcgRate rate = ccgTestFactory.createCcgRate(group, RateType.CPM, BigDecimal.TEN);
        group.setCcgRate(rate);
        getEntityManager().flush();
        getEntityManager().clear();

        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, false);
        doCheck(callCanResetCtr);
    }

    @Test
    public void testIsDisplayAdvertisingAllowed() {
        Campaign campaign = campaignTF.createPersistent();
        account = campaign.getAccount();
        AccountType accountType = account.getAccountType();

        accountType.setCPAFlag(CCGType.DISPLAY, true);
        accountType.setCPCFlag(CCGType.DISPLAY, true);
        accountType.setCPMFlag(CCGType.DISPLAY, true);
        assert (advertiserEntityRestrictions.canAccessDisplayAd(account));

        accountType.setCcgTypes(new HashSet<AccountTypeCCGType>());
        assertFalse(advertiserEntityRestrictions.canAccessDisplayAd(account));
    }

    @Test
    public void testIsKeywordTargetedTextAdAllowed() {
        Campaign campaign = campaignTF.createPersistent();
        account = campaign.getAccount();
        AccountType accountType = account.getAccountType();
        accountType.setAllowTextKeywordAdvertisingFlag(true);

        assert new ContextCall() {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canAccessKeywordTargetedTextAd(context, account);
            }
        }.call();

        accountType.setAllowTextKeywordAdvertisingFlag(false);
        accountType.setCcgTypes(new HashSet<AccountTypeCCGType>());
        assertFalse(
                new ContextCall() {
                    @Override
                    public void call(ValidationContext context) {
                        advertiserEntityRestrictions.canAccessKeywordTargetedTextAd(context, account);
                    }
                }.call()
        );
    }

    @Test
    public void testIsChannelTargetedTextAdAllowed() {
        Campaign campaign = campaignTF.createPersistent();
        account = campaign.getAccount();
        AccountType accountType = account.getAccountType();
        accountType.setCPAFlag(CCGType.TEXT, true);
        accountType.setCPCFlag(CCGType.TEXT, true);
        accountType.setCPMFlag(CCGType.TEXT, true);
        assert (advertiserEntityRestrictions.canAccessChannelTargetedTextAd(account));
        accountType.setCcgTypes(new HashSet<AccountTypeCCGType>());
        assertFalse(advertiserEntityRestrictions.canAccessChannelTargetedTextAd(account));
    }
}
