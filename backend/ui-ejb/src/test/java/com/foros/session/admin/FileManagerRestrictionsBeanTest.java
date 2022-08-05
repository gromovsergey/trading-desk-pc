package com.foros.session.admin;

import com.foros.AbstractRestrictionsBeanTest;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class FileManagerRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private FileManagerRestrictions fileManagerRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testCanEdit() throws Exception {
        Callable callCanEdit = new Callable("fileManager", "edit") {
            @Override
            public boolean call() {
                return fileManagerRestrictions.canManage();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanEdit);
    }
}
