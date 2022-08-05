package com.foros.action.xml.sundry;

import com.foros.action.fileman.FileManagerHelper;
import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.model.FileInfo;
import com.foros.session.fileman.BadNameException;
import com.foros.session.fileman.FileManager;
import com.foros.util.StringUtil;
import com.foros.util.messages.MessageProvider;

import java.io.IOException;
import java.text.MessageFormat;

// todo validation!!
public class FileUniqueXmlAction extends AbstractXmlAction<FileInfo> {

    private String accountId;
    private String fileToUpload;
    private String currDirStr;
    private String mode;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(String fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public String getCurrDirStr() {
        return currDirStr;
    }

    public void setCurrDirStr(String currDirStr) {
        this.currDirStr = currDirStr;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public FileInfo generateModel() throws ProcessException {
        String fileName = StringUtil.trimFileName(getFileToUpload());
        String currentDirectory = getCurrDirStr();
        boolean isZip = fileName.endsWith(".zip");
        String realFileName = isZip ? StringUtil.removeZipSuffix(fileName) : fileName;

        boolean exists = false;
        boolean isDirectory = false;
        String confirmMessage = "";
        try {
            FileManager fm = FileManagerHelper.getFileManager(mode,
                    StringUtil.isPropertyEmpty(accountId) ? null : Long.valueOf(accountId));
            // TODO Only One should survive.
            com.foros.model.fileman.FileInfo fileInfo;
            fileInfo = fm.getFileInfo(currentDirectory, realFileName);
            isDirectory = fileInfo.isDirectory();
            exists = fm.checkExist(currentDirectory, realFileName);

            String key = null;
            if (exists) {
                key = "fileman.confirmFileOverwrite";
                if (isZip) {
                    key = isDirectory ? "fileman.confirmFolderOverwrite" : "fileman.confirmFileOverwrite";
                } else if (fileInfo.isDirectory()) {
                    key = "fileman.folderExists";
                }
            }
            if (key != null) {
                MessageProvider messageProvider = MessageProvider.createMessageProviderAdapter();
                confirmMessage = MessageFormat.format(messageProvider.getMessage(key), realFileName);
            } else {
                confirmMessage = null;
            }
        } catch (BadNameException e) {
            // ignore it
        } catch (IOException e) {
            throw new ProcessException("Error requesting file info", e);
        }

        return new FileInfo(fileName, isDirectory, exists, confirmMessage);
    }
}
