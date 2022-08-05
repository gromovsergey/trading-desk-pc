package com.foros.session.reporting.channelInventoryForecast;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public final class ChannelInventoryForecastMeta {
    public static final DbColumn CHANNEL_ID = buildColumn("channel_id", "channel_id", ColumnTypes.id());
    public static final DbColumn CHANNEL = buildColumn("channel", "channel_name", ColumnTypes.string());
    public static final DbColumn CHANNEL_STATUS = buildColumn("channelDisplayStatus", "channel_display_status_id", ColumnTypes.id());
    public static final DbColumn SIZE_NAME = buildColumn("creativeSize", "size_name", ColumnTypes.string());
    public static final DbColumn SIZE_ID = buildColumn("size_id", "size_id", ColumnTypes.id());
    public static final DbColumn CPM = buildColumn("CPM", "cpm", ColumnTypes.currency());
    public static final DbColumn IMPRESSIONS = buildColumn("impressions", "impops", ColumnTypes.number());
    public static final DbColumn DAILY_UNIQUE_USERS = buildColumn("dailyUniqueUsers", "daily_unique_users", ColumnTypes.number());

    public static final ResolvableMetaData<DbColumn> META = metaData("channelInventoryReport")
            .outputColumns(CHANNEL, CHANNEL_STATUS, SIZE_NAME)
            .metricsColumns(CPM, IMPRESSIONS, DAILY_UNIQUE_USERS)
            .build();

    public static final ResolvableMetaData<DbColumn> SINGLE_CHANNEL_META = metaData("channelInventoryReport")
            .outputColumns(SIZE_NAME)
            .metricsColumns(CPM, IMPRESSIONS, DAILY_UNIQUE_USERS)
            .build();

}
