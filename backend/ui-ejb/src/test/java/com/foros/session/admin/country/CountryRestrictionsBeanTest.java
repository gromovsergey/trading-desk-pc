package com.foros.session.admin.country;

import com.foros.AbstractRestrictionsBeanTest;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class CountryRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private CountryRestrictions countryRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("country", "view") {
            @Override
            public boolean call() {
                return countryRestrictions.canView();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("country", "edit") {
            @Override
            public boolean call() {
                return countryRestrictions.canUpdate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
        expectResult(internalAllAccess, false);
    }
}
