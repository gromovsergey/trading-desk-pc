package com.foros.session.creative;

import com.foros.AbstractRestrictionsBeanTest;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class CreativeCategoryRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private CreativeCategoryRestrictions creativeCategoryRestrictions;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("creativeCategory", "view") {
            @Override
            public boolean call() {
                return creativeCategoryRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("creativeCategory", "edit") {
            @Override
            public boolean call() {
                return creativeCategoryRestrictions.canUpdate();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }
}