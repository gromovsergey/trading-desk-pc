package com.foros.session.reporting.publisher;

import com.phorm.oix.olap.OlapIdentifier;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.meta.olap.OlapMetaDataBuilder;
import com.foros.reporting.tools.subtotal.aggreagate.ECPMAggregateFunction;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface PublisherMeta {
    public static interface Levels {
        /* Publisher Account */
        OlapIdentifier D_PUBLISHER_ACCOUNT = OlapIdentifier.parse("Publisher Account");
        OlapIdentifier PUBLISHER_COUNTRY = D_PUBLISHER_ACCOUNT.append("[Publisher Country]");
        OlapIdentifier PUBLISHER_CURRENCY = D_PUBLISHER_ACCOUNT.append("[Publisher Currency]");
        OlapIdentifier PUBLISHER_ACCOUNT = D_PUBLISHER_ACCOUNT.append("[Publisher Account]");
        OlapIdentifier PUBLISHER_ACCOUNT_ID = D_PUBLISHER_ACCOUNT.append("[Publisher Account ID]");

        /* Publisher */
        static final OlapIdentifier PUBLISHER = OlapIdentifier.parse("[Publisher]");
        static final OlapIdentifier SITE_STATUS = PUBLISHER.append("[Site Status]");
        static final OlapIdentifier SITE_URL = PUBLISHER.append("[Site URL]");
        static final OlapIdentifier SITE = PUBLISHER.append("[Site]");
        static final OlapIdentifier SITE_ID = PUBLISHER.append("[Site ID]");
        static final OlapIdentifier TAG = PUBLISHER.append("[Tag]");
        static final OlapIdentifier TAG_ID = PUBLISHER.append("[Tag ID]");

        /* Tag Size */
        static final OlapIdentifier TAG_SIZE = OlapIdentifier.parse("[TagSize]");
        static final OlapIdentifier SIZE_NAME = TAG_SIZE.append("[Tag Size]");
        static final OlapIdentifier SIZE_ID = TAG_SIZE.append("[Size ID]");

        /* Tag Rate */
        static final OlapIdentifier TAG_RATE = OlapIdentifier.parse("[Tag Rate]");
        static final OlapIdentifier TAG_PRICING_SITE_RATE_TYPE = TAG_RATE.append("[Tag Rate Type]");
        static final OlapIdentifier TAG_PRICING_CCG_RATE_TYPE = TAG_RATE.append("[Tag CCG Rate Type]");
        static final OlapIdentifier TAG_PRICING_CCG_TYPE = TAG_RATE.append("[Tag Rate CCG Type]");
        static final OlapIdentifier TAG_PRICING_CONTRY = TAG_RATE.append("[Tag Rate Country]");
        static final OlapIdentifier TAG_PRICING = TAG_RATE.append("[Tag Rate]");

        /* User Country */
        static final OlapIdentifier USER_COUNTRY = OlapIdentifier.parse("[Country]");
        static final OlapIdentifier USER_COUNTRY_CODE = USER_COUNTRY.append("[Country Code]");

        /* Date */
        static final OlapIdentifier DATE = OlapIdentifier.parse("[Country Date]");
        static final OlapIdentifier DATE_VALUE = DATE.append("[Country Date]");

        /* Measures */
        static final OlapIdentifier MEASURES = OlapIdentifier.parse("[Measures]");
        static final OlapIdentifier IMPRESSIONS = MEASURES.append("[imps]");
        static final OlapIdentifier CREDITED_IMPRESSIONS = MEASURES.append("[pub_credited_imps]");
        static final OlapIdentifier IMPRESSIONS_WG = MEASURES.append("[imps_wg]");
        static final OlapIdentifier IMPRESSIONS_FOROS = MEASURES.append("[imps_foros]");
        static final OlapIdentifier INVALID_IMPRESSIONS = MEASURES.append("[invalid_imps]");
        static final OlapIdentifier CLICKS = MEASURES.append("[clicks]");
        static final OlapIdentifier INVALID_CLICKS = MEASURES.append("[invalid_clicks]");
        static final OlapIdentifier REQUESTS = MEASURES.append("[requests]");
        static final OlapIdentifier INVALID_REQUESTS = MEASURES.append("[invalid_requests]");
        static final OlapIdentifier PASSBACKS = MEASURES.append("[passbacks]");
        static final OlapIdentifier CTR = MEASURES.append("[ctr]");
        static final OlapIdentifier ECPM = MEASURES.append("[ecpm]");
        static final OlapIdentifier REVENUE = MEASURES.append("[revenue]");
        static final OlapIdentifier REVENUE_WG = MEASURES.append("[revenue_wg]");
        static final OlapIdentifier REVENUE_FOROS = MEASURES.append("[revenue_foros]");
    }

    // output
    OlapColumn DATE = OlapMetaDataBuilder.rowMember("date", Levels.DATE_VALUE, ColumnTypes.date());
    OlapColumn PUBLISHER_COUNTRY = OlapMetaDataBuilder.rowMember("pubCurrency", Levels.PUBLISHER_COUNTRY, ColumnTypes.string());
    OlapColumn PUBLISHER_CURRENCY = OlapMetaDataBuilder.rowMember("pubCurrency", Levels.PUBLISHER_CURRENCY, ColumnTypes.string());
    OlapColumn SITE_ID = OlapMetaDataBuilder.rowMember("siteId", Levels.SITE_ID, ColumnTypes.id());
    OlapColumn SITE_NAME = OlapMetaDataBuilder.rowMember("site", Levels.SITE, ColumnTypes.string(), SITE_ID);
    OlapColumn SITE_STATUS = OlapMetaDataBuilder.rowMember("siteStatus", Levels.SITE_STATUS, ColumnTypes.status());
    OlapColumn SITE_URL = OlapMetaDataBuilder.rowMember("siteUrl", Levels.SITE_URL, ColumnTypes.string());
    OlapColumn TAG_ID = OlapMetaDataBuilder.rowMember("tagId", Levels.TAG_ID, ColumnTypes.id());
    OlapColumn TAG_NAME = OlapMetaDataBuilder.rowMember("tag", Levels.TAG, ColumnTypes.string(), TAG_ID);
    OlapColumn SIZE_ID = OlapMetaDataBuilder.rowMember("size_id", Levels.SIZE_ID, ColumnTypes.id());
    OlapColumn SIZE_NAME = OlapMetaDataBuilder.rowMember("tagSize", Levels.SIZE_NAME, ColumnTypes.string(), SIZE_ID);
    OlapColumn TAG_PRICING_COUNTRY_CODE = OlapMetaDataBuilder.rowMember("tag_pricing_country_code", Levels.TAG_PRICING_CONTRY, ColumnTypes.country());
    OlapColumn TAG_PRICING_CCG_RATE_TYPE = OlapMetaDataBuilder.rowMember("tag_pricing_ccg_rate_type", Levels.TAG_PRICING_CCG_RATE_TYPE, ColumnTypes.string());
    OlapColumn TAG_PRICING_CCG_TYPE = OlapMetaDataBuilder.rowMember("tag_pricing_ccg_type", Levels.TAG_PRICING_CCG_TYPE, ColumnTypes.string());
    OlapColumn TAG_PRICING_SITE_RATE_TYPE = OlapMetaDataBuilder.rowMember("tag_rate_type", Levels.TAG_PRICING_SITE_RATE_TYPE, ColumnTypes.string());
    OlapColumn TAG_PRICING = OlapMetaDataBuilder.buildRowMember("tagPricing", Levels.TAG_PRICING, ColumnTypes.currency())
            .dependencies(PUBLISHER_COUNTRY, PUBLISHER_CURRENCY, TAG_PRICING_CCG_RATE_TYPE, TAG_PRICING_CCG_TYPE, TAG_PRICING_SITE_RATE_TYPE)
            .build();
    OlapColumn COUNTRY = OlapMetaDataBuilder.rowMember("country", Levels.USER_COUNTRY_CODE, ColumnTypes.string());

    // metrics
    OlapColumn REVENUE = OlapMetaDataBuilder.buildCellValue("revenue", Levels.REVENUE, ColumnTypes.currency())
            .dependency(PUBLISHER_CURRENCY)
            .aggregateSum()
            .build();
    OlapColumn REVENUE_WG = OlapMetaDataBuilder.buildCellValue("revenue.wg", Levels.REVENUE_WG, ColumnTypes.currency())
            .dependency(PUBLISHER_CURRENCY)
            .aggregateSum()
            .build();
    OlapColumn REVENUE_FOROS = OlapMetaDataBuilder.buildCellValue("revenue.foros", Levels.REVENUE_FOROS, ColumnTypes.currency())
            .dependency(PUBLISHER_CURRENCY)
            .aggregateSum()
            .build();
    OlapColumn IMPRESSIONS = OlapMetaDataBuilder.buildCellValue("impressions", Levels.IMPRESSIONS, ColumnTypes.number())
            .aggregateSum()
            .build();
    OlapColumn IMPRESSIONS_WG = OlapMetaDataBuilder.buildCellValue("impressions.wg", Levels.IMPRESSIONS_WG, ColumnTypes.number())
            .aggregateSum()
            .build();
    OlapColumn IMPRESSIONS_FOROS = OlapMetaDataBuilder.buildCellValue("impressions.foros", Levels.IMPRESSIONS_FOROS, ColumnTypes.number())
            .aggregateSum()
            .build();
    OlapColumn ECPM = OlapMetaDataBuilder.buildCellValue("eCPM", Levels.ECPM, ColumnTypes.currency())
            .dependencies(PUBLISHER_CURRENCY, REVENUE, IMPRESSIONS)
            .aggregate(ECPMAggregateFunction.factory(REVENUE, IMPRESSIONS))
            .build();
    OlapColumn CREDITED_IMPRESSIONS = OlapMetaDataBuilder.buildCellValue("creditedImpressions", Levels.CREDITED_IMPRESSIONS, ColumnTypes.number())
            .aggregateSum()
            .build();
    OlapColumn CLICKS = OlapMetaDataBuilder
            .buildCellValue("clicks", Levels.CLICKS, ColumnTypes.number())
            .aggregateSum()
            .build();
    OlapColumn CTR = OlapMetaDataBuilder
            .buildCellValue("CTR", Levels.CTR, ColumnTypes.percents())
            .aggregatePercent(CLICKS, IMPRESSIONS)
            .build();
    OlapColumn REQUESTS = OlapMetaDataBuilder
            .buildCellValue("requests", Levels.REQUESTS, ColumnTypes.number())
            .aggregateSum()
            .build();
    OlapColumn INVALID_IMPRESSIONS = OlapMetaDataBuilder.buildCellValue("invalidImpressions", Levels.INVALID_IMPRESSIONS, ColumnTypes.number())
            .dependency(IMPRESSIONS)
            .aggregateSum()
            .build();
    OlapColumn INVALID_CLICKS = OlapMetaDataBuilder.buildCellValue("invalidClicks", Levels.INVALID_CLICKS, ColumnTypes.number())
            .dependency(CLICKS)
            .aggregateSum()
            .build();
    OlapColumn INVALID_REQUESTS = OlapMetaDataBuilder.buildCellValue("invalidRequests", Levels.INVALID_REQUESTS, ColumnTypes.number())
            .dependency(REQUESTS)
            .aggregateSum()
            .build();
    OlapColumn PASSBACKS = OlapMetaDataBuilder.buildCellValue("passbacks", Levels.PASSBACKS, ColumnTypes.number())
            .aggregateSum()
            .build();

    ResolvableMetaData<OlapColumn> ALL = OlapMetaDataBuilder.metaData("olapPublisherReport")
            .outputColumns(DATE, SITE_NAME, SITE_ID, SITE_STATUS, SITE_URL, TAG_NAME, TAG_ID, SIZE_NAME, TAG_PRICING, COUNTRY)
            .metricsColumns(CREDITED_IMPRESSIONS, IMPRESSIONS, IMPRESSIONS_WG, IMPRESSIONS_FOROS, INVALID_IMPRESSIONS, CLICKS, CTR, INVALID_CLICKS, REVENUE, REVENUE_WG, REVENUE_FOROS, ECPM, REQUESTS, INVALID_REQUESTS, PASSBACKS)
            .build();

    ResolvableMetaData<OlapColumn> ALL_NON_WG = OlapMetaDataBuilder.metaData("olapPublisherReport")
            .metricsColumns(CREDITED_IMPRESSIONS, IMPRESSIONS, INVALID_IMPRESSIONS, CLICKS, CTR, INVALID_CLICKS, REVENUE, ECPM, REQUESTS, INVALID_REQUESTS, PASSBACKS)
            .outputColumns(DATE, SITE_NAME, SITE_ID, SITE_STATUS, SITE_URL, TAG_NAME, TAG_ID, SIZE_NAME, TAG_PRICING, COUNTRY)
            .build();

    ResolvableMetaData<OlapColumn> ALL_WG = OlapMetaDataBuilder.metaData("olapPublisherReport")
            .metricsColumns(CREDITED_IMPRESSIONS, IMPRESSIONS_WG, IMPRESSIONS_FOROS, INVALID_IMPRESSIONS, CLICKS, CTR, INVALID_CLICKS, REVENUE_WG, REVENUE_FOROS, ECPM, REQUESTS, INVALID_REQUESTS, PASSBACKS)
            .outputColumns(DATE, SITE_NAME, SITE_ID, SITE_STATUS, SITE_URL, TAG_NAME, TAG_ID, SIZE_NAME, TAG_PRICING, COUNTRY)
            .build();

    Set<OlapColumn> MANDATORY_NON_WG_COLUMNS = Collections.unmodifiableSet(new HashSet<OlapColumn>(Arrays.asList(
            IMPRESSIONS, REVENUE
    )));

    Set<OlapColumn> MANDATORY_WG_COLUMNS = Collections.unmodifiableSet(new HashSet<OlapColumn>(Arrays.asList(
            IMPRESSIONS_WG, IMPRESSIONS_FOROS, REVENUE_WG, REVENUE_FOROS
    )));

    Set<OlapColumn> BY_DATE = Collections.unmodifiableSet(new HashSet<OlapColumn>(Arrays.asList(
            DATE, ECPM, CLICKS, CTR
    )));

    Set<OlapColumn> BY_SITE = Collections.unmodifiableSet(new HashSet<OlapColumn>(Arrays.asList(
            SITE_NAME, ECPM, REQUESTS, PASSBACKS, CLICKS, CTR
    )));

    Set<OlapColumn> BY_SITE_TAG = Collections.unmodifiableSet(new HashSet<OlapColumn>(Arrays.asList(
            SITE_NAME, TAG_NAME, ECPM, REQUESTS, PASSBACKS, CLICKS, CTR
    )));

    Set<OlapColumn> CLICKS_DATA = Collections.unmodifiableSet(new HashSet<OlapColumn>(Arrays.asList(
            CLICKS, CTR, INVALID_CLICKS
    )));

    Map<DetailLevel, Set<OlapColumn>> COLUMNS_BY_LEVEL = Collections.unmodifiableMap(new HashMap<DetailLevel, Set<OlapColumn>>() {{
        put(DetailLevel.date, BY_DATE);
        put(DetailLevel.site, BY_SITE);
        put(DetailLevel.siteTag, BY_SITE_TAG);
        put(DetailLevel.custom, BY_DATE);
    }});
}
