package com.foros.session.fileman;

import com.foros.config.ConfigService;
import com.foros.config.MockConfigService;
import com.foros.model.fileman.FileInfo;
import com.foros.session.ServiceLocatorMock;
import com.foros.session.admin.FileManagerRestrictions;
import com.foros.session.fileman.restrictions.FileCountZipRestriction;
import com.foros.session.fileman.restrictions.FileSizeRestrictionImpl;
import com.foros.session.fileman.restrictions.ZipRestriction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import group.Unit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class FileSystemTest extends AbstractFileAwareTest {

    @Rule
    public ServiceLocatorMock serviceLocatorMock = ServiceLocatorMock.getInstance();

    private FileManagerRestrictions fileManagerRestrictions = EasyMock.createMock(FileManagerRestrictions.class);

    @Before
    public void initConfig() {
        serviceLocatorMock.injectService(ConfigService.class, new MockConfigService());
    }

    @Test
    public void createFile() throws Exception {
        FileSystem fileSystem = newTestFileSystem();

        ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);

        String fname = "testFile";
        String dir = "dir";

        fileSystem.setFileManagerRestrictions(fileManagerRestrictions);
        EasyMock.expect(fileManagerRestrictions.canManage()).andReturn(true);
        EasyMock.replay(fileManagerRestrictions);

        fileSystem.writeFile(dir, fname, is);
        assertTrue(pathProvider.getPath(dir, fname).exists());
        assertEquals(1, pathProvider.getPath(dir).list().length);
    }

    @Test
    public void overwriteFile() throws Exception {
        FileSystem fileSystem = newTestFileSystem();

        createTestFile("dir/testFile", 512);

        String dir = "dir";
        String fname = "testFile";
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[128]);

        fileSystem.setFileManagerRestrictions(fileManagerRestrictions);
        EasyMock.expect(fileManagerRestrictions.canManage()).andReturn(true);
        EasyMock.replay(fileManagerRestrictions);

        fileSystem.writeFile("dir", "testFile", is);

        assertTrue(pathProvider.getPath(dir, fname).exists());
        assertEquals(128, pathProvider.getPath(dir, fname).length());
        assertEquals(1, pathProvider.getPath(dir).list().length);
    }

    @Test
    public void createFileWithSizeRestriction() throws Exception {
        FileSystem fileSystem = newTestFileSystem();

        String dir = "dir";
        String name1 = "testAccLimit1";
        String name2 = "testAccLimit2";

        byte[] buf = "content".getBytes();

        fileSystem.setFileManagerRestrictions(fileManagerRestrictions);
        EasyMock.expect(fileManagerRestrictions.canManage()).andReturn(true);
        EasyMock.replay(fileManagerRestrictions);

        fileSystem.setFileSizeRestriction(new FileSizeRestrictionImpl(buf.length + 1));
        fileSystem.writeFile(dir, name1, new ByteArrayInputStream(buf));
        assertTrue(pathProvider.getPath(dir, name1).exists());

        fileSystem.setFileSizeRestriction(new FileSizeRestrictionImpl(buf.length - 1));
        try {
            fileSystem.writeFile(dir, name2, new ByteArrayInputStream(buf));
            fail();
        } catch (FileSizeException e) {
            assertNotNull(e.getMessage());
        }
        assertFalse(pathProvider.getPath(dir, name2).exists());
    }

    @Test
    public void createFileDirWithRestriction() throws Exception {
        FileSystem fileSystem = newTestFileSystem();
        String dname1 = "testFilesDirLevelLimit";
        String dname2 = "testFilesDirLevelLimit/dir";
        // success
        fileSystem.createFolder("", dname1);

        // fail
        fileSystem.setFileRestriction(new FailFileRestriction());
        try {
            fileSystem.createFolder("", dname2);
            fail();
        } catch (TooManyDirLevelsException e) {
            assertNotNull(e.getMessage());
        }

        assertTrue(pathProvider.getPath(dname1).exists());
        assertFalse(pathProvider.getPath(dname2).exists());
    }

    @Test
    public void unpackZip() throws Exception {
        FileSystem fileSystem = newTestFileSystem();

        InputStream zipStream = getClass().getResourceAsStream("testCreatives.zip");

        fileSystem.setFileManagerRestrictions(fileManagerRestrictions);
        EasyMock.expect(fileManagerRestrictions.canManage()).andReturn(true);
        EasyMock.expect(fileManagerRestrictions.canManage()).andReturn(true);
        EasyMock.replay(fileManagerRestrictions);

        fileSystem.unpackStream("dir", "unzip", zipStream, UnpackOptions.DEFAULT);

        File unzipDirFile = pathProvider.getPath("dir", "unzip");
        File[] list =  unzipDirFile.listFiles();
        assertNotNull("size", list);
        assertEquals("size", 2, list.length);
        assertTrue(new File(unzipDirFile, "120x600_brown.jpg").exists());
        assertTrue(new File(unzipDirFile, "subfolder").exists());
        assertTrue(new File(new File(unzipDirFile, "subfolder"), "120x600_green.jpg").exists());
    }

    @Test
    public void unpackBigZip() throws Exception {
        FileSystem fileSystem = newTestFileSystem();
        fileSystem.setFileSizeRestriction(new FileSizeRestrictionImpl(20000000));

        InputStream zipStream = getClass().getResourceAsStream("27megabytesZipped.zip");

        try {
            // unpack invalid .zip into same dir to check the file system integrity after rollback
            fileSystem.unpackStream("dir", "unzip", zipStream, UnpackOptions.DEFAULT);
            fail("Must fail as invalid ");
        } catch (FileSizeException e) {
            assertTrue(e.isArchiveException());
        } catch (Exception e) {
            fail("Must be FileSizeException");
        }
    }

    @Test
    public void unpackZipInvalid() throws Exception {
        FileSystem fileSystem = newTestFileSystem();
        InputStream zipStream = getClass().getResourceAsStream("testCreatives.zip");

        fileSystem.setFileManagerRestrictions(fileManagerRestrictions);
        EasyMock.expect(fileManagerRestrictions.canManage()).andReturn(true).anyTimes();
        EasyMock.replay(fileManagerRestrictions);

        fileSystem.unpackStream("", "testCreatives", zipStream, UnpackOptions.DEFAULT);

        // now file contains file with invalid name
        zipStream = getClass().getResourceAsStream("testCreativesInvalid.zip");

        try {
            // unpack invalid .zip into same dir to check the file system integrity after rollback
            fileSystem.unpackStream("", "testCreatives", zipStream, UnpackOptions.DEFAULT);
            fail("Must fail as invalid ");
        } catch (BadFileNameException e) {
            assertEquals("testCreativesInvalid/~120x600_brown.jpg", e.getFileName());
        }

        File unzipDirFile = pathProvider.getPath("", "testCreatives");
        File[] list = unzipDirFile.listFiles();
        assertNotNull(list);
        assertEquals(list.length, 2);
        assertTrue(new File(unzipDirFile, "120x600_brown.jpg").exists());
        assertTrue(new File(unzipDirFile, "subfolder").exists());
        assertTrue(new File(new File(unzipDirFile, "subfolder"), "120x600_green.jpg").exists());

        try {
            fileSystem.unpackStream("dir", "zip", getClass().getResourceAsStream("corrupted.zip"), UnpackOptions.DEFAULT);
            fail();
        } catch (BadZipException e) {
          assertNotNull(e.getMessage());
        }

        try {
            fileSystem.unpackStream("dir", "zip", getClass().getResourceAsStream("Bad_Archive.zip"), UnpackOptions.DEFAULT);
            fail("Must fail as file zip file is corrupted");
        } catch (BadZipException e) {
           assertNotNull(e.getMessage());
        }
    }

    @Test
    public void unpackZipLimits1() throws Exception {
        unpackZipLimitsInner(new FileCountZipRestriction(1));
    }

    @Test
    public void unpackZipLimits2() throws Exception {
        unpackZipLimitsInner(new FileCountZipRestriction(1));
    }

    @Test
    public void invalidFile() throws Exception {
        testCreateInvalidFile("");
        testCreateInvalidFile("/");
        testCreateInvalidFile("-");
        testCreateInvalidFile("~abc.jpg");
        testCreateInvalidFile("/folder/-/.jpg");
        testCreateInvalidFile("/folder/~/.jpg");
    }

    @Test
    public void checkExists() throws IOException {
        createTestFile("test1");
        createTestFile("dir/test2");

        FileSystem fileSystem = newTestFileSystem();
        assertTrue(fileSystem.checkExist(""));
        assertTrue(fileSystem.checkExist("test1"));
        assertTrue(fileSystem.checkExist("", "test1"));
        assertTrue(fileSystem.checkExist("dir/test2"));
        assertTrue(fileSystem.checkExist("dir", "test2"));

        assertFalse(fileSystem.checkExist("test2"));
        assertFalse(fileSystem.checkExist("dir", "test1"));
        assertFalse(fileSystem.checkExist("wrong"));
    }

    @Test
    public void getFileList() throws IOException {
        createTestFile("test1");
        createTestFile("test2");

        createTestFile("dir/test11");
        createTestFile("dir/test12");
        createTestFile("dir/test13");
        createTestFile("dir/.test~forostmp", 128);
        createTestDir("dir2");

        FileSystem fileSystem = newTestFileSystem();
        List<FileInfo> list;

        list = fileSystem.getFileList("");
        assertNotNull(list);
        assertEquals(4, list.size());
        Collections.sort(list, new FileInfoComparator());
        assertEquals("dir", list.get(0).getName());
        assertEquals("dir2", list.get(1).getName());
        assertEquals("test1", list.get(2).getName());
        assertEquals("test2", list.get(3).getName());

        list = fileSystem.getFileList("dir");
        assertNotNull(list);
        assertEquals(3, list.size());

        list = fileSystem.getFileList("dir2");
        assertNotNull(list);
        assertEquals(0, list.size());

        try {
            fileSystem.getFileList("wrong");
            fail();
        } catch (IOException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fileSystem.getFileList("../wrong");
            fail();
        } catch (PathIsNotAccessibleException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void delete() throws IOException {
        createTestFile("test1");
        createTestFile("test2");

        createTestFile("dir/test11");
        createTestFile("dir/test12");
        createTestFile("dir/test13");
        createTestDir("dir2");

        FileSystem fileSystem = newTestFileSystem();
        assertTrue(fileSystem.delete("test1"));
        assertFalse(pathProvider.getPath("test1").exists());

        assertTrue(fileSystem.delete("", "test2"));
        assertFalse(pathProvider.getPath("test2").exists());

        assertTrue(fileSystem.delete("dir", "test11"));
        assertFalse(pathProvider.getPath("dir", "test11").exists());

        assertTrue(fileSystem.delete("dir/test12"));
        assertFalse(pathProvider.getPath("dir", "test12").exists());

        assertTrue(fileSystem.delete("dir"));
        assertFalse(pathProvider.getPath("dir").exists());

        assertFalse(fileSystem.delete("wrong/test"));
    }

    @Test
    public void openFileRead() throws IOException, BadNameException {
        FileSystem fileSystem = newTestFileSystem();
        String testFileName = testCurrentDir + "test.file";
        File testFile = createTestFile(testFileName);

        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            is = fileSystem.readFile(testFileName);
            assertNotNull(is);
            IOUtils.copy(is, baos);
            assertEquals(testFile.length(), baos.toByteArray().length);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(baos);
        }

        try {
            fileSystem.readFile("wrong.file");
            fail();
        } catch (IOException e) {
            assertNotNull(e.getMessage());
        }

        fileSystem.createFolder("", "anotherRoot");
        FileSystem anotherFileSystem = new FileSystem(
                new SimplePathProvider(pathProvider.getPath("anotherRoot"), Collections.EMPTY_LIST));
        try {
            anotherFileSystem.readFile("../" + testFileName);
            fail();
        } catch (PathIsNotAccessibleException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void openFileWrite() throws IOException {
        FileSystem fileSystem = newTestFileSystem();
        String testFileName = testCurrentDir + "newdir/test.file";

        OutputStream os = fileSystem.openFile(testFileName);
        os.write(1);
        os.close();

        assertTrue(pathProvider.getPath(testFileName).exists());

    }

    @Test
    public void parent() throws IOException {
        FileSystem fileSystem = newTestFileSystem();

        assertEquals("dir1/dir2", fileSystem.getParent("dir1/dir2/file"));
        assertEquals("dir", fileSystem.getParent("dir/file"));
        assertEquals("", fileSystem.getParent("file"));
        try {
            fileSystem.getParent("");
            fail();
        } catch (BadNameException e) {
            assertNotNull(e);
        }
    }

    private void testCreateInvalidFile(String fileName) throws Exception {
        FileSystem fileSystem = newTestFileSystem();
        try {
            fileSystem.writeFile(testCurrentDir, fileName,  null);
            fail("Must fail as file is invalid :" + fileName);
        } catch (BadFileNameException | BadFolderNameException e) {
            // OK
        }
    }

    private FileSystem newTestFileSystem() throws IOException {
        return new FileSystem(pathProvider);
    }

    private static class FileInfoComparator implements Comparator<FileInfo> {
        public int compare(FileInfo o1, FileInfo o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private void unpackZipLimitsInner(ZipRestriction restriction) throws Exception {
        FileSystem fileSystem = newTestFileSystem();
        fileSystem.setZipRestriction(restriction);

        InputStream zipStream = getClass().getResourceAsStream("testCreatives.zip");

        boolean succeeded = false;
        try {
            fileSystem.unpackStream("somedir", "test", zipStream, UnpackOptions.DEFAULT);
        } catch (TooManyEntriesZipException e) {
            succeeded = true;
        }

        assertTrue(succeeded);

        File[] list = pathProvider.getPath("somedir", "test").listFiles();
        assertNull(list);
    }
}
