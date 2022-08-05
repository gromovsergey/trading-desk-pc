package com.foros.session.fileman;

public class FileContentException extends FileManagerException implements FilePathException {
    private final String fileName;
    private final String path;
    private boolean isExtensionCorrespondsContent;

    public FileContentException(String fileName, String path) {
        super("Can't to upload file '" + fileName + "'. Content is invalid.");
        this.fileName = fileName;
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String getPath() {
        return path;
    }

    public boolean isExtensionCorrespondsContent() {
        return isExtensionCorrespondsContent;
    }

    public void setExtensionCorrespondsContent(boolean isExtensionCorrespondsContent) {
        this.isExtensionCorrespondsContent = isExtensionCorrespondsContent;
    }
}

