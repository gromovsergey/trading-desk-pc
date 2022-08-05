package com.foros.session.reporting;

import static com.foros.session.reporting.OutputType.CSV;
import static com.foros.session.reporting.OutputType.EXCEL;
import static com.foros.session.reporting.OutputType.HTML;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.security.ObjectType;
import com.foros.reporting.meta.AbstractDependentColumn;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.CsvSerializer;
import com.foros.reporting.serializer.ResultHolder;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.xlsx.ExcelStyles;
import com.foros.reporting.serializer.xlsx.ExcelStylesRegistry;
import com.foros.reporting.serializer.xlsx.ReportXlsxSerializer;
import com.foros.reporting.tools.CancelQueryService;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.security.AuditService;
import com.foros.session.security.ReportLogger;
import com.foros.session.security.ReportRunTO;
import com.foros.util.NameValuePair;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.commons.io.output.CountingOutputStream;

@Stateless(name = "ReportsService")
@Interceptors( {RestrictionInterceptor.class, ValidationInterceptor.class})
public class ReportsServiceBean implements ReportsService {

    private static final Logger logger = Logger.getLogger(ReportsServiceBean.class.getName());

    @EJB
    private AuditService auditService;

    @EJB
    private ConfigService config;

    @EJB
    private ExcelStylesRegistry excelStylesRegistry;

    @EJB
    private CancelQueryService cancelQueryService;

    @Override
    public AuditResultHandlerWrapper createHtmlSerializer(SimpleReportData data) {
        ResultHolder holder = new ResultHolder(config.get(ConfigParameters.HTML_REPORT_MAX_ROWS), data, null, getLocale());
        return createHtmlSerializer(holder);
    }

    @Override
    public AuditResultHandlerWrapper createExcelSerializer(OutputStream stream) {
        return createExcelSerializer(stream, EXCEL, null);
    }

    @Override
    public AuditResultHandlerWrapper createExcelSerializer(OutputStream stream, OutputType type) {
        return createExcelSerializer(stream, type, null);
    }

    @Override
    public AuditResultHandlerWrapper createExcelSerializer(OutputStream stream, String title) {
        return createExcelSerializer(stream, EXCEL, title);
    }

    @Override
    public AuditResultHandlerWrapper createExcelSerializer(OutputStream stream, OutputType type, String title) {
        CountingOutputStream cos = new CountingOutputStream(stream);
        ExcelStyles excelStyles = excelStylesRegistry.get(getLocale());
        ReportXlsxSerializer xlsxSerializer = new ReportXlsxSerializer(cos, null, excelStyles, config.get(ConfigParameters.EXPORT_REPORT_MAX_ROWS), title);
        return new AuditResultHandlerWrapper(xlsxSerializer, type, cos);
    }

    @Override
    public AuditResultHandlerWrapper createCsvSerializer(OutputStream stream) {
        CountingOutputStream cos = new CountingOutputStream(stream);
        CsvSerializer csvSerializer = new CsvSerializer(cos, getLocale(), config.get(ConfigParameters.EXPORT_REPORT_MAX_ROWS));
        return new AuditResultHandlerWrapper(csvSerializer, CSV, cos);
    }

    @Override
    public AuditResultHandlerWrapper createHtmlSerializer(ResultSerializer resultHandler) {
        return new AuditResultHandlerWrapper(resultHandler, HTML);
    }

    @Override
    public void execute(final AuditableReport report) {

        final ReportLogger reportLogger = new ReportLogger();

        try {
            report.prepare();
            ReportRunTO runReportTO = buildRunReportTO(report);
            reportLogger.logStart(runReportTO);
            cancelQueryService.describe(runReportTO.toString());
            report.execute();
            reportLogger.logSuccess(report.getRowsCount(), report.getSize());
        } catch (Exception e) {
            logFailure(reportLogger, e.getMessage());
            reThrow(e);
        }
    }

    private void logFailure(ReportLogger reportLogger, String message) {
        try {
            boolean cancelCalled = cancelQueryService.wasCancelCalled();
            reportLogger.logFailure(cancelCalled ? "Report was cancelled: " + message : message);
        } catch (Exception e2) {
            logger.log(Level.SEVERE, "Failed to write audit log record.", e2);
        }
    }

    @Override
    public void executeWithoutAudit(AuditableReport report) {
        report.prepare();
        report.execute();
    }

    private ReportRunTO buildRunReportTO(AuditableReport report) {
        ReportType reportType = report.getReportType();
        ReportRunTO to = new ReportRunTO(reportType.getId(), ObjectType.PredefinedReport);
        to.setColumns(columns(report.getColumns()));
        to.setParams(params(report.getPreparedParameters()));
        to.setOutputType(report.getOutputType().name());
        return to;
    }

    private Collection<String> columns(List<DbColumn> columns) {
        ArrayList<String> res = new ArrayList<String>(columns.size());
        for (AbstractDependentColumn<?> column : columns) {
            res.add(StringUtil.getLocalizedString(column.getNameKey(), Locale.getDefault()));
        }
        return res;
    }

    private Collection<NameValuePair<String, Object>> params(List<PreparedParameter> parameters) {
        ArrayList<NameValuePair<String, Object>> res = new ArrayList<NameValuePair<String, Object>>(parameters.size());
        for (PreparedParameter parameter : parameters) {
            res.add(new NameValuePair<String, Object>(parameter.getName(), parameter.getOriginalValue()));
        }
        return res;
    }

    private void reThrow(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new RuntimeException(e);
        }
    }

    private Locale getLocale() {
        return CurrentUserSettingsHolder.getLocale();
    }
}
