package com.foros.session.admin.categoryChannel;

import com.foros.test.factory.CategoryChannelTestFactory;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.Status;
import com.foros.model.channel.CategoryChannel;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class CategoryChannelRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private CategoryChannelRestrictions categoryChannelRestrictions;

    @Autowired
    private CategoryChannelTestFactory categoryChannelTF;

    private CategoryChannel categoryChannel;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        categoryChannel = categoryChannelTF.createPersistent();
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("categoryChannel", "view") {
            @Override
            public boolean call() {
                return categoryChannelRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdateActive() throws Exception {
        Callable callCanUpdate = new Callable("categoryChannel", "edit") {
            @Override
            public boolean call() {
                categoryChannel.setStatus(Status.ACTIVE);
                return categoryChannelRestrictions.canUpdate(categoryChannel);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanUpdateDeleted() throws Exception {
        Callable callCanUpdate = new Callable("categoryChannel", "edit") {
            @Override
            public boolean call() {
                categoryChannel.setStatus(Status.DELETED);
                return categoryChannelRestrictions.canUpdate(categoryChannel);
            }
        };

        expectResult(internalAllAccess, false);

        doCheck(callCanUpdate);
    }


    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("categoryChannel", "create") {
            @Override
            public boolean call() {
                return categoryChannelRestrictions.canCreate();
            }
        };

        Callable callCanCreateNull = new Callable("categoryChannel", "create") {
            @Override
            public boolean call() {
                return categoryChannelRestrictions.canCreate(null);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
        doCheck(callCanCreateNull);
    }

    @Test
    public void testCanCreateUpdateWithDeletedParent() throws Exception {
        setDeletedObjectsVisible(true);
        Callable callCanCreate = new Callable("categoryChannel", "create") {
            @Override
            public boolean call() {
                return categoryChannelRestrictions.canCreate(categoryChannel);
            }
        };
        expectResult(internalAllAccess, true);

        final CategoryChannel child = categoryChannelTF.createChild(categoryChannel);
        Callable callCanUpdate = new Callable("categoryChannel", "create") {
            @Override
            public boolean call() {
                return categoryChannelRestrictions.canCreate(child);
            }
        };
        categoryChannelTF.delete(categoryChannel);
        entityManager.flush();

        expectResult(internalAllAccess, false);

        doCheck(callCanCreate);
        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreateChildWithDeletedGrandsire() throws Exception {
        setDeletedObjectsVisible(true);
        final CategoryChannel child = categoryChannelTF.createChild(categoryChannel);
        Callable callCanCreate = new Callable("categoryChannel", "create") {
            @Override
            public boolean call() {
                return categoryChannelRestrictions.canCreate(child);
            }
        };

        expectResult(internalAllAccess, true);

        categoryChannelTF.delete(categoryChannel);
        entityManager.flush();

        expectResult(internalAllAccess, false);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanDeleteActive() throws Exception {
        Callable callCanDeleteActive = new Callable("categoryChannel", "edit") {
            @Override
            public boolean call() {
                categoryChannel.setStatus(Status.ACTIVE);
                return categoryChannelRestrictions.canDelete(categoryChannel);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanDeleteActive);
    }

    @Test
    public void testCanDeleteDeleted() throws Exception {
        Callable callCanDeleteDeleted = new Callable("categoryChannel", "edit") {
            @Override
            public boolean call() {
                categoryChannel.setStatus(Status.DELETED);
                return categoryChannelRestrictions.canDelete(categoryChannel);
            }
        };

        expectResult(internalAllAccess, false);

        doCheck(callCanDeleteDeleted);
    }

    @Test
    public void testCanUndelete() throws Exception {
        Callable callCanUndelete = new Callable("categoryChannel", "undelete") {
            @Override
            public boolean call() {
                categoryChannel.setStatus(Status.DELETED);
                return categoryChannelRestrictions.canUndelete(categoryChannel);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUndelete);
    }
}
