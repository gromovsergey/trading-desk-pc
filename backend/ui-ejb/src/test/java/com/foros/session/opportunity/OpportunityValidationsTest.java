package com.foros.session.opportunity;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationStatus;
import com.foros.model.campaign.CampaignAllocationsTotalTO;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.test.factory.OpportunityTestFactory;

import com.foros.test.factory.StatisticTestFactory;
import com.foros.util.EntityUtils;
import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Category({ Db.class, Validation.class })
public class OpportunityValidationsTest extends AbstractValidationsTest {
    @Autowired
    private OpportunityTestFactory opportunityTF;

    @Autowired
    private StatisticTestFactory statisticTF;

    @Test
    public void testCreate() {
        Opportunity opportunity = opportunityTF.create();
        opportunity.setName(null);
        opportunity.setAmount(null);
        opportunity.setProbability(null);
        validate("Opportunity.create", opportunity, new HashMap<String, File>());
        assertHasViolation("name", "amount", "probability");

        opportunityTF.populate(opportunity);
        opportunity.setProbability(Probability.PROPOSAL_SENT);
        validate("Opportunity.create", opportunity, new HashMap<String, File>());

        opportunity.setProbability(Probability.IO_SIGNED);
        validate("Opportunity.create", opportunity, new HashMap<String, File>());
        assertHasViolation("ioFiles");

        opportunity.setProbability(Probability.AWAITING_GO_LIVE);
        validate("Opportunity.create", opportunity, new HashMap<String, File>());
        assertHasViolation("ioFiles");

        opportunity.setProbability(Probability.LIVE);
        opportunity.setIoNumber(null);
        validate("Opportunity.create", opportunity, new HashMap<String, File>());
        assertHasViolation("ioNumber", "ioFiles");

        Opportunity opportunityLost = opportunityTF.create();
        opportunityTF.populate(opportunityLost);
        opportunityLost.setProbability(Probability.LOST);
        validate("Opportunity.create", opportunity, new HashMap<String, File>());
    }

    @Test
    public void testUpdate() {
        Opportunity opportunity = opportunityTF.create();
        opportunityTF.populate(opportunity);
        opportunity.setProbability(Probability.PROPOSAL_SENT);
        opportunityTF.persist(opportunity);

        opportunity.setProbability(Probability.IO_SIGNED);
        validate("Opportunity.update", opportunity, new HashMap<String, File>());
        assertHasViolation("ioNumber", "ioFiles");

        opportunity = opportunityTF.create();
        opportunityTF.populate(opportunity);
        opportunity.setProbability(Probability.PROPOSAL_SENT);
        opportunityTF.persist(opportunity);

        opportunity.setProbability(Probability.TARGET);
        validate("Opportunity.update", opportunity, new HashMap<String, File>());
        assertHasNoViolation("probability");
    }

    @Test
    public void testMinAmount() {
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);

        // Create an opportunity with amount = 3
        Opportunity opportunity = statisticTF.createPermanentOpportunity(advertiserAccount, new BigDecimal(3));

        CampaignCreative displayCampaignCreative = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign = displayCampaignCreative.getCreativeGroup().getCampaign();

        // In one campaign add two campaign allocations for this opportunity using amounts 2 and 2.5
        // (i.e. their sum > opportunity's amount)
        statisticTF.createPermanentAllocation(campaign, opportunity, 1, new BigDecimal(2));
        statisticTF.createPermanentAllocation(campaign, opportunity, 2, new BigDecimal(2.5));

        opportunity.setAmount(new BigDecimal(2));
        validate("Opportunity.update", opportunity, new HashMap<String, File>());
        assertHasViolation("amount");

        opportunity.setAmount(new BigDecimal(3));
        validate("Opportunity.update", opportunity, new HashMap<String, File>());
        assertHasNoViolations();
    }
}
