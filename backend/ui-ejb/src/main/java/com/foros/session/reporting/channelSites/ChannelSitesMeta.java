package com.foros.session.reporting.channelSites;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public final class ChannelSitesMeta {

    public static final DbColumn ACCOUNT_ID = buildColumn("account_id", "pub_account_id", ColumnTypes.id());
    public static final DbColumn ACCOUNT = buildColumn("publisherAccount", "pub_account_name", ColumnTypes.string());
    public static final DbColumn SITE_ID = buildColumn("site_id", "site_id", ColumnTypes.id());
    public static final DbColumn SITE = buildColumn("site", "site_name", ColumnTypes.string());
    public static final DbColumn TAG_ID = buildColumn("tag_id", "tag_id", ColumnTypes.id());
    public static final DbColumn TAG = buildColumn("tag", "tag_name", ColumnTypes.string());
    public static final DbColumn IMPRESSIONS = buildColumn("impressions", "imps", ColumnTypes.number());
    public static final DbColumn IMPRESSIONS_PC = buildColumn("impressions", "imps_pc", ColumnTypes.number());
    public static final DbColumn AVG_ADV_CPM = buildColumn("avgCPM", "avg_adv_cpm", ColumnTypes.currency());
    public static final DbColumn AVG_PUBLISHER_CPM = buildColumn("avgPublisherCPM", "avg_pub_cpm", ColumnTypes.currency());
    public static final DbColumn AVG_MARGIN  = buildColumn("avgMargin", "avg_margin", ColumnTypes.percents());

    public static final ResolvableMetaData<DbColumn> META_DATA = metaData("channelSitesReport")
            .outputColumns(ACCOUNT, SITE, TAG)
            .metricsColumns(IMPRESSIONS, AVG_ADV_CPM, AVG_PUBLISHER_CPM, AVG_MARGIN)
            .build();

    private ChannelSitesMeta() {
    }
}
