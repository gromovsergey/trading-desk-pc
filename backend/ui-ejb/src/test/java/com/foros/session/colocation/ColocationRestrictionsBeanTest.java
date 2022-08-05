package com.foros.session.colocation;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.session.account.AccountServiceBean;
import com.foros.test.factory.ColocationTestFactory;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class ColocationRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private AccountServiceBean accountServiceBean;

    @Autowired
    private ColocationRestrictions colocationRestrictions;

    @Autowired
    private ColocationTestFactory colocationTF;

    private Colocation colocation;

    private IspAccount account;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        colocation = colocationTF.createPersistent((IspAccount) ispAllAccess1.getUser().getAccount());
        account = colocation.getAccount();
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(advertiserAllAccess1, false);
        expectResult(ispManagerAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(ispAllAccess2, false);
    }

    @Test
    public void testViewAndList() throws Exception {
        Callable callCanViewById = new Callable("colocation", "view") {
            @Override
            public boolean call() {
                return colocationRestrictions.canView(account.getId());
            }
        };

        Callable callCanViewByAccount = new Callable("colocation", "view") {
            @Override
            public boolean call() {
                return colocationRestrictions.canView(account);
            }
        };

        Callable callCanView = new Callable("colocation", "view") {
            @Override
            public boolean call() {
                return colocationRestrictions.canView(colocation);
            }
        };

        // normal account
        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        expectResult(internalUserAccountNoAccess, false);
        expectResult(internalMultipleAccountsAccess, true);
        expectResult(internalMultipleAccountsNoAccess, false);

        doCheck(callCanViewById);
        doCheck(callCanViewByAccount);
        doCheck(callCanView);

        // deleted colo
        expectResult(ispAllAccess1, false);

        colocationTF.delete(colocation.getId());

        doCheck(callCanView);

        // deleted account
        accountServiceBean.delete(account.getId());

        doCheck(callCanViewById);
        doCheck(callCanViewByAccount);
        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("colocation", "edit") {
            @Override
            public boolean call() {
                return colocationRestrictions.canUpdate(colocation);
            }
        };

        // normal
        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);

        doCheck(callCanUpdate);

        // deleted colo
        colocationTF.delete(colocation.getId());

        expectResult(internalAllAccess, false);
        expectResult(ispManagerAllAccess1, false);
        expectResult(ispAllAccess1, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanUndelete() throws Exception {
        Callable call = new Callable("colocation", "undelete") {
            @Override
            public boolean call() {
                return colocationRestrictions.canUndelete(colocation);
            }
        };

        // normal
        expectResult(internalAllAccess, false);
        expectResult(ispManagerAllAccess1, false);
        expectResult(ispAllAccess1, false);

        doCheck(call);

        // deleted colo
        colocationTF.delete(colocation.getId());

        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);

        doCheck(call);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable call = new Callable("colocation", "create") {
            @Override
            public boolean call() {
                return colocationRestrictions.canCreate(account);
            }
        };

        // normal
        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);

        doCheck(call);

        // deleted account
        accountServiceBean.delete(account.getId());

        expectResult(internalAllAccess, false);
        expectResult(ispManagerAllAccess1, false);
        expectResult(ispAllAccess1, false);

        doCheck(call);
    }
}
