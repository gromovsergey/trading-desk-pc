package com.foros.session.reporting.publisherOverview;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface PublisherOverviewReportMeta {
    DbColumn PUBLISHER_ID = buildColumn("publisherId", "publisher_id", ColumnTypes.id());
    DbColumn PUBLISHER_NAME = buildColumn("publisher", "publisher_name", ColumnTypes.string());
    DbColumn VERTICAL = buildColumn("vertical", "vertical", ColumnTypes.string());
    DbColumn PRODUCT = buildColumn("product", "product", ColumnTypes.string());
    DbColumn IMPRESSIONS = buildColumn("impressions", "imps", ColumnTypes.number());
    DbColumn CLICKS = buildColumn("clicks", "clicks", ColumnTypes.number());

    ResolvableMetaData<DbColumn> PUBLISHER_OVERVIEW_REPORT_SUMMARY = metaData("publisherOverviewtSummary")
        .outputColumns(IMPRESSIONS)
        .metricsColumns(CLICKS)
        .build();

    ResolvableMetaData<DbColumn> PUBLISHER_OVERVIEW_REPORT = metaData("publisherOverview")
        .outputColumns(PUBLISHER_ID)
        .metricsColumns(PUBLISHER_NAME, VERTICAL, PRODUCT, IMPRESSIONS, CLICKS)
        .build();
}
