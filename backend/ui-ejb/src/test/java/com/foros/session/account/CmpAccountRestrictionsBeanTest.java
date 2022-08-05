package com.foros.session.account;

import com.foros.session.account.AccountRestrictions;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.security.AccountRole;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class CmpAccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountRestrictions accountRestrictions;

    private CmpAccount account;

    @Test
    public void testUpdateDeleteUndeleteCmp() throws Exception {
        Callable call = new Callable("cmp_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canUpdate(account);
            }
        };

        account = (CmpAccount)cmpAllAccess1.getUser().getAccount();
        account.setAccountManager(cmpManagerAllAccess1.getUser());
        cmpAccountTF.persist(account);

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, true);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(call);

        call = new Callable("cmp_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canDelete(account);
            }
        };

        doCheck(call);

        cmpAccountTF.delete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        expectResult(cmpAllAccess1, false);
        expectResult(cmpManagerAllAccess1, false);
        doCheck(call);

        call = new Callable("cmp_account", "undelete") {
            @Override
            public boolean call() {
                return accountRestrictions.canUndelete(account);
            }
        };

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(cmpAllAccess1, false);
        doCheck(call);

        cmpAccountTF.undelete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        doCheck(call);
    }

    @Test
    public void testViewCMP() throws Exception {
        Callable call = new Callable("cmp_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(AccountRole.CMP);
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, true);
        doCheck(call);

        call = new Callable("cmp_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(account);
            }
        };
        account = cmpAccountTF.create();
        account.setAccountManager(cmpManagerAllAccess1.getUser());
        account.setInternalAccount((InternalAccount)cmpManagerAllAccess1.getUser().getAccount());
        cmpAccountTF.persist((CmpAccount)account);

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(cmpManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(call);
    }
}

