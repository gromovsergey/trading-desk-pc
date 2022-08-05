package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.*;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Category(Unit.class)
public class AccountFolderFileRestrictionTest extends AbstractFileAwareTest {
    @Test
    public void checkNewFileAllowed() throws IOException {
        createTestDir("Creative");
        createTestDir("Creative/1"); // account folder

        PathProvider creativePathProvider = new SimplePathProvider(new File(pathProvider.getPath(""), "Creative"), Collections.EMPTY_LIST);
        FailFileRestriction target = new FailFileRestriction();
        AccountFolderFileRestriction restriction = new AccountFolderFileRestriction(creativePathProvider, target);

        // do not invoke target
        restriction.checkNewFileAllowed(new Quota(), pathProvider, pathProvider.getPath("Creative/1"));
        restriction.checkNewFileAllowed(new Quota(), pathProvider, pathProvider.getPath("Templates/1"));
        restriction.checkNewFileAllowed(new Quota(), pathProvider, pathProvider.getPath("Creative/2"));

        // throw exception
        try {
            restriction.checkNewFileAllowed(new Quota(), pathProvider, pathProvider.getPath("Creative/2/file"));
            fail();
        } catch (FileManagerException e) {
            assertNotNull(e.getMessage());
            assertFalse(e instanceof TooManyDirLevelsException);
        }

        // invoke target;
        try {
            restriction.checkNewFileAllowed(new Quota(), pathProvider, pathProvider.getPath("Creative/1/file"));
            fail();
        } catch (TooManyDirLevelsException e) {
            assertNotNull(e.getMessage());
        }
    }
}
