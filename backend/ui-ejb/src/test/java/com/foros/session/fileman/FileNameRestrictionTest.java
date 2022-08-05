package com.foros.session.fileman;

import group.Restriction;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.fail;

@Category({ Unit.class, Restriction.class })
public class FileNameRestrictionTest extends AbstractFileAwareTest {
    @Test
    public void testFileOkWithBlank() {
        assertFileOkWithBlank("", "valid");
        assertFileOkWithBlank("", "valid/dir");
        assertFileOkWithBlank("valid", "dir");
        assertFileOkWithBlank(null, "valid");
        assertFileOkWithBlank(null, "still/valid");
        assertFileOkWithBlank(null, "/even/valid.file");
        assertFileOkWithBlank(null, "");
        assertFileOkWithBlank("valid/dir", "");
        assertFileOkWithNoBlank("", "valid");
        assertFileOkWithNoBlank("", "valid/dir.jpg");
        assertFileOkWithNoBlank("", "/even/valid/dir");
        assertFileOkWithNoBlank("/even/", "/valid/dir.file");
        assertFileOkWithNoBlank(null, "/still/valid");
    }

    @Test
    public void testBadFileWithBlank() {
        assertBadFileWithBlank("", "");
        assertBadFileWithBlank(null, "");
        assertBadFileWithBlank("still/invalid", "");
        assertBadFileWithBlank("even/invalid", null);
        assertBadFileWithBlank("invalid", "");
        assertBadFileWithBlank("/invalid/", null);
    }

    @Test
    public void testBadFileInFile() {
        assertBadFileInFile("", "");
        assertBadFileInFile("/bad/", "");
        assertBadFileInFile("", "/");
        assertBadFileInFile("", "-");
        assertBadFileInFile("", "~");
        assertBadFileInFile("", "?");
        assertBadFileInFile("valid/folder", "/invalid/file/\".file");
        assertBadFileInFile("/waste/folder/", "bad/file/name/-.file");
        assertBadFileInFile("/waste/folder/", "bad/file/name/~.file");
    }

    @Test
    public void testBadFolderInFile() {
        assertBadFolderInFile("folder/ /fodler", "good.file");
        assertBadFolderInFile("/here/fine/", "here/ /bad.folder");
        assertBadFolderInFile("bad/-/here", "bad/folder");
        assertBadFolderInFile("bad/~/here", "bad/folder");
        assertBadFolderInFile(null, "bad/-/folder");
        assertBadFolderInFile(null, "bad/~/folder");
    }

    @Test
    public void testFolderOkWithBlank() {
        assertFolderOkWithBlank("", "");
        assertFolderOkWithBlank(null, "");
        assertFolderOkWithBlank("", null);
        assertFolderOkWithBlank(null, null);
        assertFolderOkWithBlank("valid/folder", "");
        assertFolderOkWithBlank("", "vaild/folder/");
        assertFolderOkWithNoBlank("", "folder/");
        assertFolderOkWithNoBlank(null, "valid/folder");
        assertFolderOkWithNoBlank(null, "/valid/folder/");
        assertFolderOkWithNoBlank("fine", "/valid/folder");
        assertFolderOkWithNoBlank("", "valid/sub/folder/");
        assertFolderOkWithNoBlank("/valid/sub/folder", "ValidFolder");
    }

    @Test
    public void testBadFolder() {
        assertBadFolder(null, "-");
        assertBadFolder("invalid", "sub/ /folder");
        assertBadFolder(null, "invalid//folder/.");
        assertBadFolder("1232&^", "");
        assertBadFolder("", "&*^*h/(&*");
    }

    private void assertFileOkWithBlank(String parentDir, String fileName) {
        try {
            fileNameRestriction.checkFileName(parentDir, fileName, true);
        } catch (BadFileNameException ex) {
            fail("Should not be a bad file in file check");
        } catch (BadFolderNameException ex) {
            fail("Should not be a bad folder in file check");
        } catch (Exception ex) {
            fail("Should not be a bad file");
        }
    }

    private void assertFileOkWithNoBlank(String parentDir, String fileName) {
        try {
            fileNameRestriction.checkFileName(parentDir, fileName, false);
        } catch (BadFileNameException ex) {
            fail("Should not be a bad file in file check");
        } catch (BadFolderNameException ex) {
            fail("Should not be a bad folder in file check");
        } catch (Exception ex) {
            fail("Should not be a bad file");
        }
    }

    private void assertBadFileWithBlank(String parentDir, String fileName) {
        try {
            fileNameRestriction.checkFileName(parentDir, fileName, false);
            fail("Should throw an exception for blank");
        } catch (BadFileNameException ex) {
            // OK
        } catch (BadFolderNameException ex) {
            fail("Should not be a bad folder in file check");
        } catch (Exception ex) {
            fail("Should not be a bad file");
        }
    }

    private void assertBadFileInFile(String parentDir, String fileName) {
        try {
            fileNameRestriction.checkFileName(parentDir, fileName, false);
            fail("Should throw an error");
        } catch (BadFileNameException ex) {
            // OK
        } catch (BadFolderNameException ex) {
            fail("Should not be a bad folder in file check");
        } catch (Exception ex) {
            fail("Should not be a bad file");
        }
    }

    private void assertBadFolderInFile(String parentDir, String fileName) {
        try {
            fileNameRestriction.checkFileName(parentDir, fileName, false);
            fail("Should throw an exception");
        } catch (BadFileNameException ex) {
            fail("Should not be a bad file in file check");
        } catch (BadFolderNameException ex) {
            // OK
        } catch (Exception ex) {
            fail("Should not be a bad file");
        }
    }

    private void assertFolderOkWithBlank(String parentDir, String folderName) {
        try {
            fileNameRestriction.checkFolderName(parentDir, folderName, true);
        } catch (BadFileNameException ex) {
            fail("Should not be a bad file in folder check");
        } catch (BadFolderNameException ex) {
            fail("Should not be a bad folder in folder check");
        } catch (Exception ex) {
            fail("Should not be a bad folder");
        }
    }

    private void assertFolderOkWithNoBlank(String parentDir, String folderName) {
        try {
            fileNameRestriction.checkFolderName(parentDir, folderName, false);
        } catch (BadFileNameException ex) {
            fail("Should not be a bad file in folder check");
        } catch (BadFolderNameException ex) {
            fail("Should not be a bad folder in folder check");
        } catch (Exception ex) {
            fail("Should not be a bad folder");
        }
    }

    private void assertBadFolder(String parentDir, String folderName) {
        try {
            fileNameRestriction.checkFolderName(parentDir, folderName, false);
            fail("Should throw an exception");
        } catch (BadFileNameException ex) {
            fail("Should not be a bad file in folder check");
        } catch (BadFolderNameException ex) {
            // OK
        } catch (Exception ex) {
            fail("Should not be a bad folder");
        }
    }

}
