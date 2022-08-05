package com.foros.session.opportunity;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.opportunity.Opportunity;
import com.foros.session.account.AccountServiceBean;
import com.foros.test.factory.OpportunityTestFactory;

import group.Db;
import group.Restriction;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class OpportunityRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private OpportunityRestrictions opportunityRestrictions;

    @Autowired
    private OpportunityTestFactory opportunityTF;

    @Autowired
    private AccountServiceBean accountServiceBean;

    private Opportunity opportunity;
    private Opportunity advertiserOpportunity;
    private Opportunity agencyOpportunity;
    private AdvertisingAccountBase account;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        advertiserOpportunity = opportunityTF.createPersistent((AdvertiserAccount) advertiserAllAccess1.getUser().getAccount());
        agencyOpportunity = opportunityTF.createPersistent(agencyAdvertiser1.getAccount());
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(ispAllAccess1, false);
        expectResult(advertiserAllAccess2, false);
        expectResult(agencyAllAccess2, false);
    }

    @Test
    public void testCanCreate() throws Exception {
        //"opportunity", "create"
        Callable callCanCreate = new Callable("opportunity", "create") {
            @Override
            public boolean call() {
                return opportunityRestrictions.canCreate(account);
            }
        };

        checkAccessRules(callCanCreate);

        accountServiceBean.delete(account.getId());

        resetExpectationsToDefault();

        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        expectResult(advertiserManagerNoAccess, false);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanView() throws Exception {
        Callable callCanView = new Callable("opportunity", "view") {
            @Override
            public boolean call() {
                return opportunityRestrictions.canView(opportunity);
            }
        };

        checkAccessRules(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("opportunity", "edit") {
            @Override
            public boolean call() {
                return opportunityRestrictions.canUpdate(opportunity);
            }
        };

        checkAccessRules(callCanUpdate);
    }

    private void checkAccessRules(Callable callable) throws Exception {
        // agency access
        resetExpectationsToDefault();

        opportunity = agencyOpportunity;
        account = agencyOpportunity.getAccount();

        expectResult(agencyAllAccess1, false);
        expectResult(internalMultipleAccountsAccess, true);
        expectResult(internalMultipleAccountsNoAccess, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerNoAccess, false);
        expectResult(internalNoAccess, false);

        doCheck(callable);

        // standalone advertiser access
        resetExpectationsToDefault();

        opportunity = advertiserOpportunity;
        account = advertiserOpportunity.getAccount();

        expectResult(advertiserAllAccess1, false);
        expectResult(internalMultipleAccountsAccess, true);
        expectResult(internalMultipleAccountsNoAccess, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerNoAccess, false);
        expectResult(internalNoAccess, false);

        doCheck(callable);
    }
}
