package com.foros.action.fileman;

import com.foros.action.BaseActionSupport;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.fileman.FileInfo;
import com.foros.session.fileman.FileManager;
import com.foros.session.fileman.FileUtils;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

public abstract class FileManagerActionSupport extends BaseActionSupport {
    @EJB
    protected ConfigService configService;

    private List<String> currDir;
    private List<FileForm> fileList;

    private String mode;
    private Long accountId;
    private String fileTypes;
    private String currDirStr;
    protected FileManager fileManager;
    private static Logger logger = Logger.getLogger(FileManagerActionSupport.class.getName());

    public void setCurrDirStr(String currDirStr) {
        this.currDirStr = currDirStr;
    }

    public String getCurrDirStr() {
        return currDirStr;
    }

    public List<String> getCurrDir() {
        if (currDir != null) {
            return currDir;
        }

        if (StringUtil.isPropertyEmpty(currDirStr)) {
            currDir = new LinkedList<String>();
        } else {
            currDir = Arrays.asList(currDirStr.split("/"));
        }

        return currDir;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(String fileTypes) {
        this.fileTypes = fileTypes;
    }

    public String getFilePrefix() {
        if (FileManagerHelper.MODE_TEMPLATE.equals(mode)) {
            return "/";
        } else {
            return "";
        }
    }

    public List<FileForm> getFileList() throws IOException {
        if (fileList != null) {
            return fileList;
        }

        List<String> allowedFileTypes = null;

        if (FileManagerHelper.MODE_CREATIVE.equals(getMode()) || FileManagerHelper.MODE_PUBLISHER_ACCOUNT.equals(getMode())) {
            allowedFileTypes = Arrays.asList(StringUtil.splitByComma(getFileTypes()));
        }

        List<FileInfo> flist = fileManager.getFileList(getCurrDirStr());
        fileList = new ArrayList<FileForm>(flist.size());

        for (FileInfo f : flist) {
            FileForm fileForm = new FileForm();
            populateForm(fileForm, f, allowedFileTypes);
            fileList.add(fileForm);
        }

        Collections.sort(fileList, FileForm.FILE_COMPARATOR);

        return fileList;
    }

    protected FileManager getFileManager() {
        if (fileManager == null) {
            prepareFileManager();
        }
        return fileManager;
    }

    protected void prepareFileManager() {
        fileManager = FileManagerHelper.getFileManager(mode, accountId);
    }

    private void populateForm(FileForm form, FileInfo file, List<String> allowedFileTypes) {
        form.setName(file.getName());
        form.setDirectory(file.isDirectory());

        if (file.getTime() != 0L) {
            form.setDate(DateHelper.formatDateTimeLong(file.getTime()));
        } else {
            logger.log(Level.WARNING, "File {0}. Metadata access problem detected.", file.getName());
        }

        form.setLength(String.valueOf(file.getLength()));
        form.setFiletype(file.getMimeType());

        String mimeType = FileUtils.getMimeTypeByExtension(file.getName());

        List<String> allowedMimeTypes = FileUtils.fileTypesToMimeTypes(allowedFileTypes);

        if (StringUtil.isPropertyNotEmpty(mode)) {
            form.setAllowedToSelect(allowedFileTypes == null || allowedFileTypes.isEmpty() || allowedMimeTypes.contains(mimeType));
        } else {
            form.setAllowedToSelect(false);
        }
    }

    private String getCustomizationDir() {
        String custFolder = configService.get(ConfigParameters.CUSTOMIZATIONS_FOLDER);
        String fmRoot = configService.get(ConfigParameters.ADMIN_FILE_MANAGER_FOLDER);

        return custFolder.startsWith(fmRoot) ? custFolder.substring(fmRoot.length() + 1) : custFolder;
    }

    private File getExistingDirectory(File file) {
        if (file.getPath().equals(fileManager.getRootPath())) {
            return file;
        }

        if (file.exists()) {
            return file;
        } else {
            return getExistingDirectory(file.getParentFile());
        }
    }

    protected void refreshCurrentDirectory(String fileName) throws IOException {
        File currentDirectory = fileManager.getFile(fileName);
        File existingDirectory = getExistingDirectory(currentDirectory);
        if (StringUtil.equalsWithIgnoreCase(existingDirectory.getPath(), fileManager.getRootPath())) {
            setCurrDirStr("");
        } else {
            setCurrDirStr(existingDirectory.getPath().substring(fileManager.getRootPath().length() + 1));
        }
    }

    public Long getAuditObjectId() {
        return fileManager.getAuditObjectId(currDirStr);
    }
}
