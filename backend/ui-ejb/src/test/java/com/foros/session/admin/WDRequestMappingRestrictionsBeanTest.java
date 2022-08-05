package com.foros.session.admin;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.session.admin.wdRequestMapping.WDRequestMappingRestrictions;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class WDRequestMappingRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private WDRequestMappingRestrictions wdRequestMappingRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("wdRequestMapping", "view") {
            @Override
            public boolean call() {
                return wdRequestMappingRestrictions.canView();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("wdRequestMapping", "edit") {
            @Override
            public boolean call() {
                return wdRequestMappingRestrictions.canUpdate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("wdRequestMapping", "create") {
            @Override
            public boolean call() {
                return wdRequestMappingRestrictions.canCreate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }
}
