package app.programmatic.ui.fileNew.service;

import app.programmatic.ui.fileNew.model.UploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;

public interface FileServiceCreative {

    String uploadZip(MultipartFile multipartFile, Long agencyId, Long accountId, Long creativeId) throws IOException;

    UploadFileResponse uploadFile(MultipartFile multipartFile, Long agencyId, Long accountId, Long creativeId) throws IOException;

    String createParentFilePath(Long agencyId, Long accountId, Long creativeId);

    void moveFiles(String fromFileDir, String toFileDir) throws IOException;
}
