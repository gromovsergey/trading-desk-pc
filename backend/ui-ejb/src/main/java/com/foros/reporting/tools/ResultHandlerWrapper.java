package com.foros.reporting.tools;

import com.foros.reporting.ReportingException;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.Row;
import com.foros.reporting.meta.MetaData;

public abstract class ResultHandlerWrapper implements ResultHandler {

    protected ResultHandler resultHandler;

    protected ResultHandlerWrapper(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    @Override
    public void before(MetaData metaData) {
        resultHandler.before(metaData);
    }

    @Override
    public void row(Row row) {
        resultHandler.row(row);
    }

    @Override
    public void after() {
        resultHandler.after();
    }

    @Override
    public void close() {
        resultHandler.close();
    }

    @Override
    public void onError(ReportingException ex) {
        resultHandler.onError(ex);
    }
}
