package com.foros.reporting.serializer;

import com.foros.session.reporting.PreparedParameter;

import java.util.List;

public interface ReportData {

    List<PreparedParameter> getPreparedParameters();

    boolean isPartialResult();
}
