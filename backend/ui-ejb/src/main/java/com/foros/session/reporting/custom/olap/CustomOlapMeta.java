package com.foros.session.reporting.custom.olap;

import com.phorm.oix.olap.OlapIdentifier;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DependenciesColumnResolver;
import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.reporting.meta.olap.MemberResolver;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.meta.olap.OlapMetaDataBuilder;
import com.foros.reporting.tools.subtotal.aggreagate.ECPMAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.OneCurrencyAggregateFunction;
import com.foros.session.reporting.custom.CustomReportParameters;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface CustomOlapMeta {

    class PayoutDependenciesResolver implements DependenciesColumnResolver<OlapColumn> {
        private final OlapColumn pubCurrency;

        public PayoutDependenciesResolver(OlapColumn pubCurrency) {
            this.pubCurrency = pubCurrency;
        }

        @Override
        public Set<OlapColumn> resolve(Object context) {
            CustomReportParameters parameters = (CustomReportParameters) context;
            Set<OlapColumn> result = new HashSet<>();
            if (!parameters.getOutputCurrencyCode().equals("USD")) {
                result.add(pubCurrency);
            }
            return result;
        }
    }

    class CurrencyMemberResolver implements MemberResolver {

        private OlapIdentifier udsMember;
        private OlapIdentifier accountMember;

        public CurrencyMemberResolver(OlapIdentifier accountMember, OlapIdentifier udsMember) {
            this.accountMember = accountMember;
            this.udsMember = udsMember;
        }

        @Override
        public OlapIdentifier resolve(Object context) {
            CustomReportParameters parameters = (CustomReportParameters) context;
            return parameters.isOutputInAccountCurrency() ? accountMember : udsMember;
        }
    }

    interface Levels {
        /* Advertiser */
        static final OlapIdentifier ADVERTISER = OlapIdentifier.parse("[Advertiser]");
        static final OlapIdentifier CAMPAIGN_ID = ADVERTISER.append("[Campaign ID]");
        static final OlapIdentifier CAMPAIGN = ADVERTISER.append("[Campaign]");
        static final OlapIdentifier CREATIVE_GROUP_ID = ADVERTISER.append("[Creative Group ID]");
        static final OlapIdentifier CREATIVE_GROUP = ADVERTISER.append("[Creative Group]");
        static final OlapIdentifier CC_ID = ADVERTISER.append("[Campaign Creative ID]");
        static final OlapIdentifier CREATIVE_NAME = ADVERTISER.append("[Creative]");
        static final OlapIdentifier CREATIVE_SIZE = OlapIdentifier.parse("[Advertiser.Creative Size]").append("[Creative Size]");

        /* Advertiser Account*/
        static final OlapIdentifier ADV_ACCOUNT = OlapIdentifier.parse("[Advertiser Account]");
        static final OlapIdentifier ADV_COUNTRY = ADV_ACCOUNT.append("[Advertiser Country]");
        static final OlapIdentifier AGENCY_ID = ADV_ACCOUNT.append("[Agency ID]");
        static final OlapIdentifier AGENCY = ADV_ACCOUNT.append("[Agency]");
        static final OlapIdentifier ADVERTISER_ID = ADV_ACCOUNT.append("[Advertiser Account ID]");
        static final OlapIdentifier ADVERTISER_ACCOUNT = ADV_ACCOUNT.append("[Advertiser Account]");

        /* Advertiser Currency*/
        static final OlapIdentifier D_ADV_CURRENCY = OlapIdentifier.parse("[Advertiser Currency]");
        static final OlapIdentifier ADV_CURRENCY = D_ADV_CURRENCY.append("[Advertiser Currency]");

        /* RON Flag */
        static final OlapIdentifier RON_FLAG = OlapIdentifier.parse("[Advertiser.RON Flag]");
        static final OlapIdentifier RON_FLAG_NAME = RON_FLAG.append("[RON FLag]");

        /* Channel Target */
        static final OlapIdentifier CHANNEL_TARGET = OlapIdentifier.parse("[Advertiser.ChannelTarget]");
        static final OlapIdentifier CHANNEL_TARGET_ID = CHANNEL_TARGET.append("[Channel Target ID]");
        static final OlapIdentifier CHANNEL_TARGET_NAME = CHANNEL_TARGET.append("[Channel Target]");

        /* Isp */
        static final OlapIdentifier ISP = OlapIdentifier.parse("[ISP]");
        static final OlapIdentifier ISP_ACCOUNT = ISP.append("[Isp Account]");
        static final OlapIdentifier ISP_ACCOUNT_ID = ISP.append("[Isp Account ID]");
        static final OlapIdentifier COLO = ISP.append("[Colo]");
        static final OlapIdentifier COLO_ID = ISP.append("[Colo ID]");

        /* Publisher Account */
        static final OlapIdentifier D_PUBLISHER_ACCOUNT = OlapIdentifier.parse("Publisher Account");
        static final OlapIdentifier PUBLISHER_COUNTRY = D_PUBLISHER_ACCOUNT.append("[Publisher Country]");
        static final OlapIdentifier PUBLISHER_CURRENCY = D_PUBLISHER_ACCOUNT.append("[Publisher Currency]");
        static final OlapIdentifier PUBLISHER_ACCOUNT = D_PUBLISHER_ACCOUNT.append("[Publisher Account]");
        static final OlapIdentifier PUBLISHER_ACCOUNT_ID = D_PUBLISHER_ACCOUNT.append("[Publisher Account ID]");

        /* Publisher */
        static final OlapIdentifier PUBLISHER = OlapIdentifier.parse("[Publisher]");
        static final OlapIdentifier SITE = PUBLISHER.append("[Site]");
        static final OlapIdentifier SITE_ID = PUBLISHER.append("[Site ID]");
        static final OlapIdentifier SITE_URL = PUBLISHER.append("[Site URL]");
        static final OlapIdentifier TAG = PUBLISHER.append("[Tag]");
        static final OlapIdentifier TAG_ID = PUBLISHER.append("[Tag ID]");

        /* Tag Size */
        static final OlapIdentifier TAG_SIZE = OlapIdentifier.parse("[TagSize]");
        static final OlapIdentifier SIZE_TYPE_ID = TAG_SIZE.append("[Size Type ID]");
        static final OlapIdentifier SIZE_TYPE = TAG_SIZE.append("[Size Type]");
        static final OlapIdentifier SIZE_ID = TAG_SIZE.append("[Size ID]");
        static final OlapIdentifier SIZE_NAME = TAG_SIZE.append("[Tag Size]");

        /* Site Category */
        static final OlapIdentifier SITE_CATEGORY = OlapIdentifier.parse("[Publisher.Site Category]");
        static final OlapIdentifier SITE_CATEGORY_NAME = SITE_CATEGORY.append("[Site Category]");

        /* User Status */
        static final OlapIdentifier USER_STATUS = OlapIdentifier.parse("[UserStatus]");
        static final OlapIdentifier USER_STATUS_CODE = USER_STATUS.append("[User Status PK]");

        /* User Country */
        static final OlapIdentifier USER_COUNTRY = OlapIdentifier.parse("[UserCountry]");
        static final OlapIdentifier USER_COUNTRY_CODE = USER_COUNTRY.append("[Country Code]");

        /* Channel Device */
        static final OlapIdentifier CHANNEL_DEVICE = OlapIdentifier.parse("[ChannelDevice]");
        static final OlapIdentifier CHANNEL_DEVICE_ID = CHANNEL_DEVICE.append("[Device Channel ID]");
        static final OlapIdentifier CHANNEL_DEVICE_NAME = CHANNEL_DEVICE.append("[Device Channel]");

        /* Tag Rate */
        static final OlapIdentifier TAG_RATE = OlapIdentifier.parse("[Tag Rate]");
        static final OlapIdentifier TAG_PRICING_CCG_RATE_TYPE = TAG_RATE.append("[Tag CCG Rate Type]");
        static final OlapIdentifier TAG_PRICING_CCG_TYPE = TAG_RATE.append("[Tag Rate CCG Type]");
        static final OlapIdentifier TAG_PRICING_SITE_RATE_TYPE = TAG_RATE.append("[Tag Rate Type]");
        static final OlapIdentifier TAG_PRICING = TAG_RATE.append("[Tag Rate]");

        /* Position */
        static final OlapIdentifier POSITION = OlapIdentifier.parse("[Position]");
        static final OlapIdentifier POSITION_VALUE = POSITION.append("[Position]");

        /* Rate Model */
        static final OlapIdentifier CCG_RATE = OlapIdentifier.parse("[CcgRate]");
        static final OlapIdentifier RATE_MODEL = CCG_RATE.append("[Rate Model]");
        static final OlapIdentifier RATE_NET = CCG_RATE.append("[Rate Net]");
        static final OlapIdentifier RATE_GROSS = CCG_RATE.append("[Rate Gross]");

        /* Date */
        static final OlapIdentifier DATE = OlapIdentifier.parse("[Country Date]");
        static final OlapIdentifier DATE_VALUE = DATE.append("[Country Date]");

        /* Time */
        static final OlapIdentifier TIME = OlapIdentifier.parse("[Country Time]");
        static final OlapIdentifier HOUR = TIME.append("[Country Hour]");

        /* Measures */
        static final OlapIdentifier MEASURES = OlapIdentifier.parse("[Measures]");
        static final OlapIdentifier ACTIONS = MEASURES.append("[actions]");
        static final OlapIdentifier IMPS = MEASURES.append("[imps]");
        static final OlapIdentifier CLICKS = MEASURES.append("[clicks]");
        static final OlapIdentifier CTR = MEASURES.append("[Ctr]");
        static final OlapIdentifier REQUESTS = MEASURES.append("[requests]");
        static final OlapIdentifier PASSBACKS = MEASURES.append("[passbacks]");
        static final OlapIdentifier MARGIN = MEASURES.append("[Margin]");
        static final OlapIdentifier ACTION_RATE = MEASURES.append("[Action Rate]");
        static final OlapIdentifier NET_ECPM = MEASURES.append("[Ecpm Net]");
        static final OlapIdentifier NET_ECPM_USD = MEASURES.append("[Ecpm Net Usd]");
        static final OlapIdentifier GROSS_ECPM = MEASURES.append("[Ecpm Gross]");
        static final OlapIdentifier GROSS_ECPM_USD = MEASURES.append("[Ecpm Gross Usd]");
        static final OlapIdentifier NET_ECPC = MEASURES.append("[Ecpc Net]");
        static final OlapIdentifier NET_ECPC_USD = MEASURES.append("[Ecpc Net Usd]");
        static final OlapIdentifier GROSS_ECPC = MEASURES.append("[Ecpc Gross]");
        static final OlapIdentifier GROSS_ECPC_USD = MEASURES.append("[Ecpc Gross Usd]");
        static final OlapIdentifier NET_ECPA = MEASURES.append("[Ecpa Net]");
        static final OlapIdentifier NET_ECPA_USD = MEASURES.append("[Ecpa Net Usd]");
        static final OlapIdentifier GROSS_ECPA = MEASURES.append("[Ecpa Gross]");
        static final OlapIdentifier GROSS_ECPA_USD = MEASURES.append("[Ecpa Gross Usd]");
        static final OlapIdentifier INVALID_IMPRESSIONS = MEASURES.append("[invalid imps]");
        static final OlapIdentifier INVALID_CLICKS = MEASURES.append("[invalid clicks]");
        static final OlapIdentifier INVALID_REQUESTS = MEASURES.append("[invalid requests]");
        static final OlapIdentifier PAYOUT = MEASURES.append("[payouts]");
        static final OlapIdentifier PAYOUT_USD = MEASURES.append("[payouts usd]");
        static final OlapIdentifier UNIQUE_USERS_DAILY_COLO = MEASURES.append("[Colo daily unique users]");
        static final OlapIdentifier UNIQUE_USERS_MONTHLY_COLO = MEASURES.append("[Colo monthly unique users]");
        static final OlapIdentifier UNIQUE_USERS_DAILY_CAMPAIGN = MEASURES.append("[Campaign daily unique users]");
        static final OlapIdentifier UNIQUE_USERS_MONTHLY_CAMPAIGN = MEASURES.append("[Campaign monthly unique users]");
        static final OlapIdentifier UNIQUE_USERS_DAILY_CCG = MEASURES.append("[CCG daily unique users]");
        static final OlapIdentifier UNIQUE_USERS_MONTHLY_CCG = MEASURES.append("[CCG monthly unique users]");
        static final OlapIdentifier UNIQUE_USERS_DAILY_CC = MEASURES.append("[CC daily unique users]");
        static final OlapIdentifier UNIQUE_USERS_MONTHLY_CC = MEASURES.append("[CC monthly unique users]");
        static final OlapIdentifier UNIQUE_USERS_DAILY_SITE = MEASURES.append("[Site daily unique users]");
        static final OlapIdentifier UNIQUE_USERS_MONTHLY_SITE = MEASURES.append("[Site monthly unique users]");
        static final OlapIdentifier LIFE_IMPRESSIONS = MEASURES.append("[Life Impressions]");
        static final OlapIdentifier LIFE_CLICKS = MEASURES.append("[Life Clicks]");
        static final OlapIdentifier LIFE_CTR = MEASURES.append("[Life CTR]");
        static final OlapIdentifier LIFE_ACTIONS = MEASURES.append("[Life Actions]");
        static final OlapIdentifier LIFE_ACTION_RATE = MEASURES.append("[Life Action Rate]");
        static final OlapIdentifier INVENTORY_COST_GROSS = MEASURES.append("[inventory cost gross]");
        static final OlapIdentifier INVENTORY_COST_GROSS_USD = MEASURES.append("[inventory cost gross usd]");
        static final OlapIdentifier INVENTORY_COST_NET = MEASURES.append("[inventory cost net]");
        static final OlapIdentifier INVENTORY_COST_NET_USD = MEASURES.append("[inventory cost net usd]");
        static final OlapIdentifier REQUESTS_PER_PAGE = MEASURES.append("[Per Page Requests]");
        static final OlapIdentifier IMPRESSIONS_PER_PAGE = MEASURES.append("[Per Page Imps]");
    }

    /* Advertiser */
    OlapColumn ADV_CURRENCY = OlapMetaDataBuilder.rowMember("advCurrency", Levels.ADV_CURRENCY, ColumnTypes.string());
    OlapColumn AGENCY = OlapMetaDataBuilder.rowMember("agency", Levels.AGENCY, ColumnTypes.string());
    OlapColumn AGENCY_ID = OlapMetaDataBuilder.rowMember("agencyId", Levels.AGENCY_ID, ColumnTypes.id(), AGENCY);
    OlapColumn ADVERTISER = OlapMetaDataBuilder.rowMember("adv", Levels.ADVERTISER_ACCOUNT, ColumnTypes.string());
    OlapColumn ADVERTISER_ID = OlapMetaDataBuilder.rowMember("advId", Levels.ADVERTISER_ID, ColumnTypes.id(), ADVERTISER);
    OlapColumn CAMPAIGN = OlapMetaDataBuilder.rowMember("campaign", Levels.CAMPAIGN, ColumnTypes.string());
    OlapColumn CAMPAIGN_ID = OlapMetaDataBuilder.rowMember("campaignId", Levels.CAMPAIGN_ID, ColumnTypes.id(), CAMPAIGN);
    OlapColumn CREATIVE_GROUP = OlapMetaDataBuilder.rowMember("creativeGroup", Levels.CREATIVE_GROUP, ColumnTypes.string());
    OlapColumn CREATIVE_GROUP_ID = OlapMetaDataBuilder.rowMember("creativeGroupId", Levels.CREATIVE_GROUP_ID, ColumnTypes.id(), CREATIVE_GROUP);
    OlapColumn CC_ID = OlapMetaDataBuilder.rowMember("CCID", Levels.CC_ID, ColumnTypes.id());
    OlapColumn CREATIVE_NAME = OlapMetaDataBuilder.rowMember("creativeName", Levels.CREATIVE_NAME, ColumnTypes.string(), CC_ID);
    OlapColumn CREATIVE_SIZE = OlapMetaDataBuilder.rowMember("creativeSize", Levels.CREATIVE_SIZE, ColumnTypes.string());

    /* RON Flag */
    OlapColumn RON_FLAG = OlapMetaDataBuilder.rowMember("ronFlag", Levels.RON_FLAG_NAME, ColumnTypes.string());

    /* Isp */
    OlapColumn ISP_ACCOUNT_ID = OlapMetaDataBuilder.rowMember("ispId", Levels.ISP_ACCOUNT_ID, ColumnTypes.id());
    OlapColumn ISP_ACCOUNT = OlapMetaDataBuilder.rowMember("isp", Levels.ISP_ACCOUNT, ColumnTypes.string(), ISP_ACCOUNT_ID);
    OlapColumn COLOCATION_ID = OlapMetaDataBuilder.rowMember("colocationId", Levels.COLO_ID, ColumnTypes.id());
    OlapColumn COLOCATION = OlapMetaDataBuilder.rowMember("colocation", Levels.COLO, ColumnTypes.string(), COLOCATION_ID);

    /* Publisher */
    OlapColumn PUBLISHER_COUNTRY = OlapMetaDataBuilder.rowMember("pub_country_code", Levels.PUBLISHER_COUNTRY, ColumnTypes.string());
    OlapColumn PUBLISHER_CURRENCY = OlapMetaDataBuilder.rowMember("pubCurrency", Levels.PUBLISHER_CURRENCY, ColumnTypes.string());
    OlapColumn PUBLISHER_ACCOUNT_ID = OlapMetaDataBuilder.rowMember("publisherId", Levels.PUBLISHER_ACCOUNT_ID, ColumnTypes.id());
    OlapColumn PUBLISHER_ACCOUNT = OlapMetaDataBuilder.rowMember("publisher", Levels.PUBLISHER_ACCOUNT, ColumnTypes.string(), PUBLISHER_ACCOUNT_ID);
    OlapColumn SITE_ID = OlapMetaDataBuilder.rowMember("siteId", Levels.SITE_ID, ColumnTypes.id());
    OlapColumn SITE = OlapMetaDataBuilder.rowMember("siteName", Levels.SITE, ColumnTypes.string(), SITE_ID);
    OlapColumn SITE_URL = OlapMetaDataBuilder.rowMember("siteUrl", Levels.SITE_URL, ColumnTypes.string());
    OlapColumn TAG_ID = OlapMetaDataBuilder.rowMember("tagId", Levels.TAG_ID, ColumnTypes.id());
    OlapColumn TAG = OlapMetaDataBuilder.rowMember("tagName", Levels.TAG, ColumnTypes.string(), TAG_ID);
    OlapColumn SITE_CATEGORY_NAME = OlapMetaDataBuilder.rowMember("siteCategory", Levels.SITE_CATEGORY_NAME, ColumnTypes.string());

    /* Tag Size */
    OlapColumn SIZE_ID = OlapMetaDataBuilder.rowMember("size_id", Levels.SIZE_ID, ColumnTypes.id());
    OlapColumn SIZE_NAME = OlapMetaDataBuilder.rowMember("creativeSize", Levels.SIZE_NAME, ColumnTypes.string(), SIZE_ID);
    OlapColumn SIZE_TYPE_ID = OlapMetaDataBuilder.rowMember("sizeTypeId", Levels.SIZE_TYPE_ID, ColumnTypes.id());
    OlapColumn SIZE_TYPE = OlapMetaDataBuilder.rowMember("sizeType", Levels.SIZE_TYPE, ColumnTypes.string(), SIZE_TYPE_ID);

    /* User Status */
    OlapColumn USER_STATUS_CODE = OlapMetaDataBuilder.rowMember("userStatus", Levels.USER_STATUS_CODE, ColumnTypes.string());

    /* User Country */
    OlapColumn USER_COUNTRY = OlapMetaDataBuilder.rowMember("userCountryCode", Levels.USER_COUNTRY_CODE, ColumnTypes.string());

    /* Channel Target */
    OlapColumn CHANNEL_TARGET_ID = OlapMetaDataBuilder.rowMember("channelTargetId", Levels.CHANNEL_TARGET_ID, ColumnTypes.id());
    OlapColumn CHANNEL_TARGET_NAME = OlapMetaDataBuilder.rowMember("channelTarget", Levels.CHANNEL_TARGET_NAME, ColumnTypes.string(), CHANNEL_TARGET_ID);

    /* Channel Device */
    OlapColumn CHANNEL_DEVICE_ID = OlapMetaDataBuilder.rowMember("device", Levels.CHANNEL_DEVICE_ID, ColumnTypes.id());
    OlapColumn CHANNEL_DEVICE_NAME = OlapMetaDataBuilder.rowMember("deviceName", Levels.CHANNEL_DEVICE_NAME, ColumnTypes.string(), CHANNEL_DEVICE_ID);

    /* Tag Rate */
    OlapColumn TAG_PRICING_CCG_RATE_TYPE = OlapMetaDataBuilder.rowMember("tag_pricing_ccg_rate_type", Levels.TAG_PRICING_CCG_RATE_TYPE, ColumnTypes.string());
    OlapColumn TAG_PRICING_CCG_TYPE = OlapMetaDataBuilder.rowMember("tag_pricing_ccg_type", Levels.TAG_PRICING_CCG_TYPE, ColumnTypes.string());
    OlapColumn TAG_PRICING_SITE_RATE_TYPE = OlapMetaDataBuilder.rowMember("tag_rate_type", Levels.TAG_PRICING_SITE_RATE_TYPE, ColumnTypes.string());

    OlapColumn TAG_PRICING = OlapMetaDataBuilder.buildRowMember("tagRate", Levels.TAG_PRICING, ColumnTypes.currency())
            .dependencies(TAG_ID, PUBLISHER_CURRENCY, TAG_PRICING_CCG_RATE_TYPE, TAG_PRICING_CCG_TYPE, TAG_PRICING_SITE_RATE_TYPE)
            .build();

    /* Position */
    OlapColumn POSITION = OlapMetaDataBuilder.rowMember("position", Levels.POSITION_VALUE, ColumnTypes.number());

    /* Date */
    OlapColumn DATE = OlapMetaDataBuilder.rowMember("date", Levels.DATE_VALUE, ColumnTypes.date());

    /* Time */
    OlapColumn HOUR = OlapMetaDataBuilder.rowMember("hour", Levels.HOUR, ColumnTypes.number());

    /* Rate Model */
    OlapColumn RATE_MODEL = OlapMetaDataBuilder.rowMember("rateModel", Levels.RATE_MODEL, ColumnTypes.string());
    OlapColumn RATE_NET = OlapMetaDataBuilder.rowMember("netRate", Levels.RATE_NET, ColumnTypes.currency(), ADV_CURRENCY);
    OlapColumn RATE_GROSS = OlapMetaDataBuilder.rowMember("grossRate", Levels.RATE_GROSS, ColumnTypes.currency(), ADV_CURRENCY);


    /* Measures */
    OlapColumn IMPS = OlapMetaDataBuilder
            .buildCellValue("impressions", Levels.IMPS, ColumnTypes.number())
            .aggregateSum()
            .build();

    OlapColumn CLICKS = OlapMetaDataBuilder
            .buildCellValue("clicks", Levels.CLICKS, ColumnTypes.number())
            .aggregateSum()
            .build();

    OlapColumn CTR = OlapMetaDataBuilder
            .buildCellValue("CTR", Levels.CTR, ColumnTypes.percents())
            .aggregatePercent(CLICKS, IMPS)
            .build();

    OlapColumn REQUESTS = OlapMetaDataBuilder
            .buildCellValue("requests", Levels.REQUESTS, ColumnTypes.number())
            .aggregateSum()
            .build();

    OlapColumn PASSBACKS = OlapMetaDataBuilder.cellValue("passbacks", Levels.PASSBACKS, ColumnTypes.number());
    OlapColumn ACTIONS = OlapMetaDataBuilder.cellValue("actions", Levels.ACTIONS, ColumnTypes.number());
    OlapColumn MARGIN = OlapMetaDataBuilder.cellValue("margin", Levels.MARGIN, ColumnTypes.percents());
    OlapColumn ACTION_RATE = OlapMetaDataBuilder.cellValue("actionRate", Levels.ACTION_RATE, ColumnTypes.percents());

    OlapColumn LIFE_ACTIONS = OlapMetaDataBuilder.cellValue("lifeActions", Levels.LIFE_ACTIONS, ColumnTypes.number());
    OlapColumn INVALID_IMPRESSIONS = OlapMetaDataBuilder.buildCellValue("invalidImpressions", Levels.INVALID_IMPRESSIONS, ColumnTypes.number()).dependency(IMPS).build();
    OlapColumn INVALID_CLICKS = OlapMetaDataBuilder.buildCellValue("invalidClicks", Levels.INVALID_CLICKS, ColumnTypes.number()).dependency(CLICKS).build();
    OlapColumn INVALID_REQUESTS = OlapMetaDataBuilder.buildCellValue("invalidRequests", Levels.INVALID_REQUESTS, ColumnTypes.number()).dependency(REQUESTS).build();

    OlapColumn UNIQUE_USERS_DAILY_COLO = OlapMetaDataBuilder.cellValue("uniqueUsersDailyColo", Levels.UNIQUE_USERS_DAILY_COLO, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_MONTHLY_COLO = OlapMetaDataBuilder.cellValue("uniqueUsersMonthlyColo", Levels.UNIQUE_USERS_MONTHLY_COLO, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_DAILY_CAMPAIGN = OlapMetaDataBuilder.cellValue("uniqueUsersDailyCampaign", Levels.UNIQUE_USERS_DAILY_CAMPAIGN, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_MONTHLY_CAMPAIGN = OlapMetaDataBuilder.cellValue("uniqueUsersMonthlyCampaign", Levels.UNIQUE_USERS_MONTHLY_CAMPAIGN, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_DAILY_CCG = OlapMetaDataBuilder.cellValue("uniqueUsersDailyCcg", Levels.UNIQUE_USERS_DAILY_CCG, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_MONTHLY_CCG = OlapMetaDataBuilder.cellValue("uniqueUsersMonthlyCcg", Levels.UNIQUE_USERS_MONTHLY_CCG, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_DAILY_CC = OlapMetaDataBuilder.cellValue("uniqueUsersDailyCc", Levels.UNIQUE_USERS_DAILY_CC, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_MONTHLY_CC = OlapMetaDataBuilder.cellValue("uniqueUsersMonthlyCc", Levels.UNIQUE_USERS_MONTHLY_CC, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_DAILY_SITE = OlapMetaDataBuilder.cellValue("uniqueUsersDailySite", Levels.UNIQUE_USERS_DAILY_SITE, ColumnTypes.number());
    OlapColumn UNIQUE_USERS_MONTHLY_SITE = OlapMetaDataBuilder.cellValue("uniqueUsersMonthlySite", Levels.UNIQUE_USERS_MONTHLY_SITE, ColumnTypes.number());

    OlapColumn REQUESTS_PER_PAGE = OlapMetaDataBuilder.cellValue("requestsPerPage", Levels.REQUESTS_PER_PAGE, ColumnTypes.number());
    OlapColumn IMPRESSIONS_PER_PAGE = OlapMetaDataBuilder.cellValue("impressionsPerPage", Levels.IMPRESSIONS_PER_PAGE, ColumnTypes.number());

    /* LIFE */
    OlapColumn LIFE_IMPRESSIONS = OlapMetaDataBuilder
            .buildCellValue("lifeImpressions", Levels.LIFE_IMPRESSIONS, ColumnTypes.number())
            .dependency(REQUESTS)
            .build();

    OlapColumn LIFE_CLICKS = OlapMetaDataBuilder
            .buildCellValue("lifeClicks", Levels.LIFE_CLICKS, ColumnTypes.number())
            .dependency(REQUESTS)
            .build();

    OlapColumn LIFE_CTR = OlapMetaDataBuilder
            .buildCellValue("lifeCTR", Levels.LIFE_CTR, ColumnTypes.percents())
            .dependency(REQUESTS)
            .build();

    OlapColumn LIFE_ACTION_RATE = OlapMetaDataBuilder
            .buildCellValue("lifeActionRate", Levels.LIFE_ACTION_RATE, ColumnTypes.percents())
            .dependency(REQUESTS)
            .build();

    /* with currencies */
    OlapColumn INVENTORY_COST_NET = OlapMetaDataBuilder.buildCellValue(
            "netTotalValue", Levels.INVENTORY_COST_NET, ColumnTypes.currency())
        .dependencies(ADV_CURRENCY)
        .aggregate(OneCurrencyAggregateFunction.factory(ADV_CURRENCY)).build();

    OlapColumn INVENTORY_COST_NET_USD = OlapMetaDataBuilder.buildCellValue(
        "netTotalValue", Levels.INVENTORY_COST_NET_USD, ColumnTypes.currency())
        .aggregateSum().build();

    OlapColumn INVENTORY_COST_GROSS = OlapMetaDataBuilder.buildCellValue(
        "grossTotalValue", Levels.INVENTORY_COST_GROSS, ColumnTypes.currency())
        .dependencies(ADV_CURRENCY)
        .aggregate(OneCurrencyAggregateFunction.factory(ADV_CURRENCY)).build();

    OlapColumn INVENTORY_COST_GROSS_USD = OlapMetaDataBuilder.buildCellValue(
        "grossTotalValue", Levels.INVENTORY_COST_GROSS_USD, ColumnTypes.currency())
        .aggregateSum().build();

    OlapColumn NET_ECPM = OlapMetaDataBuilder.buildCellValue(
        "netECPM",
        Levels.NET_ECPM,
        ColumnTypes.currency())
        .dependency(ADV_CURRENCY)
        .aggregate(ECPMAggregateFunction.factory(INVENTORY_COST_NET, IMPS)).build();
    OlapColumn NET_ECPM_USD = OlapMetaDataBuilder.buildCellValue(
            "netECPM",
            Levels.NET_ECPM_USD,
            ColumnTypes.currency())
            .aggregate(ECPMAggregateFunction.factory(INVENTORY_COST_NET_USD, IMPS)).build();

    OlapColumn GROSS_ECPM = OlapMetaDataBuilder.buildCellValue(
        "grossECPM",
        Levels.GROSS_ECPM,
        ColumnTypes.currency())
        .dependency(ADV_CURRENCY)
        .aggregate(ECPMAggregateFunction.factory(INVENTORY_COST_GROSS, IMPS)).build();
    OlapColumn GROSS_ECPM_USD = OlapMetaDataBuilder.buildCellValue(
            "grossECPM",
            Levels.GROSS_ECPM_USD,
            ColumnTypes.currency())
            .aggregate(ECPMAggregateFunction.factory(INVENTORY_COST_GROSS_USD, IMPS)).build();


    OlapColumn NET_ECPC = OlapMetaDataBuilder.buildCellValue(
        "netECPC",
        Levels.NET_ECPC,
        ColumnTypes.currency())
        .dependency(ADV_CURRENCY)
        .aggregateRatio(INVENTORY_COST_NET, CLICKS).build();
    OlapColumn NET_ECPC_USD = OlapMetaDataBuilder.buildCellValue(
            "netECPC",
            Levels.NET_ECPC_USD,
            ColumnTypes.currency())
            .aggregateRatio(INVENTORY_COST_NET_USD, CLICKS).build();

    OlapColumn GROSS_ECPC = OlapMetaDataBuilder.buildCellValue(
        "grossECPC",
        Levels.GROSS_ECPC,
        ColumnTypes.currency())
            .dependency(ADV_CURRENCY)
            .aggregateRatio(INVENTORY_COST_GROSS, CLICKS).build();
    OlapColumn GROSS_ECPC_USD = OlapMetaDataBuilder.buildCellValue(
            "grossECPC",
            Levels.GROSS_ECPC_USD,
            ColumnTypes.currency())
            .aggregateRatio(INVENTORY_COST_GROSS_USD, CLICKS).build();

    OlapColumn NET_ECPA = OlapMetaDataBuilder.buildCellValue(
        "netECPA",
        Levels.NET_ECPA,
        ColumnTypes.currency())
        .dependency(ADV_CURRENCY)
        .aggregateRatio(INVENTORY_COST_NET, ACTIONS).build();
    OlapColumn NET_ECPA_USD = OlapMetaDataBuilder.buildCellValue(
            "netECPA",
            Levels.NET_ECPA_USD,
            ColumnTypes.currency())
            .aggregateRatio(INVENTORY_COST_NET_USD, ACTIONS).build();

    OlapColumn GROSS_ECPA = OlapMetaDataBuilder.buildCellValue(
        "grossECPA",
        Levels.GROSS_ECPA,
        ColumnTypes.currency())
        .dependency(ADV_CURRENCY)
        .aggregateRatio(INVENTORY_COST_GROSS, ACTIONS).build();
    OlapColumn GROSS_ECPA_USD = OlapMetaDataBuilder.buildCellValue(
            "grossECPA",
           Levels.GROSS_ECPA_USD,
            ColumnTypes.currency())
            .aggregateRatio(INVENTORY_COST_GROSS_USD, ACTIONS).build();

    OlapColumn PAYOUT = Builder.payout("pubPayout", Levels.PAYOUT, Levels.PAYOUT_USD, PUBLISHER_CURRENCY);

    /* MetaData */
    ResolvableMetaData<OlapColumn> INSTANCE = OlapMetaDataBuilder
            .metaData("olapCustomReport")
            .metricsColumns(
                    INVENTORY_COST_NET, INVENTORY_COST_GROSS,
                    IMPS, CLICKS, CTR, ACTIONS, ACTION_RATE,
                    NET_ECPM, GROSS_ECPM, NET_ECPC, GROSS_ECPC, NET_ECPA, GROSS_ECPA,
                    LIFE_IMPRESSIONS, LIFE_CLICKS, LIFE_CTR, LIFE_ACTIONS, LIFE_ACTION_RATE,
                    PAYOUT, MARGIN, REQUESTS,
                    INVALID_IMPRESSIONS, INVALID_CLICKS, INVALID_REQUESTS,
                    REQUESTS_PER_PAGE, IMPRESSIONS_PER_PAGE, PASSBACKS,
                    UNIQUE_USERS_DAILY_COLO, UNIQUE_USERS_MONTHLY_COLO, UNIQUE_USERS_DAILY_CAMPAIGN, UNIQUE_USERS_MONTHLY_CAMPAIGN,
                    UNIQUE_USERS_DAILY_CCG, UNIQUE_USERS_MONTHLY_CCG, UNIQUE_USERS_DAILY_CC, UNIQUE_USERS_MONTHLY_CC,
                    UNIQUE_USERS_DAILY_SITE, UNIQUE_USERS_MONTHLY_SITE)
            .outputColumns(
                    DATE, HOUR, POSITION, PUBLISHER_ACCOUNT_ID, PUBLISHER_ACCOUNT,
                    SITE_ID, SITE, SITE_URL, SITE_CATEGORY_NAME,
                    ISP_ACCOUNT_ID, ISP_ACCOUNT, COLOCATION, COLOCATION_ID,
                    AGENCY_ID, AGENCY, ADVERTISER_ID, ADVERTISER,
                    CAMPAIGN_ID, CAMPAIGN, CREATIVE_GROUP_ID, CREATIVE_GROUP, SIZE_NAME,
                    CHANNEL_TARGET_NAME, CHANNEL_DEVICE_NAME, SIZE_TYPE,
                    CC_ID, CREATIVE_NAME, USER_COUNTRY, RON_FLAG, RATE_NET, RATE_GROSS, RATE_MODEL,
                    TAG_ID, TAG, TAG_PRICING, USER_STATUS_CODE)
            .build();

    public static final Set<OlapColumn> SUMMARY_COLUMNS = new HashSet<>(Arrays.asList(
            REQUESTS, IMPS,
            CLICKS, CTR,
            PAYOUT,
            INVENTORY_COST_NET, INVENTORY_COST_GROSS,
            NET_ECPM, GROSS_ECPM,
            NET_ECPC, GROSS_ECPC,
            NET_ECPA, GROSS_ECPA
    ));

    public static final Set<OlapColumn> NO_SUMMARY_CRITERIA_COLUMUNS = new HashSet<>();


    public static final Set<OlapColumn> RATE_CURRENCY_COLUMNS = new HashSet<>(Arrays.asList(
            RATE_NET, RATE_GROSS
    ));

    public static final Set<OlapColumn> ADVERTISER_CURRENCY_COLUMNS = new HashSet<>(Arrays.asList(
            INVENTORY_COST_NET, INVENTORY_COST_GROSS,
            INVENTORY_COST_NET_USD, INVENTORY_COST_GROSS_USD,
            NET_ECPM, GROSS_ECPM,
            NET_ECPC, GROSS_ECPC,
            NET_ECPA, GROSS_ECPA
    ));

    public static final Set<OlapColumn> UNIQUE_USERS_COLUMNS = new HashSet<>(
            Arrays.asList(
                    UNIQUE_USERS_DAILY_COLO, UNIQUE_USERS_MONTHLY_COLO, UNIQUE_USERS_DAILY_CAMPAIGN, UNIQUE_USERS_MONTHLY_CAMPAIGN,
                    UNIQUE_USERS_DAILY_CCG, UNIQUE_USERS_MONTHLY_CCG, UNIQUE_USERS_DAILY_CC, UNIQUE_USERS_MONTHLY_CC,
                    UNIQUE_USERS_DAILY_SITE, UNIQUE_USERS_MONTHLY_SITE
            )
    );

    abstract class Builder {
        public static OlapColumn payout(
                String id,
                OlapIdentifier accountCurrency,
                OlapIdentifier usdCurrency,
                OlapColumn currencyColumn) {
            return  OlapMetaDataBuilder
                    .buildCellValue(id, new CurrencyMemberResolver(accountCurrency, usdCurrency), ColumnTypes.currency())
                    .dependency(new PayoutDependenciesResolver(currencyColumn))
                    .aggregateSum()
                    .build();
        }
    }
}
