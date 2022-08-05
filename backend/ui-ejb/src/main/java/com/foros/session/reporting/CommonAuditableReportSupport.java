package com.foros.session.reporting;

import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.tools.query.Query;

import java.util.List;

public abstract class CommonAuditableReportSupport<P> extends AuditableReportSupport<DbColumn> {
    protected ReportMetaData<DbColumn> metaData;
    protected Query query;
    protected P parameters;

    protected boolean executeSummary;
    protected ReportMetaData<DbColumn> summaryMetaData;
    protected Query summaryQuery;

    public CommonAuditableReportSupport(P parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
        super(handler);
        this.parameters = parameters;
        this.executeSummary = executeSummary;
    }

    @Override
    public void execute() {
        if (executeSummary) {
            doExecuteSummary();
        }
        doExecuteReport();
    }

    protected void doExecuteSummary() {
        assertNotNull("handler", handler);
        assertNotNull("summaryMetaData", summaryMetaData);
        assertNotNull("summaryQuery", summaryQuery);

        SummaryHandlerAdapter summaryHandlerAdapter = new SummaryHandlerAdapter(wrapSummary(handler));
        if (!summaryMetaData.getMetricsColumns().isEmpty()) {
            summaryQuery.execute(summaryHandlerAdapter, summaryMetaData);
        } else {
            executeEmpty(summaryHandlerAdapter, summaryMetaData);
        }
    }

    protected void doExecuteReport() {
        assertNotNull("handler", handler);
        assertNotNull("metaData", metaData);
        assertNotNull("query", query);

        if (!metaData.getMetricsColumns().isEmpty()) {
            query.execute(wrap(handler), metaData);
        }  else {
            executeEmpty(handler, metaData);
        }
    }

    private void executeEmpty(ResultSerializer handler, ReportMetaData meta) {
        try {
            handler.before(meta);
            handler.after();
        } finally {
            handler.close();
        }
    }

    protected ResultHandler wrap(ResultSerializer handler) {
        return handler;
    }

    protected ResultSerializer wrapSummary(ResultSerializer handler) {
        return handler;
    }

    @Override
    public List<DbColumn> getColumns() {
        return metaData != null ? metaData.getColumns() : handler.getColumns();
    }

    private void assertNotNull(String field, Object o) {
        if (o == null) {
            throw new NullPointerException(field);
        }
    }
}
