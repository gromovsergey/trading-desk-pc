package com.foros.session.fileman;

import com.foros.model.fileman.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileManager {
    void createFolder(String dir, String file) throws IOException;

    void createFile(String dir, String file, InputStream is) throws IOException;

    void unpackStream(String dir, String file, InputStream is) throws IOException;

    void unpackStream(String dir, String file, InputStream is, UnpackOptions options) throws IOException;

    boolean delete(String dir, String file) throws IOException;

    ContentSource readFile(String file);

    ContentSource readFile(String dir, String file);

    List<FileInfo> getFileList(String dir) throws IOException;

    FileInfo getFileInfo(String file) throws IOException;

    FileInfo getFileInfo(String dir, String file) throws IOException;

    boolean checkExist(String dir, String name) throws IOException;

    boolean checkExist(String fileName) throws IOException;

    File getFile(String name) throws IOException;

    String getRootPath();

    boolean renameTO(String oldFileName, String newFileName);

    Long getAuditObjectId(String path);

    public enum Folder {
        Templates(1L);

        private long id;

        Folder(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public String getKey() {
            return "admin.fileManager." + name();
        }

        public static Folder valueOf(Long id) {
            for (Folder folder : values()) {
                if (folder.id == id) {
                    return folder;
                }
            }

            return null;
        }
    }
}
