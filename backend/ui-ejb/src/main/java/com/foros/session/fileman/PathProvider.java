package com.foros.session.fileman;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PathProvider {
    String PATH_SEPARATOR = "/";

    File getPath(String currDir, String fileName);

    File getPath(String fileName);

    boolean isAccessiblePath(File path) throws IOException;

    PathProvider getNested(String path);

    PathProvider getNested(String path, OnNoProviderRoot mode);

    FileSystem createFileSystem();
    
    List<String> getAllowedFileTypes();

    String getRootDir();
}
