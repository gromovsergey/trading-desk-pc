package com.foros.session.reporting.waterfall;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.column;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public class SelectionFailuresMeta {

    public static final DbColumn MASK = column("mask", "mask", ColumnTypes.number()).build();
    public static final DbColumn PUBLISHER_EXCLUSIONS = column("publisherExclusions", "failures_0", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn SITE_TARGETING = column("siteTargeting", "failures_1", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn WALLED_GARDEN = column("walledGarden", "failures_2", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn PUBLISHER_FC = column("publisherFc", "failures_3", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn ADVERTISER_FC = column("advertiserFc", "failures_4", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn TIME_OF_DAY = column("timeOfDay", "failures_5", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn SELECTION_FAILURES = column("selectionFailures", "selection_failures", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn SELECTION_FAILURES_PC = column("selectionFailures", "selection_failures_pc", ColumnTypes.number()).build();
    public static final DbColumn SELECTION_FAILURES_DAILY = column("selectionFailuresDaily", "selection_failures_daily", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn SELECTION_FAILURES_DAILY_PC = column("selectionFailuresDaily", "selection_failures_daily_pc", ColumnTypes.number()).build();

    public static final ResolvableMetaData<DbColumn> META = metaData("selectionFailuresReport")
            .outputColumns(PUBLISHER_EXCLUSIONS, SITE_TARGETING, WALLED_GARDEN, PUBLISHER_FC, ADVERTISER_FC, TIME_OF_DAY)
            .metricsColumns(SELECTION_FAILURES, SELECTION_FAILURES_DAILY)
            .build();
}
