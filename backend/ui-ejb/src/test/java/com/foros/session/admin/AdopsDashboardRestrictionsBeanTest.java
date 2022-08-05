package com.foros.session.admin;

import com.foros.AbstractRestrictionsBeanTest;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class AdopsDashboardRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AdopsDashboardRestrictions adopsDashboardRestrictions;

    @Test
    public void testCanRun() throws Exception {
        Callable callCanRun = new Callable("adOpsDashboard", "run") {
            @Override
            public boolean call() {
                return adopsDashboardRestrictions.canRun();
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(advertiserAllAccess1, false);

        doCheck(callCanRun);
    }
}
