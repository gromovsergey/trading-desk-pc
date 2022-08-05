package com.foros.session.reporting.waterfall;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public class WaterfallMeta {

    public static final DbColumn DATE = buildColumn("date", "sdate", ColumnTypes.date());
    public static final DbColumn RELEVANT_REQUESTS = buildColumn("relevantRequests", "relevant_requests", ColumnTypes.number());
    public static final DbColumn OPPORTUNITIES_TO_SERVE = buildColumn("opportunitiesToServe", "opportunities_to_serve", ColumnTypes.number());
    public static final DbColumn IMPRESSIONS = buildColumn("impressions", "impressions", ColumnTypes.number());
    public static final DbColumn AUCTIONS_LOST = buildColumn("auctionsLost", "auctions_lost_", ColumnTypes.number());
    public static final DbColumn SELECTION_FAILURES = buildColumn("selectionFailures", "selection_failures", ColumnTypes.number());
    public static final DbColumn PUBLISHER_EXCLUSIONS = buildColumn("publisherExclusions", "failures_0", ColumnTypes.number());
    public static final DbColumn SITE_TARGETING = buildColumn("siteTargeting", "failures_1", ColumnTypes.number());
    public static final DbColumn WALLED_GARDEN = buildColumn("walledGarden", "failures_2", ColumnTypes.number());
    public static final DbColumn PUBLISHER_FC = buildColumn("publisherFc", "failures_3", ColumnTypes.number());
    public static final DbColumn ADVERTISER_FC = buildColumn("advertiserFc", "failures_4", ColumnTypes.number());   
    public static final DbColumn TIME_OF_DAY = buildColumn("timeOfDay", "failures_5", ColumnTypes.number());   

    public static final ResolvableMetaData<DbColumn> META = metaData("waterfallReport")
            .outputColumns(DATE)
            .metricsColumns(RELEVANT_REQUESTS, OPPORTUNITIES_TO_SERVE, IMPRESSIONS, AUCTIONS_LOST, SELECTION_FAILURES, PUBLISHER_EXCLUSIONS, SITE_TARGETING, WALLED_GARDEN, PUBLISHER_FC, ADVERTISER_FC, TIME_OF_DAY)
            .build();
}
