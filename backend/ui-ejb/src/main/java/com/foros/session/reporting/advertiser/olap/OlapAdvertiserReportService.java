package com.foros.session.reporting.advertiser.olap;

import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.GenericReportService;

import java.util.Set;

public interface OlapAdvertiserReportService extends GenericReportService<OlapAdvertiserReportParameters, SimpleReportData> {
    OlapAdvertiserReportState getReportState(OlapAdvertiserReportParameters params, boolean ignoreSelected);

    Set<OlapColumn> getRecommendedColumns(OlapAdvertiserReportParameters params);
}
