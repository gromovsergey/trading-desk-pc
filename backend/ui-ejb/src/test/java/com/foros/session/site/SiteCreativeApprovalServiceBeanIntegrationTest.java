package com.foros.session.site;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AdvExclusionsType;
import com.foros.model.security.User;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.CreativeRejectReason;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCreativeApproval;
import com.foros.model.site.SiteCreativeApprovalStatus;
import com.foros.model.site.SiteCreativePK;
import com.foros.model.template.CreativeTemplate;
import com.foros.service.mock.AdvertisingFinanceServiceMock;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.query.PartialList;
import com.foros.session.reporting.dashboard.AccountDashboardParameters;
import com.foros.session.reporting.dashboard.PublisherDashboardService;
import com.foros.session.reporting.dashboard.SiteDashboardTO;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.session.site.creativeApproval.CreativeExclusionBySiteSelector;
import com.foros.session.site.creativeApproval.CreativeSiteApprovalTO;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalOperation;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalOperationType;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalOperations;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalService;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalTO;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.AgencyAccountTypeTestFactory;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeLinkTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.PublisherAccountTypeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;

import group.Db;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class SiteCreativeApprovalServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private SiteCreativeApprovalService siteCreativeApprovalService;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private DisplayCreativeTestFactory creativeTF;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagsTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private DisplayCreativeLinkTestFactory creativeLinkTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private AgencyAccountTypeTestFactory agencyTypeTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private PublisherAccountTypeTestFactory publisherTypeTF;


    @Autowired
    private AdvertisingFinanceServiceMock advertisingFinanceService;

    @Autowired
    private PublisherDashboardService publisherDashboardService;

    private CreativeTemplate creativeTemplate;
    private CreativeSize size;

    private AdvertiserAccount advertiserAccount;

    private PublisherAccount publisherAccount;
    private CreativeCategory creativeCategoryContent;
    private CreativeCategory creativeCategoryContent2;
    private CreativeCategory creativeCategoryVisual;
    private Site site;

    @Before
    public void createTestEntities() throws Exception {
        creativeCategoryContent = creativeCategoryTF.createPersistent(CreativeCategoryType.CONTENT, ApproveStatus.APPROVED);
        creativeCategoryContent2 = creativeCategoryTF.createPersistent(CreativeCategoryType.CONTENT, ApproveStatus.APPROVED);
        creativeCategoryVisual = creativeCategoryTF.createPersistent(CreativeCategoryType.VISUAL, ApproveStatus.APPROVED);

        creativeCategoryTF.populateRtb(creativeCategoryContent, 0);
        creativeCategoryTF.populateRtb(creativeCategoryContent, 1);
        creativeCategoryTF.update(creativeCategoryContent);

        creativeTemplate = creativeTemplateTF.createPersistent();
        size = creativeSizeTF.createPersistent();

        AccountType agencyType = agencyTypeTF.create(size, creativeTemplate);
        agencyType.setCPMFlag(CCGType.DISPLAY, true);
        agencyTypeTF.persist(agencyType);

        AccountType publisherType = publisherTypeTF.create(size, null);
        publisherType.setAdvExclusions(AdvExclusionsType.SITE_AND_TAG_LEVELS);
        publisherTypeTF.persist(publisherType);

        AgencyAccount agencyAccount = agencyAccountTF.createPersistent(agencyType);
        getEntityManager().clear();

        advertiserAccount = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        publisherAccount = publisherAccountTF.createPersistent(publisherType);
        commitChanges();

        AdvertisingFinancialSettings settings = advertiserAccount.getAgency().getFinancialSettings();
        settings.getData().setPrepaidAmount(new BigDecimal(4.1));
        advertisingFinanceService.updateFinance(settings);
        commitChanges();

        site = prepareSite();
    }

    @Test
    public void testUpdate() {
        prepareLiveCampaign();

        // initial
        PartialList<SiteCreativeApprovalTO> search = siteCreativeApprovalService.searchCreativeApprovals(new CreativeExclusionBySiteSelector(site.getId()));
        SiteCreativeApprovalTO creative0 = search.get(0);
        SiteCreativeApprovalTO creative1 = search.get(1);

        assertEquals(SiteCreativeApprovalStatus.PENDING, creative0.getApprovalStatus());
        assertEquals(SiteCreativeApprovalStatus.CREATIVE_CATEGORY_APPROVED, creative1.getApprovalStatus());

        // approve/reject
        siteCreativeApprovalService.update(site.getId(), approve(creative0));
        siteCreativeApprovalService.update(site.getId(), reject(creative1));

        commitChanges();
        clearContext();

        search = siteCreativeApprovalService.searchCreativeApprovals(new CreativeExclusionBySiteSelector(site.getId()));
        creative0 = search.get(0);
        creative1 = search.get(1);

        assertEquals(SiteCreativeApprovalStatus.APPROVED, creative0.getApprovalStatus());
        assertEquals(SiteCreativeApprovalStatus.REJECTED, creative1.getApprovalStatus());


        // approve/reject reverse
        siteCreativeApprovalService.update(site.getId(), reject(creative0));
        siteCreativeApprovalService.update(site.getId(), approve(creative1));

        commitChanges();
        clearContext();

        search = siteCreativeApprovalService.searchCreativeApprovals(new CreativeExclusionBySiteSelector(site.getId()));
        creative0 = search.get(0);
        creative1 = search.get(1);

        assertEquals(SiteCreativeApprovalStatus.REJECTED, creative0.getApprovalStatus());
        assertEquals(SiteCreativeApprovalStatus.APPROVED, creative1.getApprovalStatus());

        assertNotNull(getEntityManager().find(SiteCreativeApproval.class, new SiteCreativePK(creative0.getCreative().getId(), site.getId())));
        siteCreativeApprovalService.update(site.getId(), reset(creative0));
        commitChanges();
        clearContext();
        assertNull(getEntityManager().find(SiteCreativeApproval.class, new SiteCreativePK(creative0.getCreative().getId(), site.getId())));
    }

    private SiteCreativeApprovalOperation reset(SiteCreativeApprovalTO creative) {
        SiteCreativeApprovalOperation operation = new SiteCreativeApprovalOperation();
        operation.setCreativeId(creative.getCreative().getId());
        operation.setType(SiteCreativeApprovalOperationType.RESET);
        return operation;
    }

    @Test
    public void testPerform() {
        prepareLiveCampaign();

        PartialList<SiteCreativeApprovalTO> search = siteCreativeApprovalService.searchCreativeApprovals(new CreativeExclusionBySiteSelector(site.getId()));
        SiteCreativeApprovalTO creative0 = search.get(0);
        SiteCreativeApprovalTO creative1 = search.get(1);

        assertEquals(SiteCreativeApprovalStatus.PENDING, creative0.getApprovalStatus());
        assertEquals(SiteCreativeApprovalStatus.CREATIVE_CATEGORY_APPROVED, creative1.getApprovalStatus());

        SiteCreativeApprovalOperations operations = new SiteCreativeApprovalOperations();
        operations.setSite(new IdNameTO(site.getId(), null));
        operations.getOperations().add(approve(creative0));
        operations.getOperations().add(reject(creative1));
        siteCreativeApprovalService.perform(operations);

        commitChanges();

        search = siteCreativeApprovalService.searchCreativeApprovals(new CreativeExclusionBySiteSelector(site.getId()));
        creative0 = search.get(0);
        creative1 = search.get(1);

        assertEquals(SiteCreativeApprovalStatus.APPROVED, creative0.getApprovalStatus());
        assertEquals(SiteCreativeApprovalStatus.REJECTED, creative1.getApprovalStatus());
    }

    @Test
    public void testSitesByCreative() {
        prepareLiveCampaign();

        // initial
        PartialList<SiteCreativeApprovalTO> search = siteCreativeApprovalService.searchCreativeApprovals(new CreativeExclusionBySiteSelector(site.getId()));
        SiteCreativeApprovalTO creative0 = search.get(0);
        SiteCreativeApprovalTO creative1 = search.get(1);

        List<CreativeSiteApprovalTO> sitesByCreative0;
        List<CreativeSiteApprovalTO> sitesByCreative1;

        assertEquals(SiteCreativeApprovalStatus.PENDING, creative0.getApprovalStatus());
        sitesByCreative0 = siteCreativeApprovalService.sitesByCreative(creative0.getCreative().getId()).asList();
        assertEquals(1, sitesByCreative0.size());

        assertEquals(SiteCreativeApprovalStatus.CREATIVE_CATEGORY_APPROVED, creative1.getApprovalStatus());
        sitesByCreative1 = siteCreativeApprovalService.sitesByCreative(creative1.getCreative().getId()).asList();
        assertEquals(0, sitesByCreative1.size());

        // reject all
        siteCreativeApprovalService.update(site.getId(), reject(creative0));
        siteCreativeApprovalService.update(site.getId(), reject(creative1));

        commitChanges();
        clearContext();

        search = siteCreativeApprovalService.searchCreativeApprovals(new CreativeExclusionBySiteSelector(site.getId()));
        creative0 = search.get(0);
        creative1 = search.get(1);

        assertEquals(SiteCreativeApprovalStatus.REJECTED, creative0.getApprovalStatus());
        sitesByCreative0 = siteCreativeApprovalService.sitesByCreative(creative0.getCreative().getId()).asList();
        assertEquals(1, sitesByCreative0.size());

        assertEquals(SiteCreativeApprovalStatus.REJECTED, creative1.getApprovalStatus());
        sitesByCreative1 = siteCreativeApprovalService.sitesByCreative(creative1.getCreative().getId()).asList();
        assertEquals(1, sitesByCreative1.size());

        // approve all
        siteCreativeApprovalService.update(site.getId(), approve(creative0));
        siteCreativeApprovalService.update(site.getId(), approve(creative1));

        commitChanges();
        clearContext();

        search = siteCreativeApprovalService.searchCreativeApprovals(new CreativeExclusionBySiteSelector(site.getId()));
        creative0 = search.get(0);
        creative1 = search.get(1);

        assertEquals(SiteCreativeApprovalStatus.APPROVED, creative0.getApprovalStatus());
        sitesByCreative0 = siteCreativeApprovalService.sitesByCreative(creative0.getCreative().getId()).asList();
        assertEquals(1, sitesByCreative0.size());

        assertEquals(SiteCreativeApprovalStatus.APPROVED, creative1.getApprovalStatus());
        sitesByCreative1 = siteCreativeApprovalService.sitesByCreative(creative1.getCreative().getId()).asList();
        assertEquals(1, sitesByCreative1.size());
    }

    @Test
    public void testCountSearchResult() {
        prepareLiveCampaign();

        AccountDashboardParameters parameters = new AccountDashboardParameters();
        parameters.setDateRange(new DateRange(new LocalDate(), new LocalDate()));
        parameters.setAccountId(publisherAccount.getId());
        List<SiteDashboardTO> list = publisherDashboardService.generateSiteDashboard(parameters);
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).getCreativesToApprove());

        CreativeExclusionBySiteSelector selector = new CreativeExclusionBySiteSelector(site.getId());
        selector.setApprovals(Collections.singleton(SiteCreativeApprovalStatus.PENDING));
        PartialList<SiteCreativeApprovalTO> search = siteCreativeApprovalService.searchCreativeApprovals(selector);
        assertNotNull(search);
        assertEquals(1, search.size());

        siteCreativeApprovalService.update(site.getId(), reject(search.get(0)));

        commitChanges();
        clearContext();

        list = publisherDashboardService.generateSiteDashboard(parameters);
        assertEquals(1, list.size());
        assertEquals(0, list.get(0).getCreativesToApprove());
    }

    private SiteCreativeApprovalOperation reject(SiteCreativeApprovalTO creative) {
        SiteCreativeApprovalOperation operation = new SiteCreativeApprovalOperation();
        operation.setCreativeId(creative.getCreative().getId());
        operation.setType(SiteCreativeApprovalOperationType.REJECT);
        operation.setRejectReason(CreativeRejectReason.CREATIVE_IS_ALREADY_SERVED);
        operation.setFeedback("test feedback");
        operation.setVersion(creative.getVersion());
        return operation;
    }

    private SiteCreativeApprovalOperation approve(SiteCreativeApprovalTO creative) {
        SiteCreativeApprovalOperation operation = new SiteCreativeApprovalOperation();
        operation.setCreativeId(creative.getCreative().getId());
        operation.setType(SiteCreativeApprovalOperationType.APPROVE);
        operation.setVersion(creative.getVersion());
        return operation;
    }


    private Site prepareSite() {
        Site site = siteTF.create(publisherAccount);
        siteTF.addCategoryExclusion(site, creativeCategoryContent, CategoryExclusionApproval.APPROVAL);
        siteTF.addCategoryExclusion(site, creativeCategoryContent2, CategoryExclusionApproval.ACCEPT);
        siteTF.addCategoryExclusion(site, creativeCategoryVisual, CategoryExclusionApproval.ACCEPT);
        siteTF.persist(site);

        tagsTF.createPersistent(site, size);

        return site;
    }


    private Campaign prepareLiveCampaign() {
        User user = advertiserAccount.getAgency().getUsers().iterator().next();
        Campaign campaign = displayCampaignTF.createLiveCampaign(advertiserAccount, user);
        CampaignCreativeGroup ccg = displayCCGTF.prepareLiveDisplayCampaignCreativeGroup(advertiserAccount, campaign);
        entityManager.persist(campaign);
        entityManager.persist(ccg);

        // will be pending
        addLiveCreative(ccg, creativeCategoryContent);
        // will be approved on category level
        addLiveCreative(ccg, creativeCategoryContent2);

        commitChanges();

        return campaign;
    }

    private void addLiveCreative(CampaignCreativeGroup campaignCreativeGroup, CreativeCategory category) {
        Creative creative = creativeTF.prepareLiveCreative(advertiserAccount, creativeTemplate, size);
        creative.setCategories(Collections.singleton(category));
        CampaignCreative campaignCreative = creativeLinkTF.create(creative);

        campaignCreativeGroup.getCampaignCreatives().add(campaignCreative);
        campaignCreative.setCreativeGroup(campaignCreativeGroup);
        entityManager.persist(creative);
        entityManager.persist(campaignCreative);
    }
}
