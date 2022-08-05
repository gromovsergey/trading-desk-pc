package com.foros.model.site;

public enum PassbackType {
    HTML_URL(null),
    HTML_CODE("html"),
    JS_CODE("js");

    private String fileExtension;

    private PassbackType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
