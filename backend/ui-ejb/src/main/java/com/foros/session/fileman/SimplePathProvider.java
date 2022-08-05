package com.foros.session.fileman;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimplePathProvider implements PathProvider {
    public static final Logger logger = Logger.getLogger(PathProviderUtil.class.getName());

    private File rootDir;
    private List<String> allowedFileTypes;

    public SimplePathProvider(String rootPath, List<String> allowedFileTypes) throws IOException {
        this(new File(FilenameUtils.normalize(rootPath)), allowedFileTypes);
    }

    public SimplePathProvider(File rootDir, List<String> allowedFileTypes) throws IOException {
        if (!rootDir.exists()) {
            throw new FileNotFoundException("Root path: " + rootDir.toString() + " doesn't exist");
        }

        this.rootDir = rootDir.getAbsoluteFile();
        this.allowedFileTypes = allowedFileTypes;
    }

    public File getPath(String currDir, String fileName) throws BadFileNameException {
        return createFileObject(FileUtils.trimPathName(currDir) + fileName);
    }

    public File getPath(String fileName) throws BadFileNameException {
        return createFileObject(FileUtils.trimPathName(fileName));
    }

    public boolean isAccessiblePath(File path) {
        return doCheckAccessible(normalizeFile(path));
    }

    public PathProvider getNested(String path) {
        return getNested(path, OnNoProviderRoot.Fail);
    }

    public PathProvider getNested(String dir, OnNoProviderRoot mode) {
        if (dir == null) {
            throw new NullPointerException("Can't create path provider for null folder, check the config.");
        }
        PathProvider result;
        File newRoot = new File(rootDir, dir);
        try {
            if (mode == OnNoProviderRoot.AutoCreate) {
                createIfNotExists(dir);
            }
            result = new SimplePathProvider(newRoot, allowedFileTypes);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't initialize nested path provider for: " + newRoot);
            throw new FileManagerException(e);
        }
        return result;
    }

    public FileSystem createFileSystem() {
        return new FileSystem(this);
    }

    private boolean doCheckAccessible(File path) {
        return path.getPath().startsWith(rootDir.getPath());
    }

    private File createFileObject(String path) {
        path = path.replace('\u00A0', ' ');
        File file = normalizeFile(new File(rootDir, path));

        if (!doCheckAccessible(file)) {
            throw new PathIsNotAccessibleException("Path '" + file.toString() + "' is unaccessible");
        }
        return file;
    }

    private File normalizeFile(File file1) {
        String fullPath = FilenameUtils.normalize(file1.getPath());
        return new File(fullPath);
    }

    @Override
    public String toString() {
        return "Root: " + rootDir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePathProvider that = (SimplePathProvider) o;

        if (!rootDir.equals(that.rootDir)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rootDir.hashCode();
    }

    private void createIfNotExists(String dir) {
        File dirFile = getPath(dir);
        if (!dirFile.mkdirs() && !dirFile.exists()) { // second check was created for autotests
            throw new FileManagerException("Can't auto-create folder: " + dir);
        }
    }

    @Override
    public List<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    @Override
    public String getRootDir() {
        return rootDir.getPath();
    }
}
