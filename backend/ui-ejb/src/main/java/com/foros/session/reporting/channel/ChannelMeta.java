package com.foros.session.reporting.channel;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ReportMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface ChannelMeta {
    DbColumn DATE = buildColumn("date", "sdate", ColumnTypes.date());
    DbColumn TOTAL_UNIQUES = buildColumn("totalUniques", "total_uniques", ColumnTypes.number());
    DbColumn ACTIVE_DAILY_UNIQUES = buildColumn("activeDailyUniques", "active_daily_uniques", ColumnTypes.number());
    DbColumn OPPOR_SERVE_IMPS = buildColumn("opporServeImps", "oppor_serve_imps", ColumnTypes.number());
    DbColumn OPPOR_SERVE_UNIQUES = buildColumn("opporServeUniques", "oppor_serve_uniques", ColumnTypes.number());
    DbColumn OPPOR_SERVE_VALUE = buildColumn("opporServeValue", "oppor_serve_value", ColumnTypes.currency());
    DbColumn OPPOR_SERVE_ECPM = buildColumn("opporServeECPM", "oppor_serve_ecpm", ColumnTypes.currency());
    DbColumn SERVED_IMPS = buildColumn("servedImps", "served_imps", ColumnTypes.number());
    DbColumn SERVED_CLICKS = buildColumn("servedClicks", "served_clicks", ColumnTypes.number());
    DbColumn SERVED_CTR = buildColumn("servedCTR", "served_ctr", ColumnTypes.percents());
    DbColumn SERVED_UNIQUES = buildColumn("servedUniques", "served_uniques", ColumnTypes.number());
    DbColumn SERVED_VALUE = buildColumn("servedValue", "served_value", ColumnTypes.currency());
    DbColumn SERVED_ECPM = buildColumn("servedECPM", "served_ecpm", ColumnTypes.currency());
    DbColumn NOT_SERVED_FOROS_IMPS = buildColumn("notServedFOROSImps", "not_served_foros_imps", ColumnTypes.number());
    DbColumn NOT_SERVED_FOROS_UNIQUES = buildColumn("notServedFOROSUniques", "not_served_foros_uniques",
            ColumnTypes.number());
    DbColumn NOT_SERVED_FOROS_VALUE = buildColumn("notServedFOROSValue", "not_served_foros_value",
            ColumnTypes.currency());
    DbColumn NOT_SERVED_FOROS_ECPM = buildColumn("notServedFOROSECPM", "not_served_foros_ecpm", ColumnTypes.currency());
    DbColumn NOT_SERVED_NO_FOROS_UNIQUES = buildColumn("notServedNoFOROSUniques", "not_served_no_foros_uniques",
            ColumnTypes.number());
    DbColumn NOT_SERVED_NO_FOROS_VALUE = buildColumn("notServedNoFOROSValue", "not_served_no_foros_value",
            ColumnTypes.currency());
    DbColumn NOT_SERVED_NO_FOROS_IMPS = buildColumn("notServedNoFOROSImps", "not_served_no_foros_imps",
            ColumnTypes.number());
    DbColumn NOT_SERVED_NO_FOROS_ECPM = buildColumn("notServedNoFOROSECPM", "not_served_no_foros_ecpm",
            ColumnTypes.currency());
    DbColumn MATCHED_URL = buildColumn("matchedUrl", "matched_url", ColumnTypes.number());
    DbColumn SEARCH_KEYWORDS = buildColumn("searchKeywords", "search_keywords", ColumnTypes.number());
    DbColumn MATCHED_KEYWORDS = buildColumn("matchedKeywords", "matched_keywords", ColumnTypes.number());
    DbColumn MATCHED_URL_KEYWORDS = buildColumn("matchedUrlKeywords", "matched_url_keywords", ColumnTypes.number());
    DbColumn TOTAL_MATCH = buildColumn("totalMatch", "total_match", ColumnTypes.number());

    ReportMetaData<DbColumn> META_DATA = metaData("channelReport")
            .outputColumns(DATE)
            .metricsColumns(TOTAL_UNIQUES, ACTIVE_DAILY_UNIQUES, OPPOR_SERVE_IMPS, OPPOR_SERVE_UNIQUES,
                    OPPOR_SERVE_ECPM, OPPOR_SERVE_VALUE, SERVED_IMPS, SERVED_CLICKS, SERVED_CTR, SERVED_UNIQUES,
                    SERVED_ECPM, SERVED_VALUE, NOT_SERVED_FOROS_IMPS, NOT_SERVED_FOROS_UNIQUES, NOT_SERVED_FOROS_ECPM,
                    NOT_SERVED_FOROS_VALUE, NOT_SERVED_NO_FOROS_IMPS, NOT_SERVED_NO_FOROS_UNIQUES, NOT_SERVED_NO_FOROS_ECPM,
                    NOT_SERVED_NO_FOROS_VALUE, MATCHED_URL, SEARCH_KEYWORDS, MATCHED_KEYWORDS, MATCHED_URL_KEYWORDS, TOTAL_MATCH)
            .build()
            .resolve(null);
}
