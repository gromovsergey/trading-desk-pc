package com.foros.session.fileman.restrictions;

import com.foros.AbstractUnitTest;
import com.foros.session.fileman.FileManagerException;
import com.foros.session.fileman.PathProvider;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

@Category(Unit.class)
public class CompositeFileRestrictionTest extends AbstractUnitTest {
    @Test
    public void checkNewFileAllowed() throws IOException {
        FileRestriction fr1 = createMock("fr1", FileRestriction.class);
        FileRestriction fr2 = createMock("fr2", FileRestriction.class);
        FileRestriction fr3 = createMock("fr3", FileRestriction.class);
        PathProvider pathProvider = createMock(PathProvider.class);
        Quota quota = new Quota();
        File file = new File("test");
        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        FileManagerException fme = new FileManagerException("Yep!");

        fr1.checkNewFileAllowed(quota, pathProvider, file);
        fr2.checkNewFileAllowed(quota, pathProvider, file);
        expectLastCall().andThrow(fme);
        //fr3 should not be called

        fr1.checkNewFolderAllowed(quota, pathProvider, file);
        fr2.checkNewFolderAllowed(quota, pathProvider, file);
        expectLastCall().andThrow(fme);
        //fr3 should not be called

        replay(fr1, fr2, fr3, pathProvider);
        CompositeFileRestriction restriction = new CompositeFileRestriction(fr1, fr2, fr3);

        try {
            restriction.checkNewFileAllowed(quota, pathProvider, file);
            fail();
        } catch (FileManagerException e) {
            assertSame(fme, e);
        }

        try {
            restriction.checkNewFolderAllowed(quota, pathProvider, file);
            fail();
        } catch (FileManagerException e) {
            assertSame(fme, e);
        }

        verify(fr1, fr2, fr3, pathProvider);
    }
}
