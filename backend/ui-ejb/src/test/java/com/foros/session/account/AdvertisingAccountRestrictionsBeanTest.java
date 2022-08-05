package com.foros.session.account;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class AdvertisingAccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AdvertisingAccountRestrictions advertisingAccountRestrictions;

    private AdvertisingAccountBase callableAccount;

    @Test
    public void testCanUpdateRestrictedFinanceFields() throws Exception {
        // Standalone Advertiser
        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return advertisingAccountRestrictions.canUpdateRestrictedFinanceFields(callableAccount);
            }
        };

        callableAccount = (AdvertiserAccount)advertiserAllAccess1.getUser().getAccount();
        advertiserAccountTF.persist((AdvertiserAccount)callableAccount);
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserAllAccess2, false);
        expectResult(advertiserManagerNoAccess, false);
        expectResult(agencyAllAccess1, false);
        setExpectResult();
        doCheck(callable);

        // Agency
        callableAccount = (AgencyAccount)agencyAllAccess1.getUser().getAccount();
        agencyAccountTF.persist((AgencyAccount)callableAccount);
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserAllAccess2, false);
        expectResult(advertiserManagerNoAccess, false);
        expectResult(agencyAllAccess1, false);
        setExpectResult();
        doCheck(callable);
    }

    @Test
    public void testCanUpdateFinance() throws Exception {
        // Standalone Advertiser
        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return advertisingAccountRestrictions.canUpdateFinance(callableAccount);
            }
        };

        callableAccount = (AdvertiserAccount) advertiserAllAccess1.getUser().getAccount();
        advertiserAccountTF.persist((AdvertiserAccount)callableAccount);
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserAllAccess2, false);
        expectResult(advertiserManagerNoAccess, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        setExpectResult();
        doCheck(callable);

        // Agency
        callableAccount = (AgencyAccount) agencyAllAccess1.getUser().getAccount();
        agencyAccountTF.persist((AgencyAccount)callableAccount);
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerAllAccess2, false);
        // Agency can update commission finance field.
        expectResult(agencyAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerNoAccess, false);
        setExpectResult();
        doCheck(callable);
    }

    private void setExpectResult() {
        expectResult(internalNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
    }
}

