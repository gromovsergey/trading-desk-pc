package com.foros.session.reporting;

import com.foros.reporting.meta.Column;

public interface ReportHeaderResolver {

    String getHeaderKey(Column column);

}
