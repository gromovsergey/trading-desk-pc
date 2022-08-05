package app.programmatic.ui.file.service;

import org.springframework.web.multipart.MultipartFile;
import app.programmatic.ui.creative.dao.model.CreativeImage;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface FileService {
    String uploadZipToCreativesRoot(MultipartFile multipartFile, Long accountId) throws IOException;

    Boolean checkExist(MultipartFile file, Long accountId) throws IOException;

    String uploadToCreativesRoot(MultipartFile multipartFile, Long accountId, String parentDir) throws IOException;

    String uploadToIoRoot(MultipartFile multipartFile, Long accountId, Long entityId, String parentDir) throws IOException;

    String uploadToIoRootAsAdmin(MultipartFile multipartFile, Long accountId, Long entityId, String parentDir) throws IOException;

    List<String> listFromIoRoot(Long accountId, Long entityId, String parentDir) throws IOException;

    List<String> listFromIoRootAsAdmin(Long accountId, Long entityId, String parentDir) throws IOException;

    byte[] downloadFromIoRoot(Long accountId, Long entityId, String path);

    byte[] downloadFromIoRootAsAdmin(Long accountId, Long entityId, String path);

    void deleteFromIoRoot(Long accountId, Long entityId, String path) throws IOException;

    void deleteFromIoRootAsAdmin(Long accountId, Long entityId, String path) throws IOException;

    List<String> accountDocumentsList(Long accountId) throws IOException;

    String uploadAccountDocument(MultipartFile multipartFile, Long accountId) throws IOException;

    byte[] downloadAccountDocument(Long accountId, String path);

    void deleteAccountDocument(Long accountId, String path) throws IOException;

    List<Boolean> checkAccountDocuments(List<Long> accountIds) throws IOException;

    void uploadChannelReport(MultipartFile multipartFile, Long accountId) throws IOException;

    byte[] downloadChannelReport(Long accountId, String path);

    List<String> channelReportList(Long accountId) throws IOException;
}
