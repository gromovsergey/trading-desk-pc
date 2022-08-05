package com.foros.session.admin.currency;

import com.foros.AbstractRestrictionsBeanTest;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class CurrencyRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private CurrencyRestrictions currencyRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("currency", "view") {
            @Override
            public boolean call() {
                return currencyRestrictions.canView();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("currency", "edit") {
            @Override
            public boolean call() {
                return currencyRestrictions.canUpdate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("currency", "create") {
            @Override
            public boolean call() {
                return currencyRestrictions.canCreate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }
}

