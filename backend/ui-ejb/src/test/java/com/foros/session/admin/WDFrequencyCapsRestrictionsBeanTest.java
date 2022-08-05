package com.foros.session.admin;

import com.foros.AbstractRestrictionsBeanTest;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class WDFrequencyCapsRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private WDFrequencyCapsRestrictions wdFrequencyCapsRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("wdFrequencyCaps", "view") {
            @Override
            public boolean call() {
                return wdFrequencyCapsRestrictions.canView();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("wdFrequencyCaps", "edit") {
            @Override
            public boolean call() {
                return wdFrequencyCapsRestrictions.canUpdate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }
}
