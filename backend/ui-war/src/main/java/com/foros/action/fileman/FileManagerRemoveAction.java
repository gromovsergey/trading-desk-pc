package com.foros.action.fileman;

import com.foros.util.StringUtil;

import java.io.IOException;

public class FileManagerRemoveAction extends FileManagerActionSupport {
    private String fileToDelete;

    public String remove() {
        prepareFileManager();
        if (StringUtil.isPropertyNotEmpty(fileToDelete)) {
            try {
                if (!fileManager.checkExist(getCurrDirStr(), fileToDelete)) {
                    addFieldError("resourceNotFound", getText("errors.fileOrFolder.notExist"));
                    refreshCurrentDirectory(getCurrDirStr());
                    return INPUT;
                }

                boolean isDeleted = FileManagerHelper.getFileManager(getMode(), getAccountId()).delete(getCurrDirStr(), fileToDelete);
                if (!isDeleted) {
                    addFieldError("fileToDelete", getText("errors.file.delete"));
                }
            } catch (IOException e) {
                addFieldError("fileToDelete", getText("errors.file.delete"));
            }
        }

        if (hasErrors()) {
            return INPUT;
        }

        return SUCCESS;
    }

    public String getFileToDelete() {
        return fileToDelete;
    }

    public void setFileToDelete(String fileToDelete) {
        this.fileToDelete = fileToDelete;
    }


}
