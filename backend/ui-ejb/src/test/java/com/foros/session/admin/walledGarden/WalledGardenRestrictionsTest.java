package com.foros.session.admin.walledGarden;

import com.foros.AbstractRestrictionsBeanTest;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class WalledGardenRestrictionsTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private WalledGardenRestrictions walledGardenRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("walledGarden", "view") {
            @Override
            public boolean call() {
                return walledGardenRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("walledGarden", "edit") {
            @Override
            public boolean call() {
                return walledGardenRestrictions.canUpdate();
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("walledGarden", "create") {
            @Override
            public boolean call() {
                return walledGardenRestrictions.canCreate();
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);

        doCheck(callCanCreate);
    }
}
