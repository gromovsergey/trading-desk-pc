package com.foros.session.fileman;

import com.foros.model.fileman.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileManagerImpl implements FileManager {
    private FileSystem fileSystem;

    public FileManagerImpl(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void createFolder(String dir, String file) throws IOException {
        fileSystem.createFolder(dir, file);
    }

    @Override
    public void createFile(String dir, String file, InputStream is) throws IOException {
        fileSystem.writeFile(dir, file, is);
    }

    @Override
    public void unpackStream(String dir, String file, InputStream is) throws IOException {
        fileSystem.unpackStream(dir, file, is, UnpackOptions.DEFAULT);
    }

    @Override
    public void unpackStream(String dir, String file, InputStream is, UnpackOptions options) throws IOException {
        fileSystem.unpackStream(dir, file, is, options);
    }

    @Override
    public boolean delete(String dir, String file) throws IOException {
        return fileSystem.delete(dir, file);
    }

    @Override
    public ContentSource readFile(String file) {
        return ContentSourceSupport.create(fileSystem, file);
    }

    @Override
    public ContentSource readFile(String dir, String file) {
        return readFile(FileUtils.trimPathName(dir) + file);
    }

    @Override
    public List<FileInfo> getFileList(String dir) throws IOException {
        return fileSystem.getFileList(dir);
    }

    @Override
    public FileInfo getFileInfo(String file) throws IOException {
        return fileSystem.getFileInfo(file);
    }

    @Override
    public FileInfo getFileInfo(String dir, String file) throws IOException {
        return fileSystem.getFileInfo(dir, file);
    }

    @Override
    public boolean checkExist(String dir, String name) throws IOException {
        return fileSystem.checkExist(dir, name);
    }

    public boolean checkExist(String fileName) throws IOException {
        return fileSystem.checkExist(fileName);
    }

    @Override
    public File getFile(String name) throws IOException {
        return fileSystem.getPathProvider().getPath(name);
    }

    @Override
    public String getRootPath() {
        return fileSystem.getPathProvider().getRootDir();
    }

    @Override
    public boolean renameTO(String oldFileName, String newFileName) {
        return fileSystem.renameTO(oldFileName, newFileName);
    }

    @Override
    public Long getAuditObjectId(String path) {
        return fileSystem.getAuditObjectId(path);
    }
}
