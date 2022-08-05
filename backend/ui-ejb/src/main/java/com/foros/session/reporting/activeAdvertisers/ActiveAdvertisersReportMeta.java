package com.foros.session.reporting.activeAdvertisers;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface ActiveAdvertisersReportMeta {
    DbColumn ADVERTISERS_COUNT = buildColumn("advertisersCount", "advertisers_cnt", ColumnTypes.number());
    DbColumn VERTICALS_COUNT = buildColumn("verticalsCount", "verticals_cnt", ColumnTypes.number());

    DbColumn VERTICAL = buildColumn("vertical", "vertical", ColumnTypes.string());
    DbColumn ADVERTISER_ID = buildColumn("advertiser.id", "advertiser_id", ColumnTypes.id());
    DbColumn ADVERTISER_NAME = buildColumn("advertiser", "advertiser_name", ColumnTypes.string());

    ResolvableMetaData<DbColumn> ACTIVE_ADVERTISERS_REPORT_SUMMARY = metaData("activeAdvertisersSummary")
        .outputColumns(ADVERTISERS_COUNT)
        .metricsColumns(VERTICALS_COUNT)
        .build();

    ResolvableMetaData<DbColumn> ACTIVE_ADVERTISERS_REPORT = metaData("activeAdvertisers")
        .outputColumns(VERTICAL)
        .metricsColumns(ADVERTISER_NAME)
        .build();
}
