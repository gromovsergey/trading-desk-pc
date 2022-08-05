package com.foros.session.account;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.security.AccountRole;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class PublisherAccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountRestrictions accountRestrictions;

    private PublisherAccount account;

    @Test
    public void testUpdateDeleteUndeletePublisher() throws Exception {
        Callable call = new Callable("publisher_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canUpdate(account);
            }
        };

        account = (PublisherAccount)publisherAllAccess1.getUser().getAccount();
        account.setAccountManager(publisherManagerAllAccess1.getUser());
        publisherAccountTF.persist(account);

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, true);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(call);

        call = new Callable("publisher_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canDelete(account);
            }
        };

        doCheck(call);

        publisherAccountTF.delete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherManagerAllAccess1, false);
        doCheck(call);

        call = new Callable("publisher_account", "undelete") {
            @Override
            public boolean call() {
                return accountRestrictions.canUndelete(account);
            }
        };

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(publisherAllAccess1, false);
        doCheck(call);

        publisherAccountTF.undelete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        doCheck(call);
    }

    @Test
    public void testViewPublisher() throws Exception {
        Callable call = new Callable("publisher_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(AccountRole.PUBLISHER);
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, true);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(call);

        call = new Callable("publisher_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(account);
            }
        };
        account = publisherAccountTF.create();
        account.setAccountManager(publisherManagerAllAccess1.getUser());
        account.setInternalAccount((InternalAccount)publisherManagerAllAccess1.getUser().getAccount());
        publisherAccountTF.persist((PublisherAccount)account);

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(call);
    }
}

