package com.foros.action.reporting;

import com.foros.action.BaseActionSupport;
import com.foros.action.download.FileDownloadResult;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.persistence.hibernate.StatementTimeoutException;
import com.foros.reporting.serializer.ReportData;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.tools.CancelQueryService;
import com.foros.reporting.tools.query.ConnectionUnavailableSQLException;
import com.phorm.oix.saiku.utils.SaikuTimeoutException;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.OutputType;
import com.foros.session.reporting.PreparedParameter;
import com.foros.util.ExceptionUtil;
import com.foros.util.ReflectionUtil;
import com.foros.util.StringUtil;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.QueryTimeoutException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

@Validations(
        customValidators = {
                @CustomValidator(type = "custconversion", fieldName = "dateRange.begin", key = "errors.invalid",
                        parameters = {@ValidationParameter(name = "fieldKey", value = "report.input.field.dateRange.dateFrom")}),
                @CustomValidator(type = "custconversion", fieldName = "dateRange.end", key = "errors.invalid",
                        parameters = {@ValidationParameter(name = "fieldKey", value = "report.input.field.dateRange.dateTo")})
        }
)
public abstract class RunReportingActionSupport<T> extends BaseActionSupport implements ServletResponseAware, ServletRequestAware, ModelDriven<T> {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
        .add("#path", "formatError(violation.propertyPath, violation.message)")
        .rules();

    private HttpServletResponse response;
    protected HttpServletRequest request;
    private OutputType format;
    private String cancellationToken;

    protected ReportData data;
    protected T parameters;
    protected List<PreparedParameter> preparedParameters;

    @EJB
    private ConfigService configService;

    @EJB
    private CancelQueryService cancelQueryService;

    private boolean mayExceedExportLimit = false;
    private boolean wasCancelCalled = false;

    public RunReportingActionSupport() {
        initParameters();
    }

