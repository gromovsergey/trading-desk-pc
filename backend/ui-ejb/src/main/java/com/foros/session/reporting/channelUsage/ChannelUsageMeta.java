package com.foros.session.reporting.channelUsage;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface ChannelUsageMeta {

    public static final DbColumn DATE = buildColumn("date", "date", ColumnTypes.date());
    public static final DbColumn NAME = buildColumn("channel", "channel_name", ColumnTypes.string());
    public static final DbColumn IMPS = buildColumn("impressions", "impressions", ColumnTypes.number());
    public static final DbColumn REVENUE = buildColumn("revenue", "revenue", ColumnTypes.currency());
    public static final DbColumn ECPM = buildColumn("eCPM", "ecpm", ColumnTypes.currency());

    public static final ResolvableMetaData<DbColumn> META_BY_CHANNEL = metaData("channelUsageReport")
            .outputColumns(NAME)
            .metricsColumns(IMPS, REVENUE, ECPM)
            .build();

    public static final ResolvableMetaData<DbColumn> META_BY_DATE = metaData("channelUsageReport")
            .outputColumns(DATE)
            .metricsColumns(IMPS, REVENUE, ECPM)
            .build();
}
