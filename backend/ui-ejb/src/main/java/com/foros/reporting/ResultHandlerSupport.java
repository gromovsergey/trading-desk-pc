package com.foros.reporting;

import com.foros.reporting.meta.MetaData;

public abstract class ResultHandlerSupport implements ResultHandler {

    @Override
    public void before(MetaData metaData) {
    }

    @Override
    public void after() {
    }

    @Override
    public void close() {
    }

    @Override
    public void onError(ReportingException ex) {

    }
}
