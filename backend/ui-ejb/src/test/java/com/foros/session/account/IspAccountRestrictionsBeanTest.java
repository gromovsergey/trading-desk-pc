package com.foros.session.account;

import com.foros.session.account.AccountRestrictions;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.security.AccountRole;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class IspAccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountRestrictions accountRestrictions;

    private IspAccount account;

    @Test
    public void testUpdateDeleteUndeleteIsp() throws Exception {
        Callable call = new Callable("isp_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canUpdate(account);
            }
        };

        account = (IspAccount)ispAllAccess1.getUser().getAccount();
        account.setAccountManager(ispManagerAllAccess1.getUser());
        ispAccountTF.persist(account);

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, true);
        doCheck(call);

        call = new Callable("isp_account", "edit") {
            @Override
            public boolean call() {
                return accountRestrictions.canDelete(account);
            }
        };

        doCheck(call);

        ispAccountTF.delete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        expectResult(ispAllAccess1, false);
        expectResult(ispManagerAllAccess1, false);
        doCheck(call);

        call = new Callable("isp_account", "undelete") {
            @Override
            public boolean call() {
                return accountRestrictions.canUndelete(account);
            }
        };

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(ispAllAccess1, false);
        doCheck(call);

        ispAccountTF.undelete(account);

        // Negative case
        expectResult(internalAllAccess, false);
        doCheck(call);
    }

    @Test
    public void testViewISP() throws Exception {
        Callable call = new Callable("isp_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(AccountRole.ISP);
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, true);
        expectResult(cmpAllAccess1, false);
        doCheck(call);

        call = new Callable("isp_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(account);
            }
        };
        account = ispAccountTF.create();
        account.setAccountManager(ispManagerAllAccess1.getUser());
        account.setInternalAccount((InternalAccount)ispManagerAllAccess1.getUser().getAccount());
        ispAccountTF.persist((IspAccount)account);

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(call);
    }
}

