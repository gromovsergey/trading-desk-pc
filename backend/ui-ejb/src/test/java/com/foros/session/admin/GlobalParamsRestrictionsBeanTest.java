package com.foros.session.admin;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.session.admin.globalParams.GlobalParamsRestrictions;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class GlobalParamsRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private GlobalParamsRestrictions globalParamsRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("globalParams", "view") {
            @Override
            public boolean call() {
                return globalParamsRestrictions.canView();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("globalParams", "edit") {
            @Override
            public boolean call() {
                return globalParamsRestrictions.canUpdate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }
}