    protected <D extends ReportData> String safelyExecute(final ReportWork<D> work) {
        if (format == null || OutputType.HTML == format) {
            try {
                runRunnable(executeHtml(work));
            } catch (Exception e) {
                handleException(e);
            }
            return success();
        } else {
            try {
                doExport(work);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    public String safelyExecuteGeneric(final GenericReportService<T, SimpleReportData> service, final String name) {
        return safelyExecute(new ReportWork<SimpleReportData>() {
            @Override
            protected void executeCsv(OutputStream os) {
                service.processCsv(parameters, os);
            }

            @Override
            protected void executeExcel(OutputStream os) {
                service.processExcel(parameters, os);
            }

            @Override
            protected void executeHtml(SimpleReportData data) {
                service.processHtml(parameters, data);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            protected SimpleReportData newData() {
                return new SimpleReportData();
            }
        });
    }

    protected String success() {
        return "success";
    }

    private void handleException(Exception e) {
        // pass through SecurityException, StatementTimeoutException & QueryTimeoutException
        if (ExceptionUtil.hasCause(e, SecurityException.class)) {
            throw (RuntimeException) e;
        }

        if (e instanceof ConstraintViolationException) {
            throw (ConstraintViolationException)e;
        }

        if (wasCancelCalled) {
            // do cleanup, report may be partially executed so state may be inconsistent
            data = null;
            preparedParameters = null;
            mayExceedExportLimit = false;

            getFieldErrors().clear();
            getActionErrors().clear();

            addActionError(getText("errors.cancel"));
            return;
        }

        if (ExceptionUtil.hasCause(e, StatementTimeoutException.class)
                || ExceptionUtil.hasCause(e, QueryTimeoutException.class)
                || ExceptionUtil.hasCause(e, SaikuTimeoutException.class)) {
            addActionError(getText("errors.serverTimeout"));
            return;
        }

        if (ExceptionUtil.hasCause(e, ConnectionUnavailableSQLException.class)) {
            addActionError(getText("errors.connectionUnavailable"));
            return;
        }

        addActionError(getText("error.report.generate"));
    }

    private <D extends ReportData> void doExport(final ReportWork<D> work) throws IOException {

        final Path tempFile = Files.createTempFile(null, "rpt");
        try {
            runRunnable(executeFile(work, tempFile));

            Cookie cookie = new Cookie("download-" + cancellationToken, "true");
            cookie.setPath("/");
            cookie.setMaxAge(-1);
            response.addCookie(cookie);
            response.setContentType(format.getFormat().getMime());
            String fileName = work.getName() + "_report" + format.getFormat().getExtension();
            FileDownloadResult.setDownloadHeaders(request, response, fileName);

            try ( FileInputStream inputStream = new FileInputStream(tempFile.toFile())) {
                IOUtils.copy(inputStream, response.getOutputStream());
            }
        } finally {
            if (Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
        }

    }

    private <D extends ReportData> Runnable executeHtml(final ReportWork<D> work) {
        return new Runnable() {
            @Override
            public void run() {
                D temp = work.newData();

                work.executeHtml(temp);
                setData(temp);

                if (data.isPartialResult()) {
                    mayExceedExportLimit = true;
                    addActionError(StringUtil.getLocalizedString("error.report.tooManyRows.reporting", getHtmlMaxRows()));
                }

                preparedParameters = data.getPreparedParameters();
            }
        };
    }

    private <D extends ReportData> Runnable executeFile(final ReportWork<D> work, final Path tempFile) {
        return new Runnable() {
            @Override
            public void run() {
                try (OutputStream outputStream = new FileOutputStream(tempFile.toFile())) {
                    switch (format) {
                        case CSV:
                            work.executeCsv(outputStream);
                            break;
                        case EXCEL:
                        case EXCEL_NOLINKS:
                            work.executeExcel(outputStream);
                            break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Can't write to temp file", e);
                }

            }
        };
    }

    private void runRunnable(final Runnable reportRunnable) {
        if (cancellationToken != null) {
            cancelQueryService.doCancellable(cancellationToken, new Runnable() {
                @Override
                public void run() {
                    try {
                        reportRunnable.run();
                        cancelQueryService.checkCancelled();
                    } finally {
                        wasCancelCalled = cancelQueryService.wasCancelCalled();
                    }
                }
            });
        } else {
            reportRunnable.run();
        }
    }

    public ReportData getData() {
        return data;
    }

    protected void setData(ReportData data) {
        this.data = data;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public T getModel() {
        return parameters;
    }

    public OutputType getFormat() {
        return format;
    }

    public void setFormat(OutputType format) {
        this.format = format;
    }

    @SuppressWarnings({"unchecked"})
    protected void initParameters() {
        try {
            parameters = (T) ReflectionUtil.getActualTypeArgument(getClass(), 0).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<PreparedParameter> getPreparedParameters() {
        return preparedParameters;
    }

    public int getHtmlMaxRows() {
        return configService.get(ConfigParameters.HTML_REPORT_MAX_ROWS);
    }

    public int getExportMaxRows() {
        return configService.get(ConfigParameters.EXPORT_REPORT_MAX_ROWS);
    }

    public boolean isMayExceedExportLimit() {
        return mayExceedExportLimit;
    }

    protected static abstract class ReportWork<D extends ReportData> {

        @SuppressWarnings({"unchecked"})
        protected D newData() {
            try {
                return (D) ReflectionUtil.getActualTypeArgument(getClass(), 0).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected void executeHtml(D data) {
            throw new UnsupportedOperationException();
        }

        protected void executeCsv(OutputStream os) {
            throw new UnsupportedOperationException();
        }

        protected void executeExcel(OutputStream os) {
            throw new UnsupportedOperationException();
        }

        public String getName() {
            throw new UnsupportedOperationException();
        }

    }

    public String formatError(String value, String message) {
        String fieldName = StringUtil.getLocalizedString("report.input.field." + value, true);

        // replace accountId -> account
        if (fieldName == null && value.endsWith("Id")) {
            fieldName = StringUtil.getLocalizedString("report.input.field." + value.replaceAll("Id$", ""), true);
        }

        if (fieldName != null) {
            return StringUtil.getLocalizedString("errors.fieldError", fieldName, message);
        }
        return message;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String getCancellationToken() {
        return cancellationToken;
    }

    public void setCancellationToken(String cancellationToken) {
        this.cancellationToken = cancellationToken;
    }
}
