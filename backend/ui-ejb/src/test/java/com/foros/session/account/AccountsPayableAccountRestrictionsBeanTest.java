package com.foros.session.account;

import com.foros.test.factory.UserTestFactory;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.AccountsPayableAccountBase;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class AccountsPayableAccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountsPayableAccountRestrictions accountsPayableAccountRestrictions;

    @Autowired
    private UserTestFactory userTF;

    private AccountsPayableAccountBase callableAccount;

    @Test
    public void testCanUpdateFinanceNoUsers() throws Exception {
        // Publisher
        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return accountsPayableAccountRestrictions.canUpdateFinanceNoUsers(callableAccount);
            }
        };

        callableAccount = publisherAccountTF.createPersistent();
        callableAccount.setAccountManager(publisherManagerAllAccess1.getUser());
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(publisherAllAccess1, false);
        setPublisherExpectResult();
        doCheck(callable);

        // ISP
        callableAccount = ispAccountTF.createPersistent();
        callableAccount.setAccountManager(ispManagerAllAccess1.getUser());
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(ispAllAccess1, false);
        setISPExpectResult();
        doCheck(callable);

        // CMP
        callableAccount = cmpAccountTF.createPersistent();
        callableAccount.setAccountManager(cmpManagerAllAccess1.getUser());
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(cmpAllAccess1, false);
        setCMPExpectResult();
        doCheck(callable);
    }

    @Test
    public void testCanUpdateFinance() throws Exception {
        Callable call = new Callable() {
            @Override
            public boolean call() {
                return accountsPayableAccountRestrictions.canUpdateFinance(callableAccount);
            }
        };

        callableAccount = publisherAccountTF.create();
        ((PublisherAccount) callableAccount).setAccountManager(publisherManagerAllAccess1.getUser());
        ((PublisherAccount) callableAccount).setInternalAccount((InternalAccount) publisherManagerAllAccess1.getUser().getAccount());
        publisherAccountTF.persist((PublisherAccount)callableAccount);

        userTF.createPersistent(callableAccount);

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerAllAccess2, false);
        setPublisherExpectResult();
        doCheck(call);

        // ISP
        callableAccount = ispAccountTF.create();
        ((IspAccount) callableAccount).setAccountManager(ispManagerAllAccess1.getUser());
        ((IspAccount) callableAccount).setInternalAccount((InternalAccount) ispManagerAllAccess1.getUser().getAccount());
        ispAccountTF.persist((IspAccount)callableAccount);

        userTF.createPersistent(callableAccount);

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(ispAllAccess1, false);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispManagerAllAccess2, false);
        setISPExpectResult();
        doCheck(call);

        // CMP
        callableAccount = cmpAccountTF.create();
        ((CmpAccount) callableAccount).setAccountManager(cmpManagerAllAccess1.getUser());
        ((CmpAccount) callableAccount).setInternalAccount((InternalAccount) cmpManagerAllAccess1.getUser().getAccount());
        cmpAccountTF.persist((CmpAccount)callableAccount);

        userTF.createPersistent(callableAccount);

        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(cmpAllAccess1, false);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(cmpManagerAllAccess2, false);
        setCMPExpectResult();
        doCheck(call);
    }

    private void setPublisherExpectResult() {
        expectResult(internalNoAccess, false);
        expectResult(publisherAllAccess2, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
    }

    private void setISPExpectResult(){
        expectResult(internalNoAccess, false);
        expectResult(ispAllAccess2, false);
        expectResult(ispManagerNoAccess, false);
        expectResult(ispManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(publisherAllAccess1, false);
    }

    private void setCMPExpectResult(){
        expectResult(internalNoAccess, false);
        expectResult(cmpAllAccess2, false);
        expectResult(cmpManagerNoAccess, false);
        expectResult(cmpManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(publisherAllAccess1, false);
    }
}
