package app.programmatic.ui.reporting.model;

import org.springframework.http.MediaType;

public enum ReportFormat {
    CSV("text/csv", "csv"),
    JSON("application/json"),
    EXCEL("application/vnd.ms-excel", "xlsx");

    private MediaType mediaType;
    private String fileExtension;

    ReportFormat(String mediaType) {
        this.mediaType =  MediaType.parseMediaType(mediaType);
        this.fileExtension = fileExtension;
    }

    ReportFormat(String mediaType, String fileExtension) {
        this.mediaType =  MediaType.parseMediaType(mediaType);
        this.fileExtension = fileExtension;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public boolean isFileFormat() {
        return fileExtension != null;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
