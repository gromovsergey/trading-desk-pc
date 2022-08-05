package com.foros.test.factory;

import com.foros.model.account.Account;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditPurpose;
import com.foros.session.campaignCredit.CampaignCreditService;
import com.foros.util.RandomUtil;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.math.BigDecimal;

@Stateless
@LocalBean
public class CampaignCreditTestFactory extends TestFactory<CampaignCredit> {
    @EJB
    private CampaignCreditService campaignCreditService;

    @EJB
    private AgencyAccountTestFactory agencyAccountTestFactory;

    @Override
    public CampaignCredit create() {
        AgencyAccount account = agencyAccountTestFactory.createPersistent();
        return create(account);
    }

    public CampaignCredit create(Account account) {
        CampaignCredit campaignCredit = new CampaignCredit();
        campaignCredit.setAccount(account);
        campaignCredit.setPurpose(CampaignCreditPurpose.I);
        campaignCredit.setDescription(RandomUtil.getRandomString());
        campaignCredit.setAmount(BigDecimal.TEN);
        return campaignCredit;
    }

    @Override
    public void persist(CampaignCredit campaignCredit) {
        campaignCreditService.create(campaignCredit);
        entityManager.flush();
    }

    @Override
    public CampaignCredit createPersistent() {
        CampaignCredit campaignCredit = create();
        persist(campaignCredit);
        return campaignCredit;
    }

    public CampaignCredit createPersistent(Account account) {
        CampaignCredit campaignCredit = create(account);
        persist(campaignCredit);
        return campaignCredit;
    }

    public CampaignCredit createPersistent(Account account, BigDecimal amount) {
        CampaignCredit campaignCredit = create(account);
        campaignCredit.setAmount(amount);
        persist(campaignCredit);
        return campaignCredit;
    }

    public void update(CampaignCredit campaignCredit) {
        campaignCreditService.update(campaignCredit);
        entityManager.flush();
    }
}
