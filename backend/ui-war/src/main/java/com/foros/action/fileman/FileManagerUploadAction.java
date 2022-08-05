package com.foros.action.fileman;

import com.foros.framework.CustomFileUploadInterceptor;
import com.foros.session.fileman.AccountSizeExceededException;
import com.foros.session.fileman.BadFileNameException;
import com.foros.session.fileman.BadFolderNameException;
import com.foros.session.fileman.BadZipException;
import com.foros.session.fileman.FileContentException;
import com.foros.session.fileman.FileManagerException;
import com.foros.session.fileman.FileSizeException;
import com.foros.session.fileman.ImageDimensionException;
import com.foros.session.fileman.TooManyDirLevelsException;
import com.foros.session.fileman.TooManyEntriesZipException;
import com.foros.util.StringUtil;

import org.apache.struts2.interceptor.RequestAware;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipFile;

import static com.foros.config.ConfigParameters.DEFAULT_MAX_FILES_IN_ZIP;
import static com.foros.config.ConfigParameters.DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION;
import static com.foros.config.ConfigParameters.DEFAULT_MAX_UPLOAD_SIZE;
import static com.foros.config.ConfigParameters.UPLOAD_MAX_DIR_LEVELS;

public class FileManagerUploadAction extends FileManagerActionSupport implements RequestAware {
    private File fileToUpload;
    private String fileToUploadFileName;
    private Map<String, Object> request;

    public String upload() {
        prepareFileManager();

        try {
            if (!fileManager.checkExist(getCurrDirStr())) {
                addFieldError("resourceNotFound", getText("errors.fileOrFolder.notExist"));
                refreshCurrentDirectory(getCurrDirStr());
                return INPUT;
            }

            if (request.get(CustomFileUploadInterceptor.ATTRIBUTE_MAX_LENGTH_EXCEEDED) != null) {
                addFieldError("fileToUpload", getText("errors.file.sizeExceeded"));
                return INPUT;
            }

            long size = fileToUpload.length();
            if (size > configService.get(DEFAULT_MAX_UPLOAD_SIZE)) {
                addFieldError("fileToUpload", getText("errors.file.sizeExceeded"));
                return INPUT;
            }

            saveUploadedFile(fileToUpload);
        } catch (TooManyEntriesZipException e) {
            addFieldError("fileToUpload", getText("errors.file.archiveTooManyEntries"));
        } catch (BadZipException e) {
            addFieldError("fileToUpload", getText("errors.file.badArchive"));
        } catch (FileSizeException e) {
            String key = e.isArchiveException() ? "errors.file.archiveSizeExceeded" : "errors.file.fileSizeExceeded";
            addFieldError("fileToUpload", getText(key, new String[] {String.valueOf(e.getThreshold())}));
        } catch (AccountSizeExceededException e) {
            String key = e.isArchiveException() ? "errors.file.archiveAccSizeExceeded" : "errors.file.accSizeExceeded";
            addFieldError("fileToUpload", getText(key));
        } catch (BadFileNameException e) {
            addFieldError("fileToUpload", getText("errors.file.badFileName"));
        } catch (BadFolderNameException e) {
            addFieldError("fileToUpload", getText("errors.file.badFolderName"));
        } catch (TooManyDirLevelsException e) {
            Integer maxDirLevels = configService.get(UPLOAD_MAX_DIR_LEVELS);
            addFieldError("fileToUpload", getText("errors.file.tooManyLevels", new String[] {maxDirLevels.toString()}));
        } catch (FileContentException e) {
            if (e.isExtensionCorrespondsContent()) {
                addFieldError("fileToUpload", getText("errors.file.invalidContent"));
            } else {
                addFieldError("fileToUpload", getText("fileman.contentCheckFailed", new String[] { e.getFileName() }));
            }
        } catch (ImageDimensionException e) {
            Integer maxDimension = configService.get(DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION);
            addFieldError("fileToUpload", getText("errors.file.imageDimensionsExceeded", new String[] {maxDimension.toString()}));
        } catch (IOException e) {
            addFieldError("fileToUpload", getText("errors.file.uploadAgain"));
        }

        if (hasErrors()) {
            return INPUT;
        }

        return SUCCESS;
    }

    public File getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(File fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public String getFileToUploadFileName() {
        return fileToUploadFileName;
    }

    public void setFileToUploadFileName(String fileToUploadFileName) {
        this.fileToUploadFileName = fileToUploadFileName;
    }

    private void saveUploadedFile(File fileToUpload) throws IOException, FileManagerException {
        if (fileToUploadFileName.toLowerCase().endsWith(".zip")) {
            int maxFiles = configService.get(DEFAULT_MAX_FILES_IN_ZIP);
            try (ZipFile zipFile = new ZipFile(fileToUpload)) {
                if (zipFile.size() > maxFiles) {
                    throw new TooManyEntriesZipException("Number of files in the archive more than " + maxFiles);
                }
            }

            fileManager.unpackStream(getCurrDirStr(), StringUtil.removeZipSuffix(fileToUploadFileName), createInputStream(fileToUpload));
        } else {
            fileManager.createFile(getCurrDirStr(), fileToUploadFileName, createInputStream(fileToUpload));
        }
    }

    private InputStream createInputStream(File fileToUpload) throws FileNotFoundException {
        InputStream inputStream;
        if (fileToUpload.length() == 0) {
            inputStream = new ByteArrayInputStream(new byte[0]);
        } else {
            inputStream = new FileInputStream(fileToUpload);
        }
        return inputStream;
    }

    @Override
    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }
}
