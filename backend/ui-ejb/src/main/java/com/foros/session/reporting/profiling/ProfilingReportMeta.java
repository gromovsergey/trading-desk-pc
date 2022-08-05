package com.foros.session.reporting.profiling;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import java.util.Arrays;
import java.util.List;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface ProfilingReportMeta {
    DbColumn DATE = buildColumn("date", "isp_sdate", ColumnTypes.date());
    DbColumn USERS = buildColumn("users", "daily_uu_total", ColumnTypes.number());
    DbColumn USERS_PROFILING = buildColumn("usersProfiling", "daily_uu_profiling", ColumnTypes.number());
    DbColumn USERS_PROFILING_PC = buildColumn("usersProfilingPc", "daily_uu_profiling_pc", ColumnTypes.percents());
    DbColumn USERS_ADSERVING = buildColumn("usersAdserving", "daily_uu_adserving", ColumnTypes.number());
    DbColumn USERS_ADSERVING_PC = buildColumn("usersAdservingPc", "daily_uu_adserving_pc", ColumnTypes.percents());
    DbColumn PROFILING_REQ = buildColumn("profilingReq", "profiling_req", ColumnTypes.number());
    DbColumn PROFILING_REQ_PU = buildColumn("profilingReqPu", "profiling_req_pu", ColumnTypes.number());
    DbColumn ADSERVING_REQ = buildColumn("adservingReq", "adserving_req", ColumnTypes.number());
    DbColumn ADSERVING_REQ_PU = buildColumn("adservingReqPu", "adserving_req_pu", ColumnTypes.number());
    DbColumn MATCHED_CHANNELS = buildColumn("matchedChannels", "matched_channels", ColumnTypes.number());
    DbColumn URL_HITS = buildColumn("urlHits", "url_hits", ColumnTypes.number());
    DbColumn SEARCH_HITS = buildColumn("searchHits", "search_kw_hits", ColumnTypes.number());
    DbColumn KEYWORD_HITS = buildColumn("keywordHits", "page_kw_hits", ColumnTypes.number());
    DbColumn CHANNELS_PER_PROFILE = buildColumn("channelsPerProfile", "channels_per_profile", ColumnTypes.number());
    DbColumn EMPTY_PROFILES = buildColumn("emptyProfiles", "empty_profiles", ColumnTypes.percents());

    List<DbColumn> HINT_COLUMNS = Arrays.asList(USERS, USERS_PROFILING, USERS_ADSERVING, PROFILING_REQ_PU,
            ADSERVING_REQ, ADSERVING_REQ_PU, MATCHED_CHANNELS, URL_HITS);

    ResolvableMetaData<DbColumn> PROFILING_REPORT = metaData("profiling")
        .outputColumns(DATE)
        .metricsColumns(USERS, USERS_PROFILING, USERS_PROFILING_PC, USERS_ADSERVING, USERS_ADSERVING_PC,
                PROFILING_REQ, PROFILING_REQ_PU, ADSERVING_REQ, ADSERVING_REQ_PU, MATCHED_CHANNELS,
                URL_HITS, SEARCH_HITS, KEYWORD_HITS, CHANNELS_PER_PROFILE, EMPTY_PROFILES)
        .build();

}
