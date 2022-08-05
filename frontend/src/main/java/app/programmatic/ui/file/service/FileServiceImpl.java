package app.programmatic.ui.file.service;

import com.foros.rs.client.model.file.RootLocation;
import com.foros.rs.client.model.restriction.Predicates;
import com.foros.rs.client.service.FilesService;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.foros.service.ForosFileService;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import org.apache.http.entity.ByteArrayEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


@Service
public class FileServiceImpl implements FileService {
    private static final Pattern HTML_PATTERN = Pattern.compile(".*(?i)[.]html$");

    @Autowired
    private ForosFileService forosFileService;

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.creative.validation.ForosCreativesUploadViolationsServiceImpl")
    public String uploadZipToCreativesRoot(MultipartFile multipartFile, Long accountId) throws IOException {
        FileInfo fileInfo = toFileInfo(multipartFile);

        String dirName = !fileInfo.getName().contains(".") ? fileInfo.getName() :
                fileInfo.getName().substring(0, fileInfo.getName().lastIndexOf('.'));

        forosFileService.getFilesService().upload(
                accountId,
                null,
                "/" + dirName,
                RootLocation.CREATIVES,
                new ByteArrayEntity(fileInfo.contents)
        );

        return dirName;
    }

    @Override
    public Boolean checkExist(MultipartFile file, Long accountId) throws IOException {
        FileInfo fileInfo = toFileInfo(file);
        String fileName = !fileInfo.getName().contains(".") ? fileInfo.getName() :
                fileInfo.getName().substring(0, fileInfo.getName().lastIndexOf('.'));

        Predicates result = forosFileService.getFilesService().checkExist(fileName, accountId, "", RootLocation.CREATIVES);
        return result.getPredicates().get(0);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public String uploadToCreativesRoot(MultipartFile multipartFile, Long accountId, String parentDir) throws IOException {
        FileInfo fileInfo = toFileInfo(multipartFile);

        String result = tryUploadZipToCreativesRoot(forosFileService.getFilesService(), fileInfo, accountId, parentDir);
        if (result != null) {
            return result;
        }
        // It was not a ZIP file:

        uploadToForos(forosFileService.getFilesService(),
                Collections.singleton(fileInfo),
                accountId,
                null,
                RootLocation.CREATIVES,
                parentDir);
        return fileInfo.getName();
    }

    private String tryUploadZipToCreativesRoot(FilesService filesService, FileInfo fileInfo, Long accountId, String parentDir) throws IOException {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(fileInfo.getContents()))) {
            ZipEntry zipEntry = zipStream.getNextEntry();

            boolean isZip = false;
            String rootHtmlFile = null;
            while (zipEntry != null) {
                isZip = true;
                if (HTML_PATTERN.matcher(zipEntry.getName()).matches()) {
                    rootHtmlFile = zipEntry.getName();
                    break;
                }

                zipEntry = zipStream.getNextEntry();
            }

            if (!isZip) {
                return null;
            }

            if (rootHtmlFile == null) {
                ConstraintViolationBuilder.throwExpectedException("file.error.creativesZip.rootNotFound", fileInfo.getName());
            }

            String newParentDir = !fileInfo.getName().contains(".") ? fileInfo.getName() :
                    fileInfo.getName().substring(0, fileInfo.getName().lastIndexOf('.'));
            filesService.upload(
                    accountId,
                    null,
                    parentDir == null ? newParentDir : (parentDir + "/" + newParentDir),
                    RootLocation.CREATIVES,
                    new ByteArrayEntity(fileInfo.contents)
            );

            return newParentDir + "/" + rootHtmlFile;

        } finally {}
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public String uploadToIoRoot(MultipartFile multipartFile, Long accountId, Long entityId, String parentDir) throws IOException {
        return uploadToIoRoot(forosFileService.getFilesService(), multipartFile, accountId, entityId, parentDir);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public String uploadToIoRootAsAdmin(MultipartFile multipartFile, Long accountId, Long entityId, String parentDir) throws IOException {
        return uploadToIoRoot(forosFileService.getAdminFilesService(), multipartFile, accountId, entityId, parentDir);
    }

    private String uploadToIoRoot(FilesService filesService, MultipartFile multipartFile, Long accountId, Long entityId,
                                  String parentDir) throws IOException {
        FileInfo fileInfo = toFileInfo(multipartFile);
        uploadToForos(filesService,
                    Collections.singleton(fileInfo),
                    accountId,
                    entityId,
                    RootLocation.IO,
                    parentDir);
        return fileInfo.getName();
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public List<String> listFromIoRoot(Long accountId, Long entityId, String parentDir) throws IOException {
        return listFromIoRoot(forosFileService.getFilesService(), accountId, entityId, parentDir);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public List<String> listFromIoRootAsAdmin(Long accountId, Long entityId, String parentDir) throws IOException {
        return listFromIoRoot(forosFileService.getAdminFilesService(), accountId, entityId, parentDir);
    }

    private List<String> listFromIoRoot(FilesService filesService, Long accountId, Long entityId, String parentDir)
            throws IOException {
        return filesService.listDir(accountId, entityId, parentDir, RootLocation.IO).getFiles();
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public byte[] downloadFromIoRoot(Long accountId, Long entityId, String path) {
        return downloadFromIoRoot(forosFileService.getFilesService(), accountId, entityId, path);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public byte[] downloadFromIoRootAsAdmin(Long accountId, Long entityId, String path) {
        return downloadFromIoRoot(forosFileService.getAdminFilesService(), accountId, entityId, path);
    }

    private byte[] downloadFromIoRoot(FilesService filesService, Long accountId, Long entityId, String path) {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {

            filesService.download(accountId, entityId, path, RootLocation.IO, result);
            return result.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public void deleteFromIoRoot(Long accountId, Long entityId, String path) throws IOException {
        deleteFromIoRoot(forosFileService.getFilesService(), accountId, entityId, path);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public void deleteFromIoRootAsAdmin(Long accountId, Long entityId, String path) throws IOException {
        deleteFromIoRoot(forosFileService.getAdminFilesService(), accountId, entityId, path);
    }

    private void deleteFromIoRoot(FilesService filesService, Long accountId, Long entityId, String path) throws IOException {
        filesService.delete(accountId, entityId, path, RootLocation.IO);
    }

    private FileInfo toFileInfo(MultipartFile file) throws IOException {
        String fileName = new String (file.getOriginalFilename().getBytes ("iso-8859-1"),"UTF-8"); // another solution is to use CommonsMultipartResolver with apache file upload and use setDefaultEncoding
        Path filePath = Paths.get(fileName);
        return new FileInfo(filePath.getFileName().toString(), file.getBytes());
    }

    private void uploadToForos(FilesService filesService, Collection<FileInfo> files, Long accountId, Long entityId,
                             RootLocation rootLocation, String parentDir) throws IOException {
        if (!files.isEmpty()) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ZipOutputStream zos = new ZipOutputStream(baos)) {

                for (FileInfo file : files) {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    zos.write(file.getContents());
                    zos.closeEntry();
                }

                zos.flush();

                filesService.upload(
                        accountId,
                        entityId,
                        parentDir,
                        rootLocation,
                        new ByteArrayEntity(baos.toByteArray())
                );
            }
        }
    }

    private static class FileInfo {
        private final String name;
        private final byte[] contents;

        public FileInfo(String name, byte[] contents) {
            this.name = name;
            this.contents = contents;
        }

        public String getName() {
            return name;
        }

        public byte[] getContents() {
            return contents;
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public List<String> accountDocumentsList(Long accountId) throws IOException {
        return forosFileService.getFilesService().accountDocumentsList(accountId).getFiles();
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public String uploadAccountDocument(MultipartFile multipartFile, Long accountId) throws IOException {
        FileInfo fileInfo = toFileInfo(multipartFile);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            zos.putNextEntry(new ZipEntry(fileInfo.getName()));
            zos.write(fileInfo.getContents());
            zos.closeEntry();
            zos.flush();

            forosFileService.getFilesService().uploadAccountDocument(
                    accountId,
                    new ByteArrayEntity(baos.toByteArray())
            );
        }

        return fileInfo.getName();
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public byte[] downloadAccountDocument(Long accountId, String path) {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            forosFileService.getFilesService().downloadAccountDocument(accountId, path, result);
            return result.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public void deleteAccountDocument(Long accountId, String path) throws IOException {
        forosFileService.getFilesService().deleteAccountDocument(accountId, path);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public List<Boolean> checkAccountDocuments(List<Long> accountIds) throws IOException {
        return forosFileService.getFilesService().checkAccountDocuments(accountIds).getPredicates();
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public void uploadChannelReport(MultipartFile multipartFile, Long accountId) throws IOException {
        FileInfo fileInfo = toFileInfo(multipartFile);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            zos.putNextEntry(new ZipEntry(fileInfo.getName()));
            zos.write(fileInfo.getContents());
            zos.closeEntry();
            zos.flush();

            forosFileService.getFilesService().uploadChannelReport(
                    accountId,
                    new ByteArrayEntity(baos.toByteArray())
            );
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public byte[] downloadChannelReport(Long accountId, String path) {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            forosFileService.getFilesService().downloadChannelReport(accountId, path, result);
            return result.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.file.validation.ForosFileViolationsServiceImpl")
    public List<String> channelReportList(Long accountId) throws IOException {
        return forosFileService.getFilesService().channelReportList(accountId).getFiles();
    }
}
