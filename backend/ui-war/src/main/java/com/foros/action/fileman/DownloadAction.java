package com.foros.action.fileman;

import com.foros.action.download.DownloadFileActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.util.StringUtil;

import java.io.IOException;

public class DownloadAction extends DownloadFileActionSupport {
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @ReadOnly
    public String download() {
        prepareFileManager();

        try {
            if (!fileManager.checkExist(getCurrDirStr(), file)) {
                addFieldError("resourceNotFound", getText("errors.fileOrFolder.notExist"));
                refreshCurrentDirectory(getCurrDirStr());
                return INPUT;
            }

            setContentSource(FileManagerHelper.getFileManager(getMode(), getAccountId()).readFile(StringUtil.isPropertyNotEmpty(getCurrDirStr()) ? getCurrDirStr() + file : file));
        } catch (IOException e) {
            addFieldError("fileManagerError", getText("errors.fileManager"));
            return INPUT;
        }
        return SUCCESS;
    }
}
