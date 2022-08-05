package com.foros.action.xml.model;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 17:01:37
 * Version: 1.0
 */
public class FileInfo {

    private String fileName;
    private boolean isDir;
    private boolean exists;
    private String confirmMessage;

    public FileInfo(String fileName, boolean dir, boolean exists, String confirmMessage) {
        this.fileName = fileName;
        isDir = dir;
        this.exists = exists;
        this.confirmMessage = confirmMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getConfirmMessage() {
        return confirmMessage;
    }

    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

}
