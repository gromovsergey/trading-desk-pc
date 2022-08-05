package com.foros.session.reporting;


public enum ExportFormat {
    CSV("csv", "text/csv", ".csv"),
    CSV_TAB("csv_tab", "text/csv", " (TAB).csv"),
    EXCEL("excel", "application/x-excel", ".xlsx"),
    HTML("html", "text/html", ".html");

    private String name;
    private String mime;
    private String extension;

    ExportFormat(String name, String mime, String extension) {
        this.name = name;
        this.mime = mime;
        this.extension = extension;
    }

    public String getName() {
        return name;
    }

    public String getMime() {
        return mime;
    }

    public String getExtension() {
        return extension;
    }

    public static ExportFormat find(String name) {
        for (ExportFormat format : ExportFormat.values()) {
            if (format.getName().equalsIgnoreCase(name)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Format with name " + name + " not found!");
    }
}
