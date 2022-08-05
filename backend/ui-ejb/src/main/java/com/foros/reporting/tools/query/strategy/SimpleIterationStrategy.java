package com.foros.reporting.tools.query.strategy;

import com.foros.reporting.ReportingException;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.Row;
import com.foros.reporting.TooManyRowsException;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.rowsource.RowSource;

public class SimpleIterationStrategy implements IterationStrategy {

    private MetaData metaData;
    private boolean closeAtTheEnd;

    public SimpleIterationStrategy(MetaData metaData) {
        this(metaData, true);
    }

    public SimpleIterationStrategy(MetaData metaData, boolean closeAtTheEnd) {
        this.metaData = metaData;
        this.closeAtTheEnd = closeAtTheEnd;
    }

    @Override
    public void process(RowSource rowSource, ResultHandler handler) {
        try {
            process0(rowSource, handler);
        } finally {
            if (closeAtTheEnd) {
                handler.close();
            }
        }
    }

    private void process0(RowSource rowSource, ResultHandler handler) {
        handler.before(metaData);
        try {
            for (Row row : rowSource) {
                handler.row(row);
            }
            handler.after();
        } catch (TooManyRowsException e) {
            // The report data was limited. Ignore it
            try {
                handler.after();
            } catch (TooManyRowsException ex) {
            }
        } catch (ReportingException e) {
            handler.onError(e);
            throw e;
        }
    }
}
