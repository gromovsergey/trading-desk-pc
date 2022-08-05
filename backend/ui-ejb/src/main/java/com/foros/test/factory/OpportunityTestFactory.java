package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignType;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.model.security.AccountType;
import com.foros.session.opportunity.OpportunityService;
import com.foros.util.RandomUtil;

import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class OpportunityTestFactory extends TestFactory<Opportunity>{

    @EJB
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @EJB
    private AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    @EJB
    private OpportunityService opportunityService;

    public void populate(Opportunity opportunity) {
        opportunity.setName(getTestEntityRandomName());
        opportunity.setAmount(BigDecimal.ONE);
        opportunity.setNotes(RandomUtil.getRandomString());
        opportunity.setProbability(Probability.LIVE);
        opportunity.setIoNumber(getTestEntityRandomName());
    }

    @Override
    public Opportunity create() {
        AccountType accountType = advertiserAccountTypeTF.createPersistent(CampaignType.DISPLAY);
        AdvertiserAccount account = advertiserAccountTF.createPersistent(accountType);
        return create(account, BigDecimal.ONE);
    }

    public Opportunity create(AdvertiserAccount account, BigDecimal amount) {
        Opportunity opportunity = new Opportunity();
        populate(opportunity);
        opportunity.setAccount(account);
        opportunity.setAmount(amount);
        return opportunity;
    }

    @Override
    public Opportunity createPersistent() {
        Opportunity opportunity = create();
        persist(opportunity);
        return opportunity;
    }

    public Opportunity createPersistent(AdvertiserAccount account) {
        return createPersistent(account, BigDecimal.ONE);
    }

    public Opportunity createPersistent(AdvertiserAccount account, BigDecimal amount) {
        Opportunity opportunity = create(account, amount);
        persist(opportunity);
        return opportunity;
    }

    @Override
    public void persist(Opportunity opportunity) {
        opportunityService.create(opportunity, null);
        entityManager.flush();
    }

    @Override
    public void update(Opportunity opportunity) {
        opportunityService.update(opportunity, null);
        entityManager.flush();
    }
}
