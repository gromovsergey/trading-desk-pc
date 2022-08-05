package com.foros.session.fileman;

import com.foros.AbstractUnitTest;
import com.foros.session.fileman.restrictions.FileNameRestriction;
import com.foros.session.fileman.restrictions.FileNameRestrictionImpl;

import static org.junit.Assert.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractFileAwareTest extends AbstractUnitTest {
    protected String testCurrentDir;
    private File testCurrentDirFile;
    protected PathProvider pathProvider;
    protected FileNameRestriction fileNameRestriction = FileNameRestrictionImpl.INSTANCE;

    @Before
    public void setUp() throws Exception {
        String tempDir = FileUtils.trimPathName(System.getProperty("java.io.tmpdir"), false);

        testCurrentDir = "testCreatives/";
        testCurrentDirFile = new File(tempDir, testCurrentDir);
        if (!testCurrentDirFile.exists()) {
            assertTrue(testCurrentDirFile.mkdir());
        } else {
            org.apache.commons.io.FileUtils.cleanDirectory(testCurrentDirFile);
        }
        pathProvider = new SimplePathProvider(tempDir + testCurrentDir, Collections.EMPTY_LIST);
    }

    @After
    public void clearFiles() throws Exception {
        FileUtils.deleteFile(testCurrentDirFile);
    }

    protected File createTestFile(String testFile) throws IOException, BadNameException {
        fileNameRestriction.checkFileName(testFile);
        return createTestFile(testFile, 128);
    }
    
    protected File createTestFile(String testFile, int size) throws IOException, BadNameException {
        File file = pathProvider.getPath(testFile);
        if (!file.getParentFile().exists()) {
            assertTrue(file.getParentFile().mkdirs());
        }
        OutputStream os = null;
        InputStream is = null;
        try {
            os = new FileOutputStream(file);
            is = new ByteArrayInputStream(new byte[size]);
            IOUtils.copy(is, os);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
        return file;
    }

    protected void createTestDir(String dir) {
        fileNameRestriction.checkFolderName(dir);
        File path = pathProvider.getPath(dir);
        assertTrue(path.mkdirs());
    }
}
