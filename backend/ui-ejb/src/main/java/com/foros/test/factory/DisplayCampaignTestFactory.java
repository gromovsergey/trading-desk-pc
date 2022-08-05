package com.foros.test.factory;

import static com.foros.util.RandomUtil.getRandomBigDecimal;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignSchedule;
import com.foros.model.campaign.CampaignType;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;
import com.foros.session.campaign.CampaignService;
import com.foros.util.DateUtil;
import com.foros.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class DisplayCampaignTestFactory extends CampaignTestFactory {
    @EJB
    private CampaignService campaignService;

    @EJB
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @EJB
    private UserTestFactory userTF;

    @EJB
    private AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    @Autowired
    private CountryTestFactory countryTF;

    public void populate(Campaign campaign) {
        campaign.setName(getTestEntityRandomName());
        campaign.setCampaignType(CampaignType.DISPLAY);

        User user;
        if (campaign.getAccount().getAgency() != null) {
            user = userTF.createPersistent(campaign.getAccount().getAgency());
        } else {
            user = userTF.createPersistent(campaign.getAccount());
        }
        campaign.setBillToUser(user);
        campaign.setSoldToUser(user);

        campaign.setBudget(BigDecimal.ONE);
        campaign.setCreativeGroups(new LinkedHashSet<CampaignCreativeGroup>());
        campaign.setCampaignSchedules(new LinkedHashSet<CampaignSchedule>());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        DateUtil.resetFields(calendar, Calendar.SECOND, Calendar.MILLISECOND);
        campaign.setDateStart(calendar.getTime());

        countryTF.persistCtrAlgorithm("US");
    }

    @Override
    public Campaign create() {
        AccountType displayAccountType = advertiserAccountTypeTF.createPersistent(CampaignType.DISPLAY);
        return create(displayAccountType);
    }

    public Campaign create(AccountType accountType) {
        AdvertiserAccount account = advertiserAccountTF.createPersistent(accountType);
        return create(account);
    }

    public Campaign create(AdvertiserAccount account) {
        Campaign campaign = new Campaign();
        campaign.setStatus(Status.ACTIVE);
        campaign.setDisplayStatus(Campaign.NO_ACTIVE_GROUPS);

        account.getAccountType().setAllowTextKeywordAdvertisingFlag(false);
        campaign.setAccount(account);

        populate(campaign);
        return campaign;
    }

    public Campaign createLiveCampaign(AdvertiserAccount advertiserAccount, User user) {
        return createLiveCampaign(advertiserAccount, user, Calendar.getInstance());
    }

    public Campaign createLiveCampaign(AdvertiserAccount advertiserAccount, User user, Calendar dateStart) {
        Campaign campaign = new Campaign();
        campaign.setStatus(Status.ACTIVE);
        campaign.setDisplayStatus(Campaign.LIVE);
        campaign.setName(getTestEntityRandomName());
        campaign.setCampaignType(CampaignType.DISPLAY);
        campaign.setBudget(getRandomBigDecimal());
        campaign.setBillToUser(user);
        campaign.setSoldToUser(user);
        campaign.setAccount(advertiserAccount);

        DateUtil.resetFields(dateStart, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
        campaign.setDateStart(dateStart.getTime());

        return campaign;
    }

    public Campaign create(Long accountId, Long userId, Long campaignId) {
        Campaign campaign = new Campaign();
        campaign.setCampaignType(CampaignType.DISPLAY);

        if (campaignId != null) {
            campaign.setId(campaignId);
        }

        campaign.setAccount(new AdvertiserAccount(accountId));

        campaign.setName(getTestEntityRandomName());

        User user = new User(userId);
        campaign.setSoldToUser(user);
        campaign.setBillToUser(user);

        campaign.setBudget(RandomUtil.getRandomBigDecimal());
        campaign.setDateStart(new Date(System.currentTimeMillis()));

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
        entityManager.flush();
    }

    @Override
    public Campaign createPersistent() {
        Campaign campaign = create();
        persist(campaign);
        return campaign;
    }

    public Campaign createPersistent(AccountType accountType) {
        Campaign campaign = create(accountType);
        persist(campaign);
        return campaign;
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

    public Campaign find(Long id) {
        return campaignService.find(id);
    }
}
