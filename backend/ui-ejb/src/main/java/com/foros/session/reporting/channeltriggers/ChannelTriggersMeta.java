package com.foros.session.reporting.channeltriggers;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.MetaDataBuilder;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;

public interface ChannelTriggersMeta {

    DbColumn CHANNEL_ID = buildColumn("channelId", "channel_id", ColumnTypes.id());
    DbColumn CHANNEL = buildColumn("channel", "channel_name", ColumnTypes.string(), CHANNEL_ID);
    DbColumn URL = buildColumn("URL", "original_trigger", ColumnTypes.string());
    DbColumn KEYWORD = buildColumn("keyword", "original_trigger", ColumnTypes.string());
    DbColumn HITS = buildColumn("hits", "hits", ColumnTypes.number());
    DbColumn IMPRESSIONS = buildColumn("impressions", "impressions", ColumnTypes.number());
    DbColumn CLICKS = buildColumn("clicks", "clicks", ColumnTypes.number());
    DbColumn CTR = buildColumn("CTR", "ctr", ColumnTypes.percents());

    ResolvableMetaData<DbColumn> URLS_METADATA = MetaDataBuilder.metaData("channelTriggersReport.urls")
            .outputColumns(CHANNEL, URL)
            .metricsColumns(HITS, IMPRESSIONS, CLICKS, CTR)
            .build();

    ResolvableMetaData<DbColumn> PAGE_KEYWORDS_METADATA = MetaDataBuilder.metaData("channelTriggersReport.pageKeywords")
            .outputColumns(CHANNEL, KEYWORD)
            .metricsColumns(HITS, IMPRESSIONS, CLICKS, CTR)
            .build();

    ResolvableMetaData<DbColumn> SEARCH_KEYWORDS_METADATA = MetaDataBuilder.metaData("channelTriggersReport.searchKeywords")
            .outputColumns(CHANNEL, KEYWORD)
            .metricsColumns(HITS, IMPRESSIONS, CLICKS, CTR)
            .build();

    ResolvableMetaData<DbColumn> URL_KEYWORDS_METADATA = MetaDataBuilder.metaData("channelTriggersReport.urlKeywords")
            .outputColumns(CHANNEL, KEYWORD)
            .metricsColumns(HITS, IMPRESSIONS, CLICKS, CTR)
            .build();
}
