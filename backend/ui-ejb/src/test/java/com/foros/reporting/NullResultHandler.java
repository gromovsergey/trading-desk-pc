package com.foros.reporting;

import com.foros.reporting.meta.MetaData;

public class NullResultHandler extends ResultHandlerSupport {
    @Override
    public void before(MetaData metaData) {
    }

    @Override
    public void row(Row row) {
    }

    @Override
    public void after() {
    }

    @Override
    public void close() {
    }
}
