package com.foros.session.campaign;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.Campaign;
import com.foros.session.account.AccountServiceBean;
import com.foros.test.factory.TextCampaignTestFactory;

import com.foros.validation.ValidationContext;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class CampaignRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @Autowired
    private TextCampaignTestFactory campaignTF;

    @Autowired
    private AccountServiceBean accountServiceBean;

    private Campaign campaign;
    private Campaign advertiserCampaign;
    private Campaign agencyCampaign;

    private AdvertisingAccountBase account;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        advertiserCampaign = campaignTF.createPersistent((AdvertiserAccount) advertiserAllAccess1.getUser().getAccount());
        agencyCampaign = campaignTF.createPersistent(agencyAdvertiser1.getAccount());
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(ispAllAccess1, false);
        expectResult(advertiserAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyAllAccess2, false);
    }

    @Test
    public void testCanView() throws Exception {
        Callable callCanView = new Callable("advertiser_entity", "view") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canView(campaign);
            }
        };

        checkAccessRules(callCanView);

        campaignTF.delete(campaign.getId());

        expectResult(advertiserAllAccess1, false);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("advertiser_entity", "edit") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canUpdate(campaign);
            }
        };

        checkAccessRules(callCanUpdate);

        campaignTF.delete(campaign.getId());

        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanInactivate() throws Exception {
        Callable callCanInactivate = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canInactivate(context, campaign);
            }
        };

        checkAccessRules(callCanInactivate);

        campaignTF.delete(campaign.getId());

        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(callCanInactivate);
    }

    @Test
    public void testCanActivate() throws Exception {
        Callable callCanActivate = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                campaign.setStatus(Status.INACTIVE);
                advertiserEntityRestrictions.canActivate(context, campaign);
            }
        };
        checkAccessRules(callCanActivate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("advertiser_entity", "create") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canCreate(account);
            }
        };

        checkAccessRules(callCanCreate);

        accountServiceBean.delete(account.getId());

        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanUndelete() throws Exception {
        Callable callCanUndelete = new ContextCall("advertiser_entity", "undelete") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canUndelete(context, advertiserCampaign);
            }
        };

        // normal campaign
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(callCanUndelete);

        // deleted campaign
        campaignTF.delete(advertiserCampaign.getId());

        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);

        doCheck(callCanUndelete);
    }

    @Test
    public void testCanDelete() throws Exception {
        Callable callCanDelete = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canDelete(context, campaign);
            }
        };

        checkAccessRules(callCanDelete);

        campaignTF.delete(campaign.getId());

        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(callCanDelete);
    }

    private void checkAccessRules(Callable callable) throws Exception {
        // agency access
        resetExpectationsToDefault();

        campaign = agencyCampaign;
        account = agencyCampaign.getAccount();

        expectResult(agencyAllAccess1, true);
        expectResult(internalUserAccountNoAccess, false);
        expectResult(internalMultipleAccountsAccess, true);
        expectResult(internalMultipleAccountsNoAccess, false);

        doCheck(callable);

        // standalone advertiser access
        resetExpectationsToDefault();

        campaign = advertiserCampaign;
        account = advertiserCampaign.getAccount();

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserManagerAllAccess1, true);

        doCheck(callable);
    }
}
