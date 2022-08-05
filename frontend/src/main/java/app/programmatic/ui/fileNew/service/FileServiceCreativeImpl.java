package app.programmatic.ui.fileNew.service;

import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.fileNew.model.UploadFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileServiceCreativeImpl implements FileServiceCreative {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public String uploadZip(MultipartFile file, Long agencyId, Long accountId, Long creativeId) {
        String parentDir = null;
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(file.getBytes()))) {
            ZipEntry zipEntry = zipStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && !zipEntry.getName().contains("__MACOSX")) {
                    String fileName = !zipEntry.getName().contains("/") ? zipEntry.getName() :
                            zipEntry.getName().substring(zipEntry.getName().lastIndexOf('/') + 1);
                    MultipartFile file1 = new MockMultipartFile(
                            fileName,
                            zipEntry.getName(),
                            MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            convertZipInputStreamToInputStream(zipStream, zipEntry, "UTF-8")
                    );
                    UploadFileResponse fileResponse = uploadFile(file1, agencyId, accountId, creativeId);
                    if (fileResponse.getFileParentUri() != null && !fileResponse.getFileParentUri().isEmpty())
                        parentDir = fileResponse.getFileParentUri();
                }
                zipEntry = zipStream.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parentDir;
    }

    private InputStream convertZipInputStreamToInputStream(ZipInputStream in, ZipEntry entry, String encoding) throws IOException {
        final int BUFFER = 2048;
        int count = 0;
        byte data[] = new byte[BUFFER];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((count = in.read(data, 0, BUFFER)) != -1) {
            out.write(data);
        }
        InputStream is = new ByteArrayInputStream(out.toByteArray());
        return is;
    }

    @Override
    public UploadFileResponse uploadFile(MultipartFile file, @NotNull Long agencyId, @NotNull Long accountId, @NotNull Long creativeId) throws IOException {
        String parentDir = createParentFilePath(agencyId, accountId, creativeId);
        String fileName = fileStorageService.storeFile(file, parentDir);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, parentDir,
                file.getContentType(), file.getSize());
    }

    @Override
    public String createParentFilePath(Long agencyId, Long accountId, Long creativeId) {
        String parentDir = agencyId + "/" + accountId + "/" + creativeId;
        if (creativeId == 0) {
            Long userId = authorizationService.getAuthUser().getId();
            parentDir += "/" + userId + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
        }

        return parentDir;
    }

    @Override
    public void moveFiles(String sourceDirName, String targetSourceDir) {
        File folder = new File(fileStorageService.getFullPath(sourceDirName).toString());
        File[] listOfFiles = folder.listFiles();
        Path destDir = Paths.get(fileStorageService.getFullPath(targetSourceDir).toString());
        if (listOfFiles != null)
            for (File file : listOfFiles) {
                fileStorageService.moveFile(file.toPath().toString(), destDir.resolve(file.getName()).toString());
            }
    }
}
