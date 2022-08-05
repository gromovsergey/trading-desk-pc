package com.foros.session.account;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.InternalAccount;
import com.foros.security.AccountRole;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class AdvertiserAccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountRestrictions accountRestrictions;

    private AdvertiserAccount account;

    @Test
    public void testUpdateDeleteUndeleteAdvertiserAgency() throws Exception {
        Callable call = new Callable("advertising_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canUpdate(account);
            }
        };

        account = advertiserAccountTF.createAdvertiserInAgency((AgencyAccount)agencyAllAccess1.getUser().getAccount());
        ((AdvertiserAccount)account).setInternalAccount((InternalAccount)advertiserManagerAllAccess1.getUser().getAccount());
        advertiserAccountTF.persistAgencyAdvertiser((AdvertiserAccount)account);

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyAllAccess1, true);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(call);

        call = new Callable("advertising_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canDelete(account);
            }
        };

        doCheck(call);

        agencyAccountTF.delete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        expectResult(agencyAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(call);

        call = new Callable("advertising_account", "undelete") {
            @Override
            public boolean call() {
                return accountRestrictions.canUndelete(account);
            }
        };

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(agencyAllAccess1, false);
        doCheck(call);

        agencyAccountTF.undelete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        doCheck(call);
    }

    @Test
    public void testUpdateDeleteUndeleteAdvertiser() throws Exception {
        Callable call = new Callable("advertising_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canUpdate(account);
            }
        };

        account = (AdvertiserAccount)advertiserAllAccess1.getUser().getAccount();
        account.setAccountManager(advertiserManagerAllAccess1.getUser());
        advertiserAccountTF.persist(account);

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, true);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(call);

        call = new Callable("advertising_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canDelete(account);
            }
        };

        doCheck(call);

        advertiserAccountTF.delete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(call);

        call = new Callable("advertising_account", "undelete") {
            @Override
            public boolean call() {
                return accountRestrictions.canUndelete(account);
            }
        };

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        doCheck(call);

        advertiserAccountTF.undelete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        doCheck(call);
    }

    @Test
    public void testViewAdvertiser() throws Exception {
        Callable call = new Callable("advertising_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(AccountRole.ADVERTISER);
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, true);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, true);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(call);

        call = new Callable("advertising_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(account);
            }
        };
        account = advertiserAccountTF.create();
        account.setAccountManager(advertiserManagerAllAccess1.getUser());
        account.setInternalAccount((InternalAccount)advertiserManagerAllAccess1.getUser().getAccount());
        advertiserAccountTF.persist((AdvertiserAccount)account);

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(call);

        account = advertiserAccountTF.createAdvertiserInAgency((AgencyAccount)agencyAllAccess1.getUser().getAccount());
        ((AdvertiserAccount)account).setInternalAccount((InternalAccount)advertiserManagerAllAccess1.getUser().getAccount());
        advertiserAccountTF.persistAgencyAdvertiser((AdvertiserAccount)account);

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyAllAccess1, true);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(call);
    }
}

