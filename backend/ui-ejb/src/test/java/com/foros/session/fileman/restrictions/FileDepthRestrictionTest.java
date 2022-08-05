package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.AbstractFileAwareTest;
import com.foros.session.fileman.TooManyDirLevelsException;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Category(Unit.class)
public class FileDepthRestrictionTest extends AbstractFileAwareTest {
    @Test
    public void testCheckNewFileAllowed() throws IOException {
        FileDepthRestriction fileDepthRestriction = new FileDepthRestriction(2);
        checkFolder(fileDepthRestriction, "111");
        checkFolder(fileDepthRestriction, "111/222");
        try {
            checkFolder(fileDepthRestriction, "111/222/333");
            fail();
        } catch (TooManyDirLevelsException e) {
            assertNotNull(e.getMessage());
        }

        checkFile(fileDepthRestriction, "111/222/333");
        try {
            checkFolder(fileDepthRestriction, "111/222/333/444");
            fail();
        } catch (TooManyDirLevelsException e) {
            assertNotNull(e.getMessage());
        }
    }

    private void checkFile(FileDepthRestriction fileDepthRestriction, String fileName) throws IOException {
        fileDepthRestriction.checkNewFileAllowed(new Quota(), pathProvider, pathProvider.getPath(fileName));
    }

    private void checkFolder(FileDepthRestriction fileDepthRestriction, String fileName) throws IOException {
        fileDepthRestriction.checkNewFolderAllowed(new Quota(), pathProvider, pathProvider.getPath(fileName));
    }

}
