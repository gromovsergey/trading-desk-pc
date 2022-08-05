package com.foros.session.admin.accountType;

import com.foros.AbstractRestrictionsBeanTest;

import com.foros.session.admin.accountType.AccountTypeRestrictions;
import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class AccountTypeRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountTypeRestrictions accountTypeRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("accountType", "view") {
            @Override
            public boolean call() {
                return accountTypeRestrictions.canView();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("accountType", "edit") {
            @Override
            public boolean call() {
                return accountTypeRestrictions.canUpdate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("accountType", "create") {
            @Override
            public boolean call() {
                return accountTypeRestrictions.canCreate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }
}

