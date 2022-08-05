package com.foros.session.fileman;

import com.foros.changes.inspection.ChangeType;
import com.foros.model.fileman.FileInfo;
import com.foros.session.admin.FileManagerRestrictions;
import com.foros.session.fileman.audit.FileSystemAudit;
import com.foros.session.fileman.audit.NullFileSystemAudit;
import com.foros.session.fileman.restrictions.FileContentRestriction;
import com.foros.session.fileman.restrictions.FileNameRestriction;
import com.foros.session.fileman.restrictions.FileNameRestrictionImpl;
import com.foros.session.fileman.restrictions.FileRestriction;
import com.foros.session.fileman.restrictions.FileSizeRestriction;
import com.foros.session.fileman.restrictions.NullFileContentRestriction;
import com.foros.session.fileman.restrictions.NullFileRestriction;
import com.foros.session.fileman.restrictions.NullFileSizeRestriction;
import com.foros.session.fileman.restrictions.NullQuotaProvider;
import com.foros.session.fileman.restrictions.NullZipRestriction;
import com.foros.session.fileman.restrictions.Quota;
import com.foros.session.fileman.restrictions.QuotaProvider;
import com.foros.session.fileman.restrictions.ZipRestriction;
import com.foros.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class FileSystem {
    private PathProvider pathProvider;
    private FileRestriction fileRestriction = NullFileRestriction.INSTANCE;
    private FileSizeRestriction fileSizeRestriction = NullFileSizeRestriction.INSTANCE;
    private ZipRestriction zipRestriction = NullZipRestriction.INSTANCE;
    private QuotaProvider quotaProvider = NullQuotaProvider.INSTANCE;
    private FileNameRestriction fileNameRestriction = FileNameRestrictionImpl.INSTANCE;
    private FileTypesRestriction fileTypesRestriction = FileTypesRestrictionImpl.INSTANCE;
    private FileContentRestriction fileContentRestriction = NullFileContentRestriction.INSTANCE;
    private FileSystemAudit fileSystemAudit = NullFileSystemAudit.INSTANCE;

    private FileManagerRestrictions fileManagerRestrictions;
    private ZipEntryFilter zipEntryFilter = new RegexpZipEntryFilter();

    public FileSystem(PathProvider pathProvider) {
        if (pathProvider == null) {
            throw new NullPointerException("pathProvider");
        }
        this.pathProvider = pathProvider;
    }

    public FileRestriction getFileRestriction() {
        return fileRestriction;
    }

    public void setFileRestriction(FileRestriction fileRestriction) {
        this.fileRestriction = fileRestriction;
    }


    public FileSizeRestriction getFileSizeRestriction() {
        return fileSizeRestriction;
    }

    public void setFileSizeRestriction(FileSizeRestriction fileSizeRestriction) {
        this.fileSizeRestriction = fileSizeRestriction;
    }

    public PathProvider getPathProvider() {
        return pathProvider;
    }

    public void setPathProvider(PathProvider pathProvider) {
        this.pathProvider = pathProvider;
    }

    public ZipRestriction getZipRestriction() {
        return zipRestriction;
    }

    public void setZipRestriction(ZipRestriction zipRestriction) {
        this.zipRestriction = zipRestriction;
    }

    public QuotaProvider getQuotaProvider() {
        return quotaProvider;
    }

    public void setQuotaProvider(QuotaProvider quotaProvider) {
        this.quotaProvider = quotaProvider;
    }

    public FileNameRestriction getFileNameRestriction() {
        return fileNameRestriction;
    }

    public void setFileNameRestriction(FileNameRestriction fileNameRestriction) {
        this.fileNameRestriction = fileNameRestriction;
    }

    public FileContentRestriction getFileContentRestriction() {
        return fileContentRestriction;
    }

    public void setFileContentRestriction(FileContentRestriction fileContentRestriction) {
        this.fileContentRestriction = fileContentRestriction;
    }

    public FileSystemAudit getFileSystemAudit() {
        return fileSystemAudit;
    }

    public void setFileSystemAudit(FileSystemAudit fileSystemAudit) {
        this.fileSystemAudit = fileSystemAudit;
    }

    public List<FileInfo> getFileList(String dirName) throws IOException, BadNameException {
        fileNameRestriction.checkFolderName(dirName);
        File dir = pathProvider.getPath(dirName);

        if (!dir.isDirectory()) {
            throw new IOException("Specified path is not a valid directory");
        }

        List<FileInfo> fileList = new LinkedList<FileInfo>();
        File[] files = dir.listFiles(new ExcludeTempFileFilter());
        if (files != null) {
            for (File file : files) {
                fileList.add(new FileInfo(file));
            }
        }

        return fileList;
    }

    public void createFolder(String currDir, String createDir) throws IOException, FileManagerException {
        try {
            fileNameRestriction.checkFolderName(currDir, createDir, false);
            File newDir = pathProvider.getPath(currDir, createDir);
            Quota quota = quotaProvider.get(pathProvider);
            createFolder(newDir, quota);
        } finally {
            fileSystemAudit.log();
        }
    }

    private void createFolder(File newDir, Quota quota) throws IOException {
        fileRestriction.checkNewFolderAllowed(quota, pathProvider, newDir);

        if(!newDir.mkdirs()) {
            throw new IOException("Can't create directory: " + newDir);
        }

        fileSystemAudit.add(ChangeType.ADD, newDir);
    }

    public void writeFile(String dir, String name, InputStream is) throws IOException, FileManagerException {
        try {
            fileNameRestriction.checkFileName(dir, name, false);
            File file = pathProvider.getPath(dir, name);
            writeFile(file, is, quotaProvider.get(pathProvider));
        } finally {
            fileSystemAudit.log();
        }
    }

    private void writeFile(File file, InputStream is, Quota quota) throws IOException {
        fileRestriction.checkNewFileAllowed(quota, pathProvider, file);

        assureDirExists(file.getParentFile());

        File backupFile = null;
        if (file.exists()) {
            backupFile = new File(file.getParentFile(), file.getName() + System.currentTimeMillis());
            org.apache.commons.io.FileUtils.moveFile(file, backupFile);
        }

        File tempFile = SharedFileOutputStream.createTempFile(file);
        try (SharedFileOutputStream os = new SharedFileOutputStream(file, tempFile, fileSystemAudit)) {
            OutputStream restricted = fileSizeRestriction.wrap(os, quota, file);
            IOUtils.copy(is, restricted);
            restricted.flush();
            os.commitOnClose();
        }

        fileContentRestriction.check(file);
        if ((fileManagerRestrictions == null || !fileManagerRestrictions.canManage()) &&
            !fileTypesRestriction.check(file, pathProvider.getAllowedFileTypes())) {
            FileUtils.deleteFile(file);
            if (backupFile != null) {
                org.apache.commons.io.FileUtils.moveFile(backupFile, file);
                FileUtils.deleteFile(backupFile);
            }
            throw new FileContentException(file.getName(), file.getPath());
        }

        if (backupFile != null) {
            FileUtils.deleteFile(backupFile);
        }
    }

    public String[] list(String currDir) {
        File path = pathProvider.getPath(currDir);
        return path.list();
    }

    public boolean delete(String currDir, String file) {
        try {
            fileNameRestriction.checkFileName(currDir, file, false);
            File path = pathProvider.getPath(currDir, file);
            return delete(path);
        } finally {
            fileSystemAudit.log();
        }
    }

    public boolean delete(String fname) {
        try {
            fileNameRestriction.checkFileName(fname);
            File path = pathProvider.getPath(fname);
            return delete(path);
        } finally {
            fileSystemAudit.log();
        }
    }

    private boolean delete(File path) {
        File root = pathProvider.getPath("");
        if (root.equals(path)) {
            throw new BadFileNameException("");
        }
        return FileUtils.deleteFile(path, fileSystemAudit);
    }

    public void unpackStream(String currDir, String newDir, InputStream is, UnpackOptions options) throws IOException, FileManagerException {
        Quota quota = quotaProvider.get(pathProvider);
        fileNameRestriction.checkFolderName(currDir, newDir, true);
        File outDir = pathProvider.getPath(currDir, newDir);
        String outPath = FileUtils.trimPathName(currDir) + FileUtils.trimPathName(newDir);
        File backup = null;
        ZipInputStream zis;

        try {
            try {
                if (!outDir.exists()) {
                    createFolder(outDir, quota);
                } else {
                    if (outDir.isDirectory()) {
                        if (options.isTransactional()) {
                            backup = createBackup(outDir);
                        }
                    } else if (outDir.isFile()){
                        FileUtils.deleteFile(outDir);
                    }
                }

                zis = zipRestriction.create(is);
                unpackZipStream(outPath, zis, quota);
            } catch (Exception e) {
                if (options.isTransactional()) {
                    if (backup != null) {
                        restoreBackup(backup, outDir);
                    } else {
                        FileUtils.deleteFile(outDir);
                    }
                }

                throw e;
            }
        } catch (FileManagerException fme) {
            fme.setArchiveException(true);
            throw fme;
        } catch (TooManyEntriesZipException e) {
            throw e;
        } catch (Exception e) {
            throw new BadZipException(e);
        } finally {
            fileSystemAudit.log();
            if (backup != null) {
                deleteBackup(backup);
            }
        }
    }

    private void unpackZipStream(String outPath, ZipInputStream zis, Quota quota) throws IOException {
        boolean unpackSuccess=false;
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            unpackSuccess = true;
            if (!zipEntryFilter.check(entry)) {
                continue;
            }

            String entryName = entry.getName();
            if(entry.isDirectory()) {
                fileNameRestriction.checkFolderName(outPath, entryName, false);
            } else {
                fileNameRestriction.checkFileName(outPath, entryName, false);
            }
            File newFile = pathProvider.getPath(outPath, entryName);
            if (entry.isDirectory()) {
                if (!newFile.exists()) {
                    createFolder(newFile, quota);
                }
            } else {
                writeFile(newFile, zis, quota);
            }
            zis.closeEntry();
        }
        if (!unpackSuccess){
            throw new ZipException("No entries found in zip file or the zip file is corrupted");
        }
    }

    private void deleteBackup(File backup) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(backup);
    }

    private void restoreBackup(File backup, File outDir) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(outDir);
        org.apache.commons.io.FileUtils.copyDirectory(backup, outDir, true);
        outDir.setLastModified(backup.lastModified());
    }

    private File createBackup(File outDir) throws IOException {
        File backup = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        org.apache.commons.io.FileUtils.copyDirectory(outDir, backup, true);
        backup.setLastModified(outDir.lastModified());
        return backup;
    }

    public boolean checkExist(String fileName) {
        fileNameRestriction.checkFileName(fileName);
        return pathProvider.getPath(fileName).exists();
    }

    public boolean checkExist(String currDir, String fileName) {
        fileNameRestriction.checkFileName(currDir, fileName, false);
        return pathProvider.getPath(currDir, fileName).exists();
    }

    public FileInfo getFileInfo(String name) {
        fileNameRestriction.checkFileName(name);
        return new FileInfo(pathProvider.getPath(name));
    }

    public FileInfo getFileInfo(String currDir, String fileName) {
        fileNameRestriction.checkFileName(currDir, fileName, false);
        return new FileInfo(pathProvider.getPath(currDir, fileName));
    }

    public InputStream readFile(String fileName) throws IOException {
        fileNameRestriction.checkFileName(fileName);
        File f = pathProvider.getPath(fileName);
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new IOException("Cannot open file " + f.getAbsolutePath(), e);
        }
    }

    public OutputStream openFile(String fileName) throws IOException, FileManagerException {
        fileNameRestriction.checkFileName(fileName);
        File file = pathProvider.getPath(fileName);
        Quota quota = quotaProvider.get(pathProvider);
        fileRestriction.checkNewFileAllowed(quota, pathProvider, file);
        assureDirExists(file.getParentFile());

        File tempFile = SharedFileOutputStream.createTempFile(file);
        SharedFileOutputStream os = new SharedFileOutputStream(file, tempFile, fileSystemAudit);
        OutputStream restricted = fileSizeRestriction.wrap(os, quota, file);
        os.commitOnClose();
        return restricted;
    }

    public void touch(String fileName) throws IOException {
        fileNameRestriction.checkFileName(fileName);
        File file = pathProvider.getPath(fileName);
        if (file.exists()) {
            file.setLastModified(System.currentTimeMillis());
        } else {
            assureDirExists(file.getParentFile());
            file.createNewFile();
        }
    }

    public String getParent(String path) {
        if (StringUtil.isPropertyEmpty(path) || PathProvider.PATH_SEPARATOR.equals(FileUtils.trimPathName(path))) {
            throw new BadFileNameException(path);
        }
        int pos = path.lastIndexOf(PathProvider.PATH_SEPARATOR);

        return pos > 0 ? path.substring(0, pos) : "";
    }

    private void assureDirExists(File parent) {
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    public void setFileManagerRestrictions(FileManagerRestrictions fileManagerRestrictions) {
        this.fileManagerRestrictions = fileManagerRestrictions;
    }

    public boolean lock(String lock) {
        File file = pathProvider.getPath(lock);
        try {
            return file.createNewFile();
        } catch (IOException e) {
            throw new FileManagerException(e);
        }
    }

    public boolean renameTO(String oldFileName, String newFileName) {
        return pathProvider.getPath(oldFileName).renameTo(new File(pathProvider.getRootDir() + "/" + newFileName));
    }

    public void setFileTypesRestriction(FileTypesRestriction fileTypesRestriction) {
        this.fileTypesRestriction = fileTypesRestriction;
    }

    public Long getAuditObjectId(String dir) {
        File file = pathProvider.getPath(dir);
        return fileSystemAudit.getAuditObjectId(file);
    }
}
