package com.foros.session.reporting.referrer;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ReferrerMeta {

    public static final DbColumn DOMAIN = buildColumn("domain", "url", ColumnTypes.string());
    public static final DbColumn EXTERNAL_TAG_ID = buildColumn("externalTagId", "ext_tag_id", ColumnTypes.string());
    public static final DbColumn REQUESTS = buildColumn("totalRequests", "requests", ColumnTypes.number());
    public static final DbColumn OPTED_IN_REQUESTS = buildColumn("opted_in", "opted_in", ColumnTypes.number());
    public static final DbColumn PASSBACKS = buildColumn("passbacks", "passbacks", ColumnTypes.number());
    public static final DbColumn BIDS = buildColumn("bids", "bids", ColumnTypes.number());
    public static final DbColumn IMPS = buildColumn("impressions", "imps", ColumnTypes.number());
    public static final DbColumn CLICKS = buildColumn("clicks", "clicks", ColumnTypes.number());
    public static final DbColumn CTR = buildColumn("CTR", "ctr", ColumnTypes.percents());
    public static final DbColumn FLOOR = buildColumn("floor", "floor_cost", ColumnTypes.currency());
    public static final DbColumn FLOOR_WIN = buildColumn("floorWin", "floor_win", ColumnTypes.currency());
    public static final DbColumn FLOOR_NO_BID = buildColumn("floorNoBid", "floor_no_bid", ColumnTypes.currency());
    public static final DbColumn FLOOR_LOST = buildColumn("floorLost", "floor_lost", ColumnTypes.currency());
    public static final DbColumn BID_WIN = buildColumn("bidWin", "bid_win", ColumnTypes.currency());
    public static final DbColumn BID_LOST = buildColumn("bidLost", "bid_lost", ColumnTypes.currency());
    public static final DbColumn COST = buildColumn("cost", "cost", ColumnTypes.currency());

    public static final ResolvableMetaData<DbColumn> META_DATA = metaData("referrerReport")
            .metricsColumns(DOMAIN, EXTERNAL_TAG_ID, REQUESTS, OPTED_IN_REQUESTS, PASSBACKS, BIDS, IMPS, CLICKS, CTR, FLOOR,
                            FLOOR_WIN, FLOOR_NO_BID, FLOOR_LOST, BID_WIN, BID_LOST, COST)
            .build();

    public static final Set<DbColumn> CLICKS_DATA = Collections.unmodifiableSet(new HashSet<DbColumn>(Arrays.asList(
        CLICKS, CTR)));

    private ReferrerMeta() {
    }
}
