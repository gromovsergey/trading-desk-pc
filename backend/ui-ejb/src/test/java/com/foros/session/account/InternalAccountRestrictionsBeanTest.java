package com.foros.session.account;

import com.foros.session.account.AccountRestrictions;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.InternalAccount;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class InternalAccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountRestrictions accountRestrictions;

    private InternalAccount account;

    @Test
    public void testUpdateDeleteUndeleteInternal() throws Exception {
        Callable call = new Callable("internal_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canUpdate(account);
            }
        };

        account = internalAccountTF.createPersistent();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        doCheck(call);

        call = new Callable("internal_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canDelete(account);
            }
        };

        doCheck(call);

        internalAccountTF.delete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        doCheck(call);

        call = new Callable("internal_account", "undelete") {
            @Override
            public boolean call() {
                return accountRestrictions.canUndelete(account);
            }
        };

        expectResult(internalAllAccess, true);
        doCheck(call);

        internalAccountTF.undelete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        doCheck(call);
    }
}

