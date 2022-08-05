package com.foros.session.reporting;

import com.phorm.oix.olap.EmptyRangeException;
import com.phorm.oix.olap.MemberNotFoundException;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.rowsource.RowSources;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.tools.olap.query.OlapQuery;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CommonAuditableOlapReportSupport<P> extends AuditableReportSupport<OlapColumn> {
    private static final Logger logger = Logger.getLogger(CommonAuditableOlapReportSupport.class.getName());

    protected ReportMetaData<OlapColumn> metaData;
    protected final P parameters;

    public CommonAuditableOlapReportSupport(P parameters, AuditResultHandlerWrapper handler) {
        super(handler);
        this.parameters = parameters;
    }

    @Override
    public void execute() {
        ResultHandler wrapped = wrap(handler);

        try {
            buildQuery().execute(wrapped, metaData);
        } catch (EmptyRangeException | MemberNotFoundException ex) {
            logger.log(Level.INFO, "It is impossible to resolve members: {0}", ex.getMessage());
            new SimpleIterationStrategy(metaData).process(RowSources.empty(), wrapped);
        }
    }

    @Override
    public List<OlapColumn> getColumns() {
        return metaData.getColumns();
    }

    protected abstract OlapQuery buildQuery();

    protected abstract ResultHandler wrap(ResultSerializer handler);
}
