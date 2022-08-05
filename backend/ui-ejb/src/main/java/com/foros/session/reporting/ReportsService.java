package com.foros.session.reporting;

import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.SimpleReportData;

import java.io.OutputStream;
import javax.ejb.Local;

@Local
public interface ReportsService {

    AuditResultHandlerWrapper createCsvSerializer(OutputStream stream);

    AuditResultHandlerWrapper createExcelSerializer(OutputStream stream);

    AuditResultHandlerWrapper createExcelSerializer(OutputStream stream, OutputType type);

    AuditResultHandlerWrapper createExcelSerializer(OutputStream stream, OutputType type, String title);

    AuditResultHandlerWrapper createExcelSerializer(OutputStream stream, String title);

    AuditResultHandlerWrapper createHtmlSerializer(ResultSerializer resultHandler);

    AuditResultHandlerWrapper createHtmlSerializer(SimpleReportData data);

    void execute(AuditableReport report);

    void executeWithoutAudit(AuditableReport report);
}
