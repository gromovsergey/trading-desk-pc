package com.foros.action.fileman;

import com.foros.framework.ReadOnly;

import java.io.IOException;

public class FileManagerAction extends FileManagerActionSupport {

    @ReadOnly
    public String fileManager() {
        prepareFileManager();
        try {
            if (!fileManager.checkExist(getCurrDirStr())) {
                addFieldError("resourceNotFound", getText("errors.fileOrFolder.notExist"));
                refreshCurrentDirectory(getCurrDirStr());
            }
        } catch (IOException e) {
            addFieldError("fileManagerError", getText("errors.fileManager"));
        }
        return SUCCESS;
    }
}
