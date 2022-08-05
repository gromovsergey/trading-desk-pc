package com.foros.session.campaign;

import com.foros.test.UserDefinition;
import com.foros.test.UserDefinitionFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.security.PolicyEntry;
import com.foros.security.AccountRole;
import com.foros.session.account.AccountService;
import com.foros.test.factory.DisplayCampaignTestFactory;

import com.foros.validation.ValidationContext;
import java.math.BigDecimal;

import group.Db;
import group.Restriction;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class CampaignCreativeGroupRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @Autowired
    private CreativeGroupRestrictions creativeGroupRestrictions;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignCreativeGroupService ccgService;

    private CampaignCreativeGroup ccg;

    private UserDefinition advertiserAllAccessLocal;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();

        advertiserAllAccessLocal = userDefinitionFactory.create(AccountRole.ADVERTISER, PermissionsSet.ALL).managedBy(
                advertiserManagerAllAccess1);
        advertiserAllAccessLocal.addCcgType(CCGType.DISPLAY, TGTType.KEYWORD, RateType.CPA);
        advertiserAllAccessLocal.addCcgType(CCGType.DISPLAY, TGTType.CHANNEL, RateType.CPA);

        AdvertiserAccount account = (AdvertiserAccount) advertiserAllAccessLocal.getUser().getAccount();
        ccg = createCCG(account);
        expectResult(advertiserAllAccessLocal, true);
    }

    @Test
    public void testCanView() {
        Callable callCanView = new Callable("advertiser_entity", "view") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canView(ccg);
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
                return advertiserEntityRestrictions.canCreate(ccg.getCampaign());
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
                return advertiserEntityRestrictions.canUpdate(ccg);
            }
        };
        Callable callCanUpdateChannelTarget = new Callable("advertiser_entity", "edit") {
            @Override
            public boolean call() {
                return creativeGroupRestrictions.canUpdateChannelTarget(ccg);
            }
        };

        doCheck(callCanUpdate);
        doCheck(callCanUpdateChannelTarget);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanUpdate);
    }

    @Test
    public void testCanActivate() {
        ccgService.inactivate(ccg.getId()); // Added for now. See OUI-25720

        Callable callCanActivate = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canActivate(context, ccg);
            }
        };

        doCheck(callCanActivate);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanActivate);

        //check activate from pending
        callCanActivate = new ContextCall() {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canActivate(context, ccg);
            }
        };

        accountService.activate(ccg.getAccount().getId());
        // Commented for now. See OUI-25720
//        ccgService.activate(ccg.getId());//to pending
//        assertEquals("for test consistence ccg status must be PENDING here", ccg.getStatus(), Status.PENDING);

        UserDefinition internalWithActivatePermission = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("advertiser_entity", "edit")).addCustomPermission(
                        new PolicyEntry("advertiser_entity", "activate"));
        UserDefinition internalWithoutActivatePermission = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("advertiser_entity", "edit"));

        resetExpectationsToDefault();
        expectResult(internalWithActivatePermission, true);
        expectResult(internalWithoutActivatePermission, false);
        expectResult(advertiserAllAccessLocal, true);//only owner can do that
        doCheck(callCanActivate);

    }

    @Test
    public void testCanInactivate() {
        Callable callCanInactivate = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canInactivate(context, ccg);
            }
        };

        setUpAllExpectations(true);
        doCheck(callCanInactivate);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanInactivate);
    }

    @Test
    public void testCanDelete() {
        Callable callCanDelete = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canDelete(context, ccg);
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
                advertiserEntityRestrictions.canUndelete(context, ccg);
            }
        };

        setUpAllExpectations(false);
        doCheck(callCanUndelete);

        ccgService.delete(ccg.getId());
        setUpExpectationsTrueForInternalsOnly();
        doCheck(callCanUndelete);

        ccgService.undelete(ccg.getId());
        setUpAllExpectations(false);
        runTestWithDeletedParents(callCanUndelete);
    }

    @Test
    public void testCanApprove() {
        Callable callCanApprove = new ContextCall("advertiser_entity", "approve") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canApprove(context, ccg);
            }
        };

        setUpAllExpectations(false);
        doCheck(callCanApprove);

        ccgService.decline(ccg.getId(), "test reason");
        setUpExpectationsTrueForInternalsOnly();
        doCheck(callCanApprove);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanApprove);
    }

    @Test
    public void testCanDecline() {
        Callable callCanDecline = new ContextCall("advertiser_entity", "approve") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canDecline(context, ccg);
            }
        };

        setUpExpectationsTrueForInternalsOnly();
        doCheck(callCanDecline);

        ccgService.decline(ccg.getId(), "test reason");
        setUpAllExpectations(false);
        doCheck(callCanDecline);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanDecline);
    }

    @Test
    public void testCanApproveChildren() {
        Callable callCanApproveChildren = new ContextCall("advertiser_entity", "approve") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canApproveChildren(context, ccg);
            }
        };

        setUpExpectationsTrueForInternalsOnly();
        doCheck(callCanApproveChildren);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanApproveChildren);
    }

    @Test
    public void testCanUndeleteChildren() {
        Callable callCanUndeleteChildren = new ContextCall("advertiser_entity", "undelete") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canUndeleteChildren(context, ccg);
            }
        };

        setUpExpectationsTrueForInternalsOnly();
        doCheck(callCanUndeleteChildren);

        setUpAllExpectations(false);
        runTestWithDeletedEntityOrParents(callCanUndeleteChildren);
    }

    private void runTestWithDeletedEntityOrParents(Callable call) {
        ccgService.delete(ccg.getId());
        doCheck(call);
        ccgService.undelete(ccg.getId());

        runTestWithDeletedParents(call);
    }

    private void runTestWithDeletedParents(Callable call) {
        campaignService.delete(ccg.getCampaign().getId());
        doCheck(call);
        campaignService.undelete(ccg.getCampaign().getId());

        accountService.delete(ccg.getAccount().getId());
        doCheck(call);
        accountService.undelete(ccg.getAccount().getId());
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

    private CampaignCreativeGroup createCCG(AdvertiserAccount account) {
        Campaign campaign = displayCampaignTF.createPersistent(account);

        CampaignCreativeGroup displayCcg = displayCCGTF.create(campaign);
        displayCcg.setDateStart(campaign.getDateStart());
        displayCcg.setTgtType(TGTType.CHANNEL);
        CcgRate ccgRate = displayCCGTF.createCcgRate(displayCcg, RateType.CPA, BigDecimal.valueOf(1000));
        displayCcg.setCcgRate(ccgRate);
        displayCCGTF.persist(displayCcg);

        return displayCcg;
    }
}
