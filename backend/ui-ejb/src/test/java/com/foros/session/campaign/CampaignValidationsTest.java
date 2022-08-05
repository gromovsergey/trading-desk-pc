package com.foros.session.campaign;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.UserTestFactory;

import group.Db;
import group.Validation;
import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class CampaignValidationsTest extends AbstractValidationsTest {
    @Autowired
    private UserTestFactory userTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTestFactory;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    @Test
    public void testAccount() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign campaign = displayCampaignTF.create(accountType);

        validate("Campaign.create", campaign);
        assertViolationsCount(0);

        campaign.setAccount(null);
        validate("Campaign.create", campaign);
        assertHasViolation("account");

        campaign.setAccount(new AdvertiserAccount());
        validate("Campaign.create", campaign);
        assertHasViolation("account.id");

        AgencyAccount agency = agencyAccountTestFactory.createPersistent();
        campaign.setAccount(new AdvertiserAccount(agency.getId()));
        validate("Campaign.create", campaign);
        assertHasViolation("account.id");
    }

    @Test
    public void testDeliveryPacing() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign campaign = displayCampaignTF.createPersistent(accountType);
        campaign = new Campaign(campaign.getId());

        validate("Campaign.update", campaign);
        assertViolationsCount(0);

        campaign.setDeliveryPacing(DeliveryPacing.FIXED);
        campaign.setDailyBudget(null);
        validate("Campaign.update", campaign);
        assertHasViolation("dailyBudget");

        campaign.setDailyBudget(BigDecimal.valueOf(-1));
        validate("Campaign.update", campaign);
        assertHasViolation("dailyBudget");

        campaign.setDailyBudget(BigDecimal.valueOf(11111111111L));
        campaign.setBudget(BigDecimal.TEN);
        validate("Campaign.update", campaign);
        assertHasViolation("dailyBudget");

        campaign.setDailyBudget(Campaign.BUDGET_MAX);
        campaign.setBudget(Campaign.BUDGET_UNLIMITED);
        validate("Campaign.update", campaign);
        assertHasViolation("dailyBudget");

        campaign.setDailyBudget(BigDecimal.valueOf(11));
        campaign.setBudget(Campaign.BUDGET_UNLIMITED);
        validate("Campaign.update", campaign);
        assertViolationsCount(0);

        campaign.setDeliveryPacing(DeliveryPacing.DYNAMIC);
        campaign.setDateEnd(null);
        campaign.setBudget(BigDecimal.TEN);
        validate("Campaign.update", campaign);
        assertHasViolation("deliveryPacing");

        campaign.setDeliveryPacing(DeliveryPacing.DYNAMIC);
        campaign.setDateEnd(new Date(new Date().getTime() + 1000L));
        campaign.setBudget(Campaign.BUDGET_UNLIMITED);
        validate("Campaign.update", campaign);
        assertHasViolation("dynamicDailyBudget");
    }

    @Test
    public void testMaxPubShare() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign campaign = displayCampaignTF.createPersistent(accountType);
        campaign = new Campaign(campaign.getId());

        validate("Campaign.update", campaign);
        assertViolationsCount(0);

        campaign.setMaxPubShare(new BigDecimal("0.6"));
        validate("Campaign.update", campaign);
        assertViolationsCount(0);

        campaign.setMaxPubShare(BigDecimal.ZERO);
        validate("Campaign.update", campaign);
        assertViolationsCount(1);
        assertHasViolation("maxPubShare");

        campaign.setMaxPubShare(BigDecimal.TEN);
        validate("Campaign.update", campaign);
        assertViolationsCount(1);
        assertHasViolation("maxPubShare");

        campaign.setMaxPubShare(new BigDecimal("0.66"));
        validate("Campaign.update", campaign);
        assertViolationsCount(1);
        assertHasViolation("maxPubShare");

        campaign.setMaxPubShare(new BigDecimal("-0.60"));
        validate("Campaign.update", campaign);
        assertViolationsCount(1);
        assertHasViolation("maxPubShare");
    }

    @Test
    public void testDeliverySchedule() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign campaign = displayCampaignTF.create(accountType);

        // timeTo exceeds maximum limit.
        displayCampaignTF.addSchedule(campaign, 0L, 10080L);
        validate("Campaign.create", campaign);
        assertHasViolation("deliverySchedule");

        // timeFrom > timeTo
        displayCampaignTF.addSchedule(campaign, 59L, 0L);
        validate("Campaign.create", campaign);
        assertHasViolation("deliverySchedule");

        // timeFrom == null
        displayCampaignTF.addSchedule(campaign, null, 0L);
        validate("Campaign.create", campaign);
        assertHasViolation("deliverySchedule");

        // timeFrom not a multiple of 30.
        displayCampaignTF.addSchedule(campaign, 1L, 20L);
        validate("Campaign.create", campaign);
        assertHasViolation("deliverySchedule");
    }

    @Test
    public void testDeliveryScheduleWithIntersection() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign campaign = displayCampaignTF.create(accountType);
        displayCampaignTF.addSchedule(campaign, 0L, 59L);
        displayCampaignTF.addSchedule(campaign, 0L, 29L);

        validate("Campaign.create", campaign);
        assertHasViolation("deliverySchedule");
    }

    @Test
    public void testSalesManagers() throws Exception {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent(accountType);
        User salesManager = userTF.createPersistentSalesManager(advertiserAccount);
        Campaign campaign = displayCampaignTF.createPersistent(advertiserAccount);

        campaign.setSalesManager(salesManager);
        validate("Campaign.update", campaign);
        assertHasNoViolation("salesManager");

        campaign.setSalesManager(userTF.createPersistent());
        validate("Campaign.update", campaign);
        assertHasViolation("salesManager");

        campaign.setSalesManager(null);
        validate("Campaign.update", campaign);
        assertHasNoViolation("salesManager");
    }

    @Test
    public void testBudget() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign existing = displayCampaignTF.createPersistent(accountType);

        // IO Management OFF
        existing.getAccount().getAccountType().setIoManagement(false);

        assertCanEditBudget("Campaign.update", new Campaign(existing.getId()));
        assertCanCreateBudget("Campaign.create", existing.getAccount());

        // IO Management ON
        existing.getAccount().getAccountType().setIoManagement(true);

        // no budget in new campaigns
        assertCanNotCreateBudget("Campaign.create", existing.getAccount());
        // but campaign still in manual mode (since budget != 0)
        assertCanEditBudget("Campaign.update", new Campaign(existing.getId()));

        // Campaign switched to use campaign allocations
        existing.setBudget(BigDecimal.ZERO);
        assertCanNotEditBudgetButCanLeaveAsIs("Campaign.update", new Campaign(existing.getId()));
    }

    private void assertCanCreateBudget(String validationName, AdvertiserAccount account) {
        Campaign campaign = new Campaign();
        campaign.setAccount(new AdvertiserAccount(account.getId()));

        campaign.setBudget(null);
        validate(validationName, campaign);
        assertHasNoViolation("budget");

        campaign.setBudget(BigDecimal.TEN);
        validate(validationName, campaign);
        assertHasNoViolation("budget");

        campaign.setBudget(BigDecimal.ZERO);
        validate(validationName, campaign);
        assertHasViolation("budget");
    }

    private void assertCanNotCreateBudget(String validationName, AdvertiserAccount account) {
        Campaign campaign = new Campaign();
        campaign.setAccount(new AdvertiserAccount(account.getId()));

        campaign.setBudget(null);
        validate(validationName, campaign);
        assertHasViolation("budget");

        campaign.setBudget(BigDecimal.TEN);
        validate(validationName, campaign);
        assertHasViolation("budget");

        campaign.setBudget(BigDecimal.ZERO);
        validate(validationName, campaign);
        assertHasViolation("budget");
    }

    private void assertCanNotEditBudgetButCanLeaveAsIs(String validationName, Campaign campaign) {
        validate(validationName, campaign);
        assertViolationsCount(0);

        campaign.setBudget(null);
        validate(validationName, campaign);
        assertViolationsCount(1);

        campaign.setBudget(BigDecimal.TEN);
        validate(validationName, campaign);
        assertHasViolation("budget");
        assertViolationsCount(1);

        campaign.setBudget(BigDecimal.ZERO);
        validate(validationName, campaign);
        assertHasViolation("budget");
        assertViolationsCount(1);
    }

    private void assertCanEditBudget(String validationName, Campaign campaign) {
        validate(validationName, campaign);
        assertViolationsCount(0);

        campaign.setBudget(null);
        validate(validationName, campaign);
        assertViolationsCount(0);

        campaign.setBudget(BigDecimal.TEN);
        validate(validationName, campaign);
        assertViolationsCount(0);

        campaign.setBudget(BigDecimal.ZERO);
        validate(validationName, campaign);
        assertHasViolation("budget");
        assertViolationsCount(1);
    }
}
