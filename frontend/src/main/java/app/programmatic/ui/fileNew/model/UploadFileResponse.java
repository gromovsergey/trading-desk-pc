package app.programmatic.ui.fileNew.model;

public class UploadFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileParentUri;
    private String fileType;
    private long size;

    public UploadFileResponse(String fileName, String fileDownloadUri, String fileParentUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileParentUri = fileParentUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileParentUri() {
        return fileParentUri;
    }

    public void setFileParentUri(String fileParentUri) {
        this.fileParentUri = fileParentUri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
