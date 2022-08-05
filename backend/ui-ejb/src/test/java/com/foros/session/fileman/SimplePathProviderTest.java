package com.foros.session.fileman;

import group.Unit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

@Category(Unit.class)
public class SimplePathProviderTest {
    private File testDir;

    @Before
    public void setUp() throws Exception {
        testDir = new File(System.getProperty("java.io.tmpdir"), SimplePathProviderTest.class.getName());
        testDir.mkdirs();
        cleanDirectory(testDir);
        writeByteArrayToFile(new File(testDir, "test.file"), "test".getBytes("ASCII"));
        File subdir = new File(testDir, "subdir");
        subdir.mkdir();
        writeByteArrayToFile(new File(subdir, "test.file"), "test".getBytes("ASCII"));
    }

    @After
    public void tearDown() throws Exception {
        deleteDirectory(testDir);
    }

    @Test
    public void isAccessible() throws IOException {
        SimplePathProvider pp = new SimplePathProvider(testDir, Collections.EMPTY_LIST);
        assertTrue(pp.isAccessiblePath(testDir));
        assertTrue(pp.isAccessiblePath(new File(testDir, "test.file")));
        assertTrue(pp.isAccessiblePath(new File(testDir, "absent.file")));
        assertTrue(pp.isAccessiblePath(new File(testDir, "subdir/")));
        assertTrue(pp.isAccessiblePath(new File(testDir, "subdir/absent.file")));
        assertTrue(pp.isAccessiblePath(new File(testDir, "absentDir/")));
        assertTrue(pp.isAccessiblePath(new File(testDir, "absentDir/absent.file")));
        assertFalse(pp.isAccessiblePath(testDir.getParentFile()));
        assertFalse(pp.isAccessiblePath(new File(testDir, "../anotherDir/")));
    }

    @Test
    public void getPath() throws IOException {
        SimplePathProvider pp = new SimplePathProvider(testDir, Collections.EMPTY_LIST);

        assertEquals(testDir, pp.getPath(""));
        assertEquals(new File(testDir, "test.file"), pp.getPath("/test.file"));
        assertEquals(new File(testDir, "test.file"), pp.getPath("", "test.file"));
        assertEquals(new File(testDir, "subfolder/test.file"), pp.getPath("/subfolder/test.file"));
        assertEquals(new File(testDir, "subfolder/test.file"), pp.getPath("/subfolder", "test.file"));
        assertEquals(new File(testDir, "absentSubfolder/test.file"), pp.getPath("/absentSubfolder/test.file"));
    }
}
