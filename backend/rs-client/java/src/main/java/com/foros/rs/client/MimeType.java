package com.foros.rs.client;

import org.apache.http.entity.ContentType;

public enum MimeType {

    TEXT_PLAIN("text/plain"),
    TEXT_CSV("text/csv"),
    APPLICATION_XML("application/xml"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_GZIP("application/x-gzip"),
    APPLICATION_EXCEL("application/x-excel");

    private String type;

    MimeType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public ContentType toContentType() {
        return ContentType.create(type);
    }

    public static MimeType parse(String mimeType) {
        for (MimeType value : values()) {
            if (value.getType().equals(mimeType)) {
                return value;
            }
        }
        return null;
    }
}
