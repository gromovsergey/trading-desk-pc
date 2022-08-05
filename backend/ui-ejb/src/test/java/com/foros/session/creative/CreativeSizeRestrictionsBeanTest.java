package com.foros.session.creative;

import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class CreativeSizeRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private CreativeSizeRestrictions creativeSizeRestrictions;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("creativeSize", "view") {
            @Override
            public boolean call() {
                return creativeSizeRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdateActive() throws Exception {
        final CreativeSize creativeSize = creativeSizeTF.createPersistent();
        Callable callCanUpdate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
            	creativeSize.setStatus(Status.ACTIVE);
                return creativeSizeRestrictions.canUpdate(creativeSize);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanUpdateDeleted() throws Exception {
        final CreativeSize creativeSize = creativeSizeTF.createPersistent();
        Callable callCanUpdate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
            	creativeSize.setStatus(Status.DELETED);
                return creativeSizeRestrictions.canUpdate(creativeSize);
            }
        };

        expectResult(internalAllAccess, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanUpdateTextSize() throws Exception {
        final CreativeSize creativeSize = creativeSizeTF.createPersistent();
        Callable callCanUpdate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
            	creativeSize.setDefaultName(CreativeSize.TEXT_SIZE);
                return creativeSizeRestrictions.canUpdate(creativeSize);
            }
        };

        expectResult(internalAllAccess, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("creativeSize", "create") {
            @Override
            public boolean call() {
                return creativeSizeRestrictions.canCreate();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanDeleteActive() throws Exception {
        final CreativeSize creativeSize = creativeSizeTF.createPersistent();
        Callable callCanUpdate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
            	creativeSize.setStatus(Status.ACTIVE);
                return creativeSizeRestrictions.canDelete(creativeSize);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanDeleteDeleted() throws Exception {
        final CreativeSize creativeSize = creativeSizeTF.createPersistent();
        Callable callCanUpdate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
            	creativeSize.setStatus(Status.DELETED);
                return creativeSizeRestrictions.canDelete(creativeSize);
            }
        };

        expectResult(internalAllAccess, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanDeleteTextSize() throws Exception {
        final CreativeSize creativeSize = creativeSizeTF.createPersistent();
        Callable callCanUpdate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
            	creativeSize.setDefaultName(CreativeSize.TEXT_SIZE);
                return creativeSizeRestrictions.canDelete(creativeSize);
            }
        };

        expectResult(internalAllAccess, false);

        doCheck(callCanUpdate);
    }
}