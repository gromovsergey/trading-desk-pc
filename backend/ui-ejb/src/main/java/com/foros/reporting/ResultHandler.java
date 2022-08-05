package com.foros.reporting;

import com.foros.reporting.meta.MetaData;

/**
 * Result handler
 */
public interface ResultHandler {

    void before(MetaData metaData);

    void row(Row row);

    void after();

    void close();

    void onError(ReportingException ex);

}
