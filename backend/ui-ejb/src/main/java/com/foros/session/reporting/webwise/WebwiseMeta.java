package com.foros.session.reporting.webwise;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public class WebwiseMeta {

    public static final DbColumn DATE = buildColumn("date", "sdate", ColumnTypes.date());
    public static final DbColumn UNIQUE_USERS = buildColumn("uniqueUsers", "daily_unique_users", ColumnTypes.number());
    public static final DbColumn WEEKLY_UNIQUE_USERS = buildColumn("weeklyUniqueUsers", "weekly_unique_users", ColumnTypes.number());
    public static final DbColumn MONTHLY_UNIQUE_USERS = buildColumn("monthlyUniqueUsers", "monthly_unique_users", ColumnTypes.number());
    public static final DbColumn RANGE_UNIQUE_USERS = buildColumn("totalUniqueUsers", "range_unique_users", ColumnTypes.number());
    public static final DbColumn SWITCH_ONS = buildColumn("switchOns", "ons", ColumnTypes.number());
    public static final DbColumn SWITCH_OFFS = buildColumn("switchOffs", "offs", ColumnTypes.number());
    public static final DbColumn SWITCH_ONS_TOTAL = buildColumn("switchOns.total", "ons", ColumnTypes.number());
    public static final DbColumn SWITCH_OFFS_TOTAL = buildColumn("switchOffs.total", "offs", ColumnTypes.number());

    public static final ResolvableMetaData<DbColumn> META = metaData("webwiseReport")
            .outputColumns(DATE)
            .metricsColumns(UNIQUE_USERS, WEEKLY_UNIQUE_USERS, MONTHLY_UNIQUE_USERS, SWITCH_ONS, SWITCH_OFFS)
            .build();

    public static final ResolvableMetaData<DbColumn> SUMMARY_META = metaData("webwiseReport")
            .metricsColumns(SWITCH_ONS_TOTAL, SWITCH_OFFS_TOTAL, RANGE_UNIQUE_USERS)
            .build();
}
