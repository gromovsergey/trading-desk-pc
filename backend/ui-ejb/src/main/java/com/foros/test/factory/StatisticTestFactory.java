package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.model.creative.CreativeSize;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.security.AccountType;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.campaignAllocation.CampaignAllocationService;
import com.foros.session.campaignCredit.CampaignCreditAllocationService;
import com.foros.session.opportunity.OpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Set;

public class StatisticTestFactory {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private DisplayCreativeLinkTestFactory displayCreativeLinkTF;

    @Autowired
    private PublisherAccountTypeTestFactory publisherAccountTypeTF;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagsTF;

    @Autowired
    private CampaignCreditTestFactory campaignCreditTF;

    @Autowired
    private OpportunityTestFactory opportunityTF;

    @Autowired
    private OpportunityService opportunityService;

    @Autowired
    private CampaignAllocationService campaignAllocationService;

    @Autowired
    private CampaignCreditAllocationService campaignCreditAllocationService;

    /**
     * The query is described at
     *     https://confluence.ocslab.com/display/TDOC/Adserver+Stats+Emulation
     */
    public void generateRequestStatsHourly(long ccId, int imps, int clicks, int actions, int adv_amount, int adv_comm_amount) {
        // Functions test_functional.* return VOID,
        // so to prevent MappingException here
        // do wrap it into "select count(*) from (...) t"
        // Details at http://stackoverflow.com/questions/12557957/jpa-hibernate-call-postgres-function-void-return-mappingexception
        final String str = String.format(
                "select test_functional.saverequeststatshourly_begin(),\n" +
                "  test_functional.saverequeststatshourly_add_line(%d, %d, %d, %d, %d, %d),\n" +
                "  test_functional.saverequeststatshourly_finalize()\n",
                ccId, imps, clicks, actions, adv_amount, adv_comm_amount);
        System.out.println("[" + getClass().getSimpleName() + "] Executing:\n" + str);

        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Query query = entityManager.createNativeQuery("select count(*) from (" + str + ") t");
                query.getResultList();
                return null;
            }
        });
        entityManager.clear();
    }

    /**
     * To write unit tests with statistic, it needs full entities hierarchy, i.e.
     *   AccountType -> Account -> Campaign -> CCG -> CC -> ...
     * Moreover, as Statistic generation functions imply,
     *   the entities MUST BE available in new transactions.
     */

    /** Minimal set of mandatory Entities */
    public AdvertiserAccount createPermanentAdvertiserAccount(final BigDecimal prepaidAmount) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(Propagation.REQUIRES_NEW.value());

        // Advertiser AccountType -> Account -> Campaign -> Display CCG -> CC -> Creative
        // Creative Size and Template must be correctly set in the Account Type
        // (it may be Text CCG, but let's use more complex way)
        AdvertiserAccount advertiserAccount = (AdvertiserAccount) tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                AdvertiserAccount advertiserAccount = advertiserAccountTF.create();
                advertiserAccount.getFinancialSettings().getData().setPrepaidAmount(prepaidAmount);
                advertiserAccountTF.persist(advertiserAccount);
                return advertiserAccount;
            }
        });
        entityManager.clear();

        return advertiserAccount;
    }

    public CampaignCreative createPermanentDisplayCampaignCreative(final AdvertiserAccount advertiserAccount) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(Propagation.REQUIRES_NEW.value());

        // Campaign -> Display CCG -> CC -> Creative
        CampaignCreative campaignCreative = (CampaignCreative) tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Campaign campaign = displayCampaignTF.createPersistent(advertiserAccount);
                CampaignCreativeGroup ccg = displayCCGTF.createPersistent(campaign);
                CampaignCreative campaignCreative = displayCreativeLinkTF.createPersistent(ccg);
                return campaignCreative;
            }
        });
        entityManager.clear();

        final CreativeSize creativeSize = campaignCreative.getCreative().getSize();
        final CreativeTemplate creativeTemplate = campaignCreative.getCreative().getTemplate();

        // Statistic Generation NEEDS an appropriate Tag with Tag Pricing in the Campaign's Country
        // Publisher Account Type -> Site -> Tag -> TagPricing
        // Add the same Creative Size and Template to the Publisher Account Type
        Tag tag = (Tag) tt.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                AccountType accountType = publisherAccountTypeTF.createPersistent(creativeSize, creativeTemplate);
                PublisherAccount account = publisherAccountTF.createPersistent(accountType);
                Site site = siteTF.createPersistent(account);
                TagPricing tagPricing = tagsTF.createTagPricing(account.getCountry().getCountryCode(), BigDecimal.ONE);
                return tagsTF.createPersistent(site, tagPricing);
            }
        });
        entityManager.clear();

        return campaignCreative;
    }

    public Opportunity createPermanentOpportunity(final AdvertiserAccount advertiserAccount, final BigDecimal opportunityAmount) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(Propagation.REQUIRES_NEW.value());

        Opportunity opportunity = (Opportunity) tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Opportunity opportunity = opportunityTF.createPersistent(advertiserAccount, opportunityAmount);
                entityManager.clear();
                opportunity = opportunityService.find(opportunity.getId());
                return opportunity;
            }
        });
        entityManager.clear();

        return opportunity;
    }

    public CampaignAllocation createPermanentAllocation(final Campaign displayCampaign, final Opportunity opportunity, final int order, final BigDecimal allocationAmount) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(Propagation.REQUIRES_NEW.value());

        CampaignAllocation allocation = (CampaignAllocation)tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                CampaignAllocation allocation = new CampaignAllocation();
                allocation.setOpportunity(opportunity);
                allocation.setOrder((long)order);
                allocation.setAmount(allocationAmount);

                Campaign campaign = displayCampaignTF.find(displayCampaign.getId());
                Set<CampaignAllocation> allocations = campaign.getAllocations();
                allocations.add(allocation);
                campaign.setAllocations(allocations);

                campaignAllocationService.updateCampaignAllocations(campaign);
                return allocation;
            }
        });
        entityManager.clear();

        return allocation;
    }

    public CampaignCredit createPermanentCampaignCredit(final AdvertiserAccount advertiserAccount, final BigDecimal amount) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(Propagation.REQUIRES_NEW.value());

        CampaignCredit campaignCredit = (CampaignCredit)tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                CampaignCredit campaignCredit = campaignCreditTF.createPersistent(advertiserAccount, amount);
                return campaignCredit;
            }
        });
        entityManager.clear();

        return campaignCredit;
    }

    public CampaignCreditAllocation createPermanentCreditAllocation(final Campaign campaign, final CampaignCredit campaignCredit, final BigDecimal amount) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(Propagation.REQUIRES_NEW.value());

        CampaignCreditAllocation allocation = (CampaignCreditAllocation)tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                CampaignCreditAllocation allocation = new CampaignCreditAllocation();
                allocation.setCampaignCredit(campaignCredit);
                allocation.setCampaign(campaign);
                allocation.setAllocatedAmount(amount);

                Long id = campaignCreditAllocationService.create(allocation);
                allocation = campaignCreditAllocationService.find(id);
                return allocation;
            }
        });
        entityManager.clear();

        return allocation;
    }
}
