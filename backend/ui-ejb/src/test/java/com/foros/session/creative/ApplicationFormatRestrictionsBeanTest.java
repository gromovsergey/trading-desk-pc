package com.foros.session.creative;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.session.template.ApplicationFormatRestrictions;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class ApplicationFormatRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private ApplicationFormatRestrictions applicationFormatRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("applicationFormat", "view") {
            @Override
            public boolean call() {
                return applicationFormatRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("applicationFormat", "edit") {
            @Override
            public boolean call() {
                return applicationFormatRestrictions.canUpdate();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("applicationFormat", "create") {
            @Override
            public boolean call() {
                return applicationFormatRestrictions.canCreate();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }
}