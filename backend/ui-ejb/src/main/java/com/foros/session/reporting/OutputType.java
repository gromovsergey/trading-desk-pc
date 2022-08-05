package com.foros.session.reporting;

public enum OutputType {
    HTML(ExportFormat.HTML),
    EXCEL(ExportFormat.EXCEL),
    EXCEL_NOLINKS(ExportFormat.EXCEL),
    CSV(ExportFormat.CSV);

    private ExportFormat format;

    private OutputType(ExportFormat format) {
        this.format = format;
    }

    public ExportFormat getFormat() {
        return format;
    }
}
