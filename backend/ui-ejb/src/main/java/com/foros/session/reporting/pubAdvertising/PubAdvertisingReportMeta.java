package com.foros.session.reporting.pubAdvertising;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface PubAdvertisingReportMeta {
    public static final DbColumn IMPRESSIONS_CNT = buildColumn("impressions", "impressions", ColumnTypes.number());
    public static final DbColumn CLICKS_CNT = buildColumn("clicks", "clicks", ColumnTypes.number());
    public static final DbColumn CTR_CNT = buildColumn("CTR", "ctr", ColumnTypes.percents());
    public static final DbColumn REVENUE_CNT = buildColumn("revenue", "revenue", ColumnTypes.currency());
    public static final DbColumn ECPM_CNT = buildColumn("eCPM", "ecpm", ColumnTypes.currency());

    public static final DbColumn SITE_NAME = buildColumn("site", "site_name", ColumnTypes.string());
    public static final DbColumn SITE_ID = buildColumn("site.id", "site_id", ColumnTypes.id());
    public static final DbColumn SITE_STATUS = buildColumn("site.status", "site_status", ColumnTypes.status());
    public static final DbColumn AGENCY = buildColumn("agency", "agency", ColumnTypes.string());
    public static final DbColumn ADVERTISER_NAME = buildColumn("advertiser", "advertiser_name", ColumnTypes.string());
    public static final DbColumn ADVERTISER_ID = buildColumn("advertiser.id", "advertiser_id", ColumnTypes.id());
    public static final DbColumn IMPRESSIONS = buildColumn("impressions", "impressions", ColumnTypes.number());
    public static final DbColumn CLICKS = buildColumn("clicks", "clicks", ColumnTypes.number());
    public static final DbColumn CTR = buildColumn("CTR", "ctr", ColumnTypes.percents());
    public static final DbColumn REVENUE = buildColumn("revenue", "revenue", ColumnTypes.currency());
    public static final DbColumn ECPM = buildColumn("eCPM", "ecpm", ColumnTypes.currency());

    public static final ResolvableMetaData<DbColumn> PUB_ADVERTISING_REPORT_SUMMARY = metaData("pubAdvertisingSummary")
            .metricsColumns(IMPRESSIONS_CNT, CLICKS_CNT, CTR_CNT, REVENUE_CNT, ECPM_CNT)
            .build();

    public static final ResolvableMetaData<DbColumn> PUB_ADVERTISING_REPORT = metaData("pubAdvertising")
            .outputColumns(SITE_NAME, AGENCY, ADVERTISER_NAME)
            .metricsColumns(IMPRESSIONS, CLICKS, CTR, REVENUE, ECPM)
            .build();

    Collection<DbColumn> INTERNAL_ONLY = Collections.unmodifiableCollection(
            Arrays.asList(REVENUE, REVENUE_CNT, ECPM, ECPM_CNT));
}
