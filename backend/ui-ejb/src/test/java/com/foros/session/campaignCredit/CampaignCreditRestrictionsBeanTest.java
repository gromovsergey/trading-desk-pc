package com.foros.session.campaignCredit;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.test.factory.CampaignCreditAllocationTestFactory;
import com.foros.test.factory.CampaignCreditTestFactory;

import group.Db;
import group.Restriction;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class CampaignCreditRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private CampaignCreditRestrictions campaignCreditRestrictions;

    @Autowired
    private CampaignCreditTestFactory campaignCreditTF;

    @Autowired
    private CampaignCreditAllocationTestFactory campaignCreditAllocationTF;

    private CampaignCredit advertiserCampaignCredit;
    private CampaignCredit agencyCampaignCredit1;
    private CampaignCredit agencyCampaignCredit2;
    private CampaignCreditAllocation allocation1;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();

        advertiserCampaignCredit = campaignCreditTF.createPersistent(advertiserAllAccess1.getUser().getAccount());
        agencyCampaignCredit1 = campaignCreditTF.createPersistent(agencyAllAccess1.getUser().getAccount());
        agencyCampaignCredit2 = campaignCreditTF.createPersistent(agencyAllAccess1.getUser().getAccount());
        allocation1 = campaignCreditAllocationTF.createPersistent(agencyCampaignCredit1);
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();

        expectResult(ispAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(cmpAllAccess1, false);
    }

    @Test
    public void testCanView() throws Exception {
        // check only view permission
        Callable callCanView = new Callable("campaignCredit", "view") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canView();
            }
        };

        setUpDefaultExpectations();
        expectResult(internalAllAccess, true);
        doCheck(callCanView);

        // check concrete entities for advertiser account
        Callable callCanViewCampaignCredit = new Callable("campaignCredit", "view") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canView(advertiserCampaignCredit);
            }
        };

        Callable callCanViewAccount = new Callable("campaignCredit", "view") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canView(advertiserCampaignCredit.getAccount());
            }
        };

        setUpAccessExpectations();
        expectResult(agencyAllAccess1, false);
        expectResult(advertiserAllAccess1, true);
        doCheck(callCanViewCampaignCredit);
        doCheck(callCanViewAccount);

        // check concrete entities for agency account
        callCanViewCampaignCredit = new Callable("campaignCredit", "view") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canView(agencyCampaignCredit1);
            }
        };

        callCanViewAccount = new Callable("campaignCredit", "view") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canView(agencyCampaignCredit1.getAccount());
            }
        };

        setUpAccessExpectations();
        expectResult(agencyAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        doCheck(callCanViewCampaignCredit);
        doCheck(callCanViewAccount);
    }

    @Test
    public void testCanEdit() throws Exception {
        // check only edit permission
        Callable callCanEdit = new Callable("campaignCredit", "edit") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canEdit();
            }
        };

        setUpDefaultExpectations();
        expectResult(internalAllAccess, true);
        doCheck(callCanEdit);

        // check concrete entities
        Callable callCanEditCampaignCredit = new Callable("campaignCredit", "edit") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canEdit(advertiserCampaignCredit);
            }
        };

        Callable callCanEditAccount = new Callable("campaignCredit", "edit") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canEdit(advertiserCampaignCredit.getAccount());
            }
        };

        setUpAccessExpectations();
        expectResult(agencyAllAccess1, false);
        expectResult(advertiserAllAccess1, false);

        doCheck(callCanEditCampaignCredit);
        doCheck(callCanEditAccount);
    }

    @Test
    public void testCanDelete() throws Exception {
        Callable callCanDelete = new Callable("campaignCredit", "edit") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canDelete(agencyCampaignCredit1.getId());
            }
        };
        setUpAccessExpectations();
        expectResult(internalMultipleAccountsAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(internalAllAccess, true);
        doCheck(callCanDelete);

        callCanDelete = new Callable("campaignCredit", "edit") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canDelete(agencyCampaignCredit2.getId());
            }
        };
        setUpAccessExpectations();
        doCheck(callCanDelete);
    }

    @Test
    public void testCanEditAllocations() throws Exception {
        // check only edit_allocations permission
        Callable callCanEditAllocations = new Callable("campaignCredit", "edit_allocations") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canEditAllocations();
            }
        };

        setUpDefaultExpectations();
        expectResult(internalAllAccess, true);
        doCheck(callCanEditAllocations);

        // check concrete entities for advertiser account
        Callable callForAllocation = new Callable("campaignCredit", "edit_allocations") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canEditAllocations(allocation1);
            }
        };

        Callable callForCampaignCredit = new Callable("campaignCredit", "edit_allocations") {
            @Override
            public boolean call() {
                return campaignCreditRestrictions.canEditAllocations(agencyCampaignCredit1);
            }
        };

        setUpAccessExpectations();
        expectResult(agencyAllAccess1, true);
        expectResult(advertiserAllAccess1, false);

        doCheck(callForAllocation);
        doCheck(callForCampaignCredit);
    }

    private void setUpAccessExpectations() {
        setUpDefaultExpectations();

        expectResult(advertiserAllAccess2, false);
        expectResult(agencyAllAccess2, false);
        expectResult(internalMultipleAccountsAccess, true);
        expectResult(internalMultipleAccountsNoAccess, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
    }
}
