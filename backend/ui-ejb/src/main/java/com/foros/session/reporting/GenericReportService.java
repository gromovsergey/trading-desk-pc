package com.foros.session.reporting;

import com.foros.reporting.serializer.ReportData;

import java.io.OutputStream;

public interface GenericReportService<P, D extends ReportData> {
    void processHtml(P parameters, D data);

    void processExcel(P parameters, OutputStream stream);

    void processCsv(P parameters, OutputStream stream);
}
