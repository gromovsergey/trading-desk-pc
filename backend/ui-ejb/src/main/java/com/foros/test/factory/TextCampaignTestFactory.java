package com.foros.test.factory;

import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignSchedule;
import com.foros.model.campaign.CampaignType;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignService;
import com.foros.util.DateUtil;
import com.foros.util.RandomUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.LinkedHashSet;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class TextCampaignTestFactory extends CampaignTestFactory {
    @EJB
    private CampaignService campaignService;

    @EJB
    private AccountService accountService;

    @EJB
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @EJB
    private UserTestFactory userTestHelper;

    @EJB
    private AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    public void populate(Campaign campaign) {
        AdvertiserAccount account = campaign.getAccount();

        campaign.setName(getTestEntityRandomName());
        campaign.setCampaignType(CampaignType.TEXT);

        User user;
        if (account.getAgency() != null) {
            user = userTestHelper.createPersistent(account.getAgency());
        } else {
            user = userTestHelper.createPersistent(account);
        }

        campaign.setSoldToUser(user);
        if (account.getAccountType().isPerCampaignInvoicingFlag()) {
            campaign.setBillToUser(user);
        }

        campaign.setBudget(BigDecimal.ONE);
        campaign.setCreativeGroups(new LinkedHashSet<CampaignCreativeGroup>());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        DateUtil.resetFields(calendar, Calendar.SECOND, Calendar.MILLISECOND);
        campaign.setDateStart(calendar.getTime());

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        DateUtil.resetFields(calendar, Calendar.SECOND, Calendar.MILLISECOND);
        campaign.setDateEnd(calendar.getTime());
        campaign.setCampaignSchedules(new LinkedHashSet<CampaignSchedule>());
    }

    public Campaign create(Long accountId, Long userId, Long campaignId) {
        Campaign campaign = new Campaign();
        campaign.setCampaignType(CampaignType.TEXT);

        if (campaignId != null) {
            campaign.setId(campaignId);
        }

        AdvertiserAccount account = accountService.findAdvertiserAccount(accountId);
        campaign.setAccount(account);

        campaign.setName(getTestEntityRandomName());

        User user = new User(userId);
        campaign.setSoldToUser(user);
        if (account.getAccountType().isPerCampaignInvoicingFlag()) {
            campaign.setBillToUser(user);
        }

        campaign.setBudget(RandomUtil.getRandomBigDecimal().setScale(2, RoundingMode.UP));

        Calendar calendar = Calendar.getInstance();
        DateUtil.resetFields(calendar, Calendar.SECOND, Calendar.MILLISECOND);
        campaign.setDateStart(calendar.getTime());

        return campaign;
    }

    @Override
    public Campaign create() {
        AccountType textAccountType = advertiserAccountTypeTF.createPersistent(CampaignType.TEXT);
        return create(textAccountType);
    }

    public Campaign create(AccountType accountType) {
        assert(accountType.isAllowTextAdvertisingFlag());
        AdvertiserAccount account = advertiserAccountTF.createPersistent(accountType);
        return create(account);
    }

    public Campaign create(AdvertiserAccount account) {
        Campaign campaign = new Campaign();
        campaign.setStatus(Status.ACTIVE);
        campaign.setDisplayStatus(Campaign.NO_ACTIVE_GROUPS);

        account.getAccountType().setAllowTextKeywordAdvertisingFlag(true);
        campaign.setAccount(account);

        populate(campaign);
        return campaign;
    }

    @Override
    public void persist(Campaign campaign) {
        campaignService.create(campaign);
        entityManager.flush();
        refresh(campaign); // todo find out who updates version of the creative
    }

    public void update(Campaign campaign) {
        campaignService.update(campaign);
    }

    @Override
    public Campaign createPersistent() {
        Campaign campaign = create();
        persist(campaign);
        return campaign;
    }

    public Campaign createPersistent(AccountType accountType) {
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent(accountType);
        return createPersistent(advertiserAccount);
    }

    public Campaign createPersistent(AdvertiserAccount account) {
        Campaign campaign = create(account);
        persist(campaign);
        return campaign;
    }

    public void delete(Long campaignId) {
        campaignService.delete(campaignId);
    }

    public void inactivate(Long campaignId) {
        campaignService.inactivate(campaignId);
    }
}
