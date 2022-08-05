package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.AbstractFileAwareTest;
import com.foros.session.fileman.AccountSizeExceededException;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Category(Unit.class)
public class FilesPerFolderRestrictionTest extends AbstractFileAwareTest {
    @Test
    public void testFilesPerFolderRestriction() throws Exception {
        FilesPerFolderRestriction restriction = new FilesPerFolderRestriction(2);
        String newFile = "newFile";

        createTestFile("file1");
        check(restriction, newFile);

        createTestFile(".file~forostmp", 128);
        check(restriction, newFile);

        createTestFile("file2");
        try {
            check(restriction, "newFile");
            fail();
        } catch (AccountSizeExceededException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testWithParents() throws Exception {
        FilesPerFolderRestriction restriction = new FilesPerFolderRestriction(2);
        String newFile = "newDir/dir/file";

        createTestFile("dir1/dir/file");
        check(restriction, newFile);

        createTestFile("dir2/dir/file");
        try {
            check(restriction, newFile);
            fail();
        } catch (AccountSizeExceededException e) {
            assertNotNull(e.getMessage());
        }
    }

    private void check(FilesPerFolderRestriction restriction, String fileName) throws Exception {
        Exception folderEx = null;
        Exception fileEx = null;
        try {
            restriction.checkNewFileAllowed(new Quota(), pathProvider, pathProvider.getPath(fileName));
        } catch (Exception e) {
            fileEx = e;
        }
        try {
            restriction.checkNewFolderAllowed(new Quota(), pathProvider, pathProvider.getPath(fileName));
        } catch (Exception e) {
            folderEx = e;
        }

        // both null or both not null
        assertEquals(fileEx == null, folderEx == null);

        if (fileEx != null && folderEx != null) {
            assertEquals(fileEx.getClass(), folderEx.getClass());
            assertEquals(fileEx.getMessage(), folderEx.getMessage());
            throw fileEx;
        }
    }
}
