package com.foros.session.reporting.advertiser.olap;

import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.formatter.ResourceKeyHeaderFormatter;
import com.foros.session.reporting.ReportHeaderFormatterRegistry;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class OlapAdvertiserReportHeaderFormatterRegistry extends ReportHeaderFormatterRegistry {

    public OlapAdvertiserReportHeaderFormatterRegistry(ReportMetaData<OlapColumn> metaData) {
        if (isChangeColumnName(metaData, OlapAdvertiserMeta.CREDITED_IMPRESSIONS)) {
            formatters.put(OlapAdvertiserMeta.IMPRESSIONS, new ResourceKeyHeaderFormatter("report.output.field.totalImpressions"));
        }

        if (isChangeColumnName(metaData, OlapAdvertiserMeta.CREDITED_CLICKS)) {
            formatters.put(OlapAdvertiserMeta.CLICKS, new ResourceKeyHeaderFormatter("report.output.field.totalClicks"));
        }
    }

    private boolean isChangeColumnName(ReportMetaData<OlapColumn> metaData, OlapColumn column) {
        List<OlapColumn> wgColumns = OlapAdvertiserMeta.WG_TRIPLETS.get(column);
        List<OlapColumn> columns = metaData.getMetricsColumns();
        return columns.contains(column) || CollectionUtils.containsAny(columns, wgColumns);
    }
}
