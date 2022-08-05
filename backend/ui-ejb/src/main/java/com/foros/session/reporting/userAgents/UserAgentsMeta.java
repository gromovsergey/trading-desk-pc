package com.foros.session.reporting.userAgents;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public class UserAgentsMeta {

    public static final DbColumn USER_AGENT = buildColumn("user_agent", "user_agent", ColumnTypes.string());
    public static final DbColumn REQUESTS = buildColumn("requests", "requests", ColumnTypes.number());
    public static final DbColumn CHANNELS = buildColumn("channels", "channels", ColumnTypes.string());
    public static final DbColumn PLATFORMS = buildColumn("platforms", "platforms", ColumnTypes.string());

    public static final ResolvableMetaData<DbColumn> META_DATA = metaData("userAgentsReport")
            .metricsColumns(USER_AGENT, REQUESTS, CHANNELS, PLATFORMS).build();

    private UserAgentsMeta() {

    }
}
