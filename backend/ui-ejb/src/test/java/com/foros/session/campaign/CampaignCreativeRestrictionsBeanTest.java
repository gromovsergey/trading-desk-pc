package com.foros.session.campaign;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.security.AccountRole;
import com.foros.session.account.AccountService;
import com.foros.test.UserDefinition;
import com.foros.test.UserDefinitionFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.validation.ValidationContext;

import group.Db;
import group.Restriction;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class CampaignCreativeRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignCreativeGroupService ccgService;

    @Autowired
    private CampaignCreativeService ccService;

    @Autowired
    private DisplayCreativeTestFactory creativeTF;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    private UserDefinition advertiserAllAccessLocal;

    private CampaignCreative campaignCreative;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();

        final CreativeTemplate template = creativeTemplateTF.createPersistent();
        CreativeSize size = creativeSizeTF.createPersistent();

        advertiserAllAccessLocal = userDefinitionFactory.create(AccountRole.ADVERTISER, PermissionsSet.ALL).managedBy(advertiserManagerAllAccess1);
        advertiserAllAccessLocal.setCreativeTemplate(template);
        advertiserAllAccessLocal.setCreativeSize(size);
        advertiserAllAccessLocal.addCcgType(CCGType.DISPLAY, TGTType.CHANNEL, RateType.CPA);

        AdvertiserAccount account = (AdvertiserAccount) advertiserAllAccessLocal.getUser().getAccount();
        Creative creative = createCreative(account, template, size);
        campaignCreative = createCC(account, creative);
        expectResult(advertiserAllAccessLocal, true);
    }

    public void setUpDefaultExpectations() {
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
    }

    @Test
    public void testCanView() {
        Callable callCanView = new Callable("advertiser_entity", "view") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canView(campaignCreative);
            }
        };
        doCheck(callCanView);

        setUpExpectationsTrueForInternalsOnly();
        runTestWithDeletedEntityOrParents(callCanView);
    }

    @Test
    public void testCanCreate() {
        Callable callCanCreate = new Callable("advertiser_entity", "create") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canCreate(campaignCreative.getCreativeGroup());
            }
        };
        doCheck(callCanCreate);

        setUpAllExpectations(false);
        runTestWithDeletedParents(callCanCreate);
    }

    @Test
    public void testCanUpdate() {
        Callable callCanUpdate = new Callable("advertiser_entity", "edit") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canUpdate(campaignCreative);
            }
        };
        doCheck(callCanUpdate);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanUpdate);
    }

    @Test
    public void testCanActivate() {
        Callable callCanActivate = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canActivate(context, campaignCreative);
            }
        };
        setUpAllExpectations(false);
        doCheck(callCanActivate);

        ccService.inactivate(campaignCreative.getId());
        setUpAllExpectations(true);
        doCheck(callCanActivate);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanActivate);
    }

    @Test
    public void testCanInactivate() {
        Callable callCanInactivate = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canInactivate(context, campaignCreative);
            }
        };
        doCheck(callCanInactivate);

        ccService.inactivate(campaignCreative.getId());
        setUpAllExpectations(false);
        doCheck(callCanInactivate);

        runTestWithDeletedEntityOrParents(callCanInactivate);
    }

    @Test
    public void testCanDelete() {
        Callable callCanDelete = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canDelete(context, campaignCreative);
            }
        };
        doCheck(callCanDelete);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanDelete);
    }

    @Test
    public void testCanUndelete() {
        Callable callCanUndelete = new ContextCall("advertiser_entity", "undelete") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canUndelete(context, campaignCreative);
            }
        };
        setUpAllExpectations(false);
        doCheck(callCanUndelete);

        ccService.delete(campaignCreative.getId());
        setUpExpectationsTrueForInternalsOnly();
        doCheck(callCanUndelete);

        ccService.undelete(campaignCreative.getId());
        setUpAllExpectations(false);
        runTestWithDeletedParents(callCanUndelete);
    }

    private void runTestWithDeletedEntityOrParents(Callable call) {
        ccService.delete(campaignCreative.getId());
        doCheck(call);
        ccService.undelete(campaignCreative.getId());

        runTestWithDeletedParents(call);
    }

    private void runTestWithDeletedParents(Callable call) {
        ccgService.delete(campaignCreative.getCreativeGroup().getId());
        doCheck(call);
        ccgService.undelete(campaignCreative.getCreativeGroup().getId());

        campaignService.delete(campaignCreative.getCreativeGroup().getCampaign().getId());
        doCheck(call);
        campaignService.undelete(campaignCreative.getCreativeGroup().getCampaign().getId());

        accountService.delete(campaignCreative.getCreativeGroup().getAccount().getId());
        doCheck(call);
        accountService.undelete(campaignCreative.getCreativeGroup().getAccount().getId());
    }

    private CampaignCreative createCC(AdvertiserAccount account, Creative creative) {
        Campaign campaign = displayCampaignTF.createPersistent(account);

        CampaignCreativeGroup ccg = displayCCGTF.create(campaign);
        ccg.setTgtType(TGTType.CHANNEL);
        CcgRate ccgRate = displayCCGTF.createCcgRate(ccg, RateType.CPA, BigDecimal.valueOf(10));
        ccg.setCcgRate(ccgRate);
        ccg.setBudget(BigDecimal.valueOf(10000));
        ccg.setDateStart(campaign.getDateStart());
        displayCCGTF.persist(ccg);

        CampaignCreative cc = new CampaignCreative();
        cc.setCreative(creative);
        cc.setCreativeGroup(ccg);
        ccService.create(cc);

        return cc;
    }

    private Creative createCreative(AdvertiserAccount account, CreativeTemplate template, CreativeSize size) {
        Creative creative = creativeTF.create(account, template, size);
        creativeTF.persist(creative);
        return creative;
    }

    private void setUpAllExpectations(boolean result) {
        expectResult(internalAllAccess, result);
        expectResult(advertiserManagerAllAccess1, result);
        expectResult(advertiserAllAccessLocal, result);
    }

    private void setUpExpectationsTrueForInternalsOnly() {
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserAllAccessLocal, false);
    }
}
