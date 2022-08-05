package app.programmatic.ui.fileNew.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    String storeFile(MultipartFile file, String parentDir);

    void moveFile(String fromFile, String toFile);

    Path getFullPath(String path);
}
