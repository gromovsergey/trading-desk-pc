package com.foros.session.admin.fraudConditions;

import com.foros.AbstractRestrictionsBeanTest;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class FraudConditionsRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private FraudConditionsRestrictions fraudConditionsRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("fraudConditions", "view") {
            @Override
            public boolean call() {
                return fraudConditionsRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }
}
