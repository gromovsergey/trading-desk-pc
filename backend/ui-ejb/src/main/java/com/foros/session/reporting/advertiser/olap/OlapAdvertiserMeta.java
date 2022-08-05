package com.foros.session.reporting.advertiser.olap;

import com.foros.model.Context;
import com.foros.model.ExtensionProperty;
import com.phorm.oix.olap.OlapIdentifier;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.olap.MemberResolver;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.meta.olap.OlapMetaDataBuilder;
import com.foros.reporting.tools.subtotal.aggreagate.ECPMAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.MarginAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.WeightedAggregateFunction;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

public interface OlapAdvertiserMeta {
    class ConditionMemberResolver implements MemberResolver<Context> {

        private MemberResolver<Context> truth;
        private MemberResolver<Context> untruth;
        private ExtensionProperty<Boolean> condition;

        public ConditionMemberResolver(MemberResolver<Context> truth, MemberResolver<Context> untruth, ExtensionProperty<Boolean> condition) {
            this.truth = truth;
            this.untruth = untruth;
            this.condition = condition;
        }

        @Override
        public OlapIdentifier resolve(Context reportContext) {
            Boolean conditionValue = reportContext.getProperty(this.condition);
            return (conditionValue != null && conditionValue ? truth.resolve(reportContext) : untruth.resolve(reportContext));
        }
    }

    class NetGrossMemberResolver implements MemberResolver<Context> {

        private OlapIdentifier netMember;
        private OlapIdentifier grossMember;

        public NetGrossMemberResolver(OlapIdentifier netMember, OlapIdentifier grossMember) {
            this.netMember = netMember;
            this.grossMember = grossMember;
        }

        @Override
        public OlapIdentifier resolve(Context reportContext) {
            Boolean isGross = reportContext.getProperty(OlapAdvertiserReportServiceBase.IS_GROSS);
            return (isGross != null && isGross ? grossMember : netMember);
        }
    }

    abstract class RateNetGrossMemberResolver implements MemberResolver<Context> {
        public static MemberResolver<Context> of (final OlapIdentifier gross, final OlapIdentifier net) {
            return new RateNetGrossMemberResolver() {
                @Override
                public OlapIdentifier resolve(Context reportContext) {
                    Boolean isGross = reportContext.getProperty(OlapAdvertiserReportServiceBase.IS_GROSS);

                    if (isGross != null && isGross) {
                        return gross;
                    } else {
                        return net;
                    }
                }
            };
        }
    }

    abstract class AdvCcgMemberResolver implements MemberResolver<Context> {
        public static MemberResolver<Context> of (final OlapIdentifier adv, final OlapIdentifier ccg) {
            return new RateNetGrossMemberResolver() {
                @Override
                public OlapIdentifier resolve(Context reportContext) {
                    OlapDetailLevel reportType = reportContext.getProperty(OlapAdvertiserReportServiceBase.PARAMETERS).getReportType();
                    return (reportType == OlapDetailLevel.CreativeGroup || reportType == OlapDetailLevel.AdGroup) ? ccg : adv;
                }
            };
        }

    }

    class ReportTypeMemberResolver implements MemberResolver<Context> {
        private final OlapIdentifier advertiserMember;
        private final OlapIdentifier campaignMember;
        private final OlapIdentifier ccgMember;
        private final OlapIdentifier ccMember;

        public ReportTypeMemberResolver(
                OlapIdentifier advertiserMember,
                OlapIdentifier campaignMember,
                OlapIdentifier ccgMember,
                OlapIdentifier ccMember) {
            this.advertiserMember = advertiserMember;
            this.campaignMember = campaignMember;
            this.ccgMember = ccgMember;
            this.ccMember = ccMember;
        }

        @Override
        public OlapIdentifier resolve(Context reportContext) {
            OlapAdvertiserReportParameters parameters = reportContext.getProperty(OlapAdvertiserReportServiceBase.PARAMETERS);
            switch (parameters.getReportType()) {
            case Account:
            case Advertiser:
                return advertiserMember;
            case Campaign:
                return campaignMember;
            case Creative:
            case TextAd:
                return ccMember;
            default:
                return ccgMember;
            }
        }
    }

    class Helper {
        static OlapColumn[] withAllDates(OlapColumn... columns) {
            return (OlapColumn[]) ArrayUtils.addAll(
                DATE_COLUMNS.toArray(new OlapColumn[DATE_COLUMNS.size()]),
                columns
                );
        }

        public static Set<OlapColumn> set(OlapColumn... columns) {
            return new HashSet<>(Arrays.asList(columns));
        }

        static OlapColumn[] getTextMetricColumns() {
            return new OlapColumn[] {
                    CREDITED_IMPRESSIONS,
                    IMPRESSIONS, IMPRESSIONS_HID,
                    CREDITED_CLICKS,
                    CLICKS, CLICKS_HID,
                    CTR, CTR_HID,
                    CREDITED_ACTIONS,
                    MARGIN,
                    SELF_SERVICE_COST,
                    SELF_SERVICE_COST_NET,
                    SELF_SERVICE_COST_GROSS,
                    TOTAL_UNIQUE_USERS,
                    MONTHLY_UNIQUE_USERS,
                    DAILY_UNIQUE_USERS,
                    NEW_UNIQUE_USERS,
                    COST, COST_NET, COST_GROSS,
                    TOTAL_VALUE, TOTAL_VALUE_NET, TOTAL_VALUE_GROSS,
                    CAMPAIGN_CREDIT_USED, CAMPAIGN_CREDIT_USED_NET, CAMPAIGN_CREDIT_USED_GROSS,
                    ECPM, ECPM_NET, ECPM_GROSS,
                    ECPU, ECPU_NET, ECPU_GROSS,
                    AVERAGE_ACTUAL_CPC, AVERAGE_ACTUAL_CPC_NET, AVERAGE_ACTUAL_CPC_GROSS
            };
        }

        static OlapColumn[] getTextMetricColumnsCCG() {
            return new OlapColumn[] { CREDITED_IMPRESSIONS, IMPRESSIONS, IMPRESSIONS_HID,
                    CREDITED_CLICKS, CLICKS, CLICKS_HID, CTR, CTR_HID, CREDITED_ACTIONS,
                    MARGIN, SELF_SERVICE_COST, SELF_SERVICE_COST_NET, SELF_SERVICE_COST_GROSS,
                    TOTAL_UNIQUE_USERS, MONTHLY_UNIQUE_USERS,
                    DAILY_UNIQUE_USERS, NEW_UNIQUE_USERS, COST, COST_NET, COST_GROSS,
                    TOTAL_VALUE, TOTAL_VALUE_NET, TOTAL_VALUE_GROSS,
                    CAMPAIGN_CREDIT_USED, CAMPAIGN_CREDIT_USED_NET, CAMPAIGN_CREDIT_USED_GROSS,
                    ECPM, ECPM_NET, ECPM_GROSS, ECPU, ECPU_NET,
                    ECPU_GROSS, AVERAGE_ACTUAL_CPC, AVERAGE_ACTUAL_CPC_NET, AVERAGE_ACTUAL_CPC_GROSS,
                    OPPORTUNITIES_TO_SERVE, AUCTIONS_LOST };
        }

        static OlapColumn[] getDisplayMetricColumns() {
            return new OlapColumn[] {
                    CREDITED_IMPRESSIONS, CREDITED_IMPRESSIONS_FOROS, CREDITED_IMPRESSIONS_WG,
                    IMPRESSIONS, IMPRESSIONS_FOROS, IMPRESSIONS_WG, IMPRESSIONS_HID,
                    CREDITED_CLICKS, CREDITED_CLICKS_FOROS, CREDITED_CLICKS_WG,
                    CLICKS, CLICKS_FOROS, CLICKS_WG, CLICKS_HID,
                    CTR, CTR_FOROS, CTR_WG, CTR_HID,
                    CREDITED_ACTIONS, CREDITED_ACTIONS_FOROS, CREDITED_ACTIONS_WG,
                    MARGIN, SELF_SERVICE_COST, SELF_SERVICE_COST_NET, SELF_SERVICE_COST_GROSS,
                    TOTAL_UNIQUE_USERS, MONTHLY_UNIQUE_USERS, DAILY_UNIQUE_USERS,
                    NEW_UNIQUE_USERS, TARGETING_COST, INVENTORY_COST, INVENTORY_COST_NET,
                    INVENTORY_COST_GROSS, INVENTORY_COST_FOROS, INVENTORY_COST_NET_FOROS,
                    INVENTORY_COST_GROSS_FOROS, INVENTORY_COST_WG, INVENTORY_COST_NET_WG,
                    INVENTORY_COST_GROSS_WG,
                    TOTAL_COST, TOTAL_COST_NET, TOTAL_COST_GROSS,
                    TOTAL_VALUE, TOTAL_VALUE_NET, TOTAL_VALUE_GROSS,
                    CAMPAIGN_CREDIT_USED, CAMPAIGN_CREDIT_USED_NET, CAMPAIGN_CREDIT_USED_GROSS,
                    CAMPAIGN_CREDIT_USED_FOROS, CAMPAIGN_CREDIT_USED_NET_FOROS, CAMPAIGN_CREDIT_USED_GROSS_FOROS,
                    CAMPAIGN_CREDIT_USED_WG, CAMPAIGN_CREDIT_USED_NET_WG, CAMPAIGN_CREDIT_USED_GROSS_WG,
                    ECPU, ECPU_NET, ECPU_GROSS, ECPM, ECPM_NET, ECPM_GROSS,
                    RATE_FOR_INVENTORY_ECPM, RATE_FOR_INVENTORY_ECPM_NET, RATE_FOR_INVENTORY_ECPM_GROSS,
                    RATE_FOR_TARGETING_ECPM, WG_LICENSING_COST, WG_LICENSING_COST_NET, WG_LICENSING_COST_GROSS,
                    VIDEO_START, VIDEO_VIEW, VIDEO_Q1, VIDEO_MIDPOINT, VIDEO_Q3,
                    VIDEO_COMPLETE, VIDEO_COMPLETION_RATE, VIDEO_SKIP, VIDEO_PAUSE, VIDEO_VIEW_RATE, VIDEO_MUTE, VIDEO_UNMUTE};
        }

        static OlapColumn[] getDisplayMetricColumnsCCG() {
            return new OlapColumn[] { CREDITED_IMPRESSIONS, CREDITED_IMPRESSIONS_FOROS, CREDITED_IMPRESSIONS_WG,
                    IMPRESSIONS, IMPRESSIONS_FOROS, IMPRESSIONS_WG, IMPRESSIONS_HID,
                    CREDITED_CLICKS, CREDITED_CLICKS_FOROS, CREDITED_CLICKS_WG,
                    CLICKS, CLICKS_FOROS, CLICKS_WG, CLICKS_HID, CTR, CTR_FOROS, CTR_WG, CTR_HID,
                    CREDITED_ACTIONS, CREDITED_ACTIONS_FOROS, CREDITED_ACTIONS_WG,
                    MARGIN, SELF_SERVICE_COST, SELF_SERVICE_COST_NET, SELF_SERVICE_COST_GROSS,
                    TOTAL_UNIQUE_USERS, MONTHLY_UNIQUE_USERS, DAILY_UNIQUE_USERS,
                    NEW_UNIQUE_USERS, TARGETING_COST, COST, COST_NET, COST_GROSS,
                    INVENTORY_COST, INVENTORY_COST_NET, INVENTORY_COST_GROSS,
                    INVENTORY_COST_FOROS, INVENTORY_COST_NET_FOROS, INVENTORY_COST_GROSS_FOROS,
                    INVENTORY_COST_WG, INVENTORY_COST_NET_WG, INVENTORY_COST_GROSS_WG,
                    TOTAL_COST, TOTAL_COST_NET, TOTAL_COST_GROSS,
                    TOTAL_VALUE, TOTAL_VALUE_NET, TOTAL_VALUE_GROSS,
                    CAMPAIGN_CREDIT_USED, CAMPAIGN_CREDIT_USED_NET, CAMPAIGN_CREDIT_USED_GROSS,
                    CAMPAIGN_CREDIT_USED_FOROS, CAMPAIGN_CREDIT_USED_NET_FOROS, CAMPAIGN_CREDIT_USED_GROSS_FOROS,
                    CAMPAIGN_CREDIT_USED_WG, CAMPAIGN_CREDIT_USED_NET_WG, CAMPAIGN_CREDIT_USED_GROSS_WG,
                    ECPU, ECPU_NET, ECPU_GROSS, ECPM, ECPM_NET, ECPM_GROSS,
                    RATE_FOR_INVENTORY_ECPM, RATE_FOR_INVENTORY_ECPM_NET, RATE_FOR_INVENTORY_ECPM_GROSS,
                    RATE_FOR_TARGETING_ECPM, WG_LICENSING_COST, WG_LICENSING_COST_NET, WG_LICENSING_COST_GROSS,
                    OPPORTUNITIES_TO_SERVE, AUCTIONS_LOST };
        }
    }

    interface Levels {
        /* Date */
        OlapIdentifier DATE = OlapIdentifier.parse("[Country Date]");
        OlapIdentifier DATE_ADDITIONAL = OlapIdentifier.parse("[Country Date.Country Date Additional]");
        OlapIdentifier DATE_SUN_SAT = OlapIdentifier.parse("[Country Date.Country Date Sun-Sat]");
        OlapIdentifier YEAR_VALUE = DATE.append("[Country Year]");
        OlapIdentifier MONTH_VALUE = DATE.append("[Country Month]");
        OlapIdentifier MONTH_WITH_YEAR_VALUE = DATE_ADDITIONAL.append("[Country Month With Year]");
        OlapIdentifier QUARTER_VALUE = DATE_ADDITIONAL.append("[Quarter Date]");
        OlapIdentifier YEAR_VALUE_ADDITIONAL = DATE_ADDITIONAL.append("[Country Year]");
        OlapIdentifier WEEK_MON_SUN_VALUE = DATE_ADDITIONAL.append("[Week Mon-Sun Date]");
        OlapIdentifier WEEK_SUN_SAT_VALUE = DATE_SUN_SAT.append("[Week Sun-Sat Date]");
        OlapIdentifier DATE_VALUE = DATE.append("[Country Date]");

        /* Advertiser Account*/
        OlapIdentifier D_ADV_ACCOUNT = OlapIdentifier.parse("[Advertiser Account]");
        OlapIdentifier AGENCY_ID_ADV = D_ADV_ACCOUNT.append("[Agency ID]");
        OlapIdentifier AGENCY_ADV = D_ADV_ACCOUNT.append("[Agency]");
        OlapIdentifier ADVERTISER_ID_ADV = D_ADV_ACCOUNT.append("[Advertiser Account ID]");
        OlapIdentifier ADVERTISER_ADV = D_ADV_ACCOUNT.append("[Advertiser Account]");

        /* Advertiser Currency*/
        OlapIdentifier D_ADV_CURRENCY = OlapIdentifier.parse("[Advertiser Currency]");
        OlapIdentifier CURRENCY_ADV = D_ADV_CURRENCY.append("[Advertiser Currency]");

        /* Advertiser */
        OlapIdentifier ADVERTISER_DIMENSION = OlapIdentifier.parse("[Advertiser]");
        OlapIdentifier CAMPAIGN_ID_ADV = ADVERTISER_DIMENSION.append("[Campaign ID]");
        OlapIdentifier CAMPAIGN_ADV = ADVERTISER_DIMENSION.append("[Campaign]");
        OlapIdentifier CREATIVE_GROUP_ID_ADV = ADVERTISER_DIMENSION.append("[Creative Group ID]");
        OlapIdentifier CREATIVE_GROUP_ADV = ADVERTISER_DIMENSION.append("[Creative Group]");
        OlapIdentifier CC_ID_ADV = ADVERTISER_DIMENSION.append("[Campaign Creative ID]");
        OlapIdentifier CREATIVE_NAME_ADV = ADVERTISER_DIMENSION.append("[Creative]");

        /* Creative Group Type */
        OlapIdentifier CCG_TYPE = OlapIdentifier.parse("[CCG Type]").append("[CCG Type]");

        /* Creative Group */
        OlapIdentifier CREATIVE_GROUP_DIMENSION = OlapIdentifier.parse("[Creative Group]");
        OlapIdentifier CAMPAIGN_CCG = CREATIVE_GROUP_DIMENSION.append("[Campaign]");
        OlapIdentifier CAMPAIGN_ID_CCG = CREATIVE_GROUP_DIMENSION.append("[Campaign ID]");
        OlapIdentifier CREATIVE_GROUP_CCG = CREATIVE_GROUP_DIMENSION.append("[Creative Group]");
        OlapIdentifier CREATIVE_GROUP_ID_CCG = CREATIVE_GROUP_DIMENSION.append("[Creative Group ID]");

        /* Country */
        OlapIdentifier COUNTRY = OlapIdentifier.parse("[Country]").append("[Country Code]");

        /* Inventory Rate */
        OlapIdentifier INVENTORY_RATE_DIMENSION = OlapIdentifier.parse("[Creative Group.Inventory Rate]");
        OlapIdentifier INVENTORY_RATE = INVENTORY_RATE_DIMENSION.append("[inventory_rate]");
        OlapIdentifier INVENTORY_RATE_GTN = INVENTORY_RATE_DIMENSION.append("[inventory_rate_gtn]");
        OlapIdentifier INVENTORY_RATE_NTG = INVENTORY_RATE_DIMENSION.append("[inventory_rate_ntg]");

        /* Creative Size */
        OlapIdentifier CREATIVE_SIZE_DIMENSION = OlapIdentifier.parse("[Advertiser.Creative Size]");
        OlapIdentifier CREATIVE_SIZE = CREATIVE_SIZE_DIMENSION.append("[Creative Size]");

        /* Creative Group Channel Target */
        OlapIdentifier CREATIVE_GROUP_CHANNEL_TARGET = OlapIdentifier.parse("[Creative Group.ChannelTarget]");
        OlapIdentifier CHANNEL_TARGET_ID = CREATIVE_GROUP_CHANNEL_TARGET.append("[Channel Target ID]");
        OlapIdentifier CHANNEL_TARGET_NAME = CREATIVE_GROUP_CHANNEL_TARGET.append("[Channel Target]");

        /* ChannelDevice */
        OlapIdentifier CHANNEL_DEVICE_DIMENSION = OlapIdentifier.parse("[ChannelDevice]");
        OlapIdentifier CHANNEL_DEVICE_NAME = CHANNEL_DEVICE_DIMENSION.append("[Device Channel]");
        OlapIdentifier CHANNEL_DEVICE_ID = CHANNEL_DEVICE_DIMENSION.append("[Device Channel ID]");

        /* CcgKeyword */
        OlapIdentifier CCG_KEYWORD_DIMENSION = OlapIdentifier.parse("[CcgKeyword]");
        OlapIdentifier KEYWORD_TYPE = CCG_KEYWORD_DIMENSION.append("[Keyword Type]");
        OlapIdentifier KEYWORD_NAME = CCG_KEYWORD_DIMENSION.append("[Keyword]");
        OlapIdentifier KEYWORD_ID = CCG_KEYWORD_DIMENSION.append("[CCG Keyword ID]");

        /* HID Profile */
        OlapIdentifier HID_PROFILE_DIMENSION = OlapIdentifier.parse("[HID Profile]");
        OlapIdentifier HID_PROFILE = HID_PROFILE_DIMENSION.append("[Hid Profile]");

        /* FraudCorrection */
        OlapIdentifier FRAUD_CORRECTION_DIMENSION = OlapIdentifier.parse("[FraudCorrection]");
        OlapIdentifier FRAUD_CORRECTION = FRAUD_CORRECTION_DIMENSION.append("[Fraud Correction]");

        /* Walled Garden */
        OlapIdentifier WALLED_GARDEN_CORRECTION_DIMENSION = OlapIdentifier.parse("[Walled Garden]");
        OlapIdentifier WALLED_GARDEN = FRAUD_CORRECTION_DIMENSION.append("[Walled Garden]");

        /* external_devicechannel */
        OlapIdentifier EXTERNAL_DEVICE_CHANNEL_DIMENSION = OlapIdentifier.parse("[external_devicechannel]");
        OlapIdentifier EDC_ACCOUNT_TYPE = EXTERNAL_DEVICE_CHANNEL_DIMENSION.append("[account_type_id]");
        OlapIdentifier EDC_PARENR_ID = EXTERNAL_DEVICE_CHANNEL_DIMENSION.append("[parent_id]");
        OlapIdentifier EDC_PARENR_NAME = EXTERNAL_DEVICE_CHANNEL_DIMENSION.append("[parent_name]");
        OlapIdentifier EDC_ID = EXTERNAL_DEVICE_CHANNEL_DIMENSION.append("[device_channel_id]");

        /* Measures */
        OlapIdentifier MEASURES = OlapIdentifier.parse("[Measures]");
        OlapIdentifier IMPS = MEASURES.append("[imps]");
        OlapIdentifier CLICKS = MEASURES.append("[clicks]");
        OlapIdentifier CTR = MEASURES.append("[ctr]");
        OlapIdentifier ECPM_NET = MEASURES.append("[ecpm_net]");
        OlapIdentifier ECPM_GROSS = MEASURES.append("[ecpm_gross]");
        OlapIdentifier TARGETING_COST = MEASURES.append("[targeting_cost]");
        OlapIdentifier INVENTORY_COST_NET = MEASURES.append("[inventory_cost_net]");
        OlapIdentifier INVENTORY_COST_NET_WG = MEASURES.append("[inventory_cost_net_wg]");
        OlapIdentifier INVENTORY_COST_NET_FOROS = MEASURES.append("[inventory_cost_net_foros]");
        OlapIdentifier INVENTORY_COST_GROSS = MEASURES.append("[inventory_cost_gross]");
        OlapIdentifier INVENTORY_COST_GROSS_WG = MEASURES.append("[inventory_cost_gross_wg]");
        OlapIdentifier INVENTORY_COST_GROSS_FOROS = MEASURES.append("[inventory_cost_gross_foros]");
        OlapIdentifier IMPRESSIONS_HID = MEASURES.append("[hid_imps]");
        OlapIdentifier CLICKS_HID = MEASURES.append("[hid_clicks]");
        OlapIdentifier CTR_HID = MEASURES.append("[hid_ctr]");
        OlapIdentifier MARGIN = MEASURES.append("[margin]");
        OlapIdentifier CREDITED_IMPRESSIONS = MEASURES.append("[imps_credited]");
        OlapIdentifier CREDITED_IMPRESSIONS_FOROS = MEASURES.append("[imps_credited_foros]");
        OlapIdentifier CREDITED_IMPRESSIONS_WG = MEASURES.append("[imps_credited_wg]");
        OlapIdentifier CREDITED_CLICKS = MEASURES.append("[clicks_credited]");
        OlapIdentifier CREDITED_CLICKS_FOROS = MEASURES.append("[clicks_credited_foros]");
        OlapIdentifier CREDITED_CLICKS_WG = MEASURES.append("[clicks_credited_wg]");
        OlapIdentifier CREDITED_ACTIONS = MEASURES.append("[actions_credited]");
        OlapIdentifier CREDITED_ACTIONS_FOROS = MEASURES.append("[actions_credited_foros]");
        OlapIdentifier CREDITED_ACTIONS_WG = MEASURES.append("[actions_credited_wg]");
        OlapIdentifier CAMPAIGN_CREDIT_USED_NET = MEASURES.append("[campaign_credit_used_net]");
        OlapIdentifier CAMPAIGN_CREDIT_USED_NET_WG = MEASURES.append("[campaign_credit_used_net_wg]");
        OlapIdentifier CAMPAIGN_CREDIT_USED_NET_FOROS = MEASURES.append("[campaign_credit_used_net_foros]");
        OlapIdentifier CAMPAIGN_CREDIT_USED_GROSS = MEASURES.append("[campaign_credit_used_gross]");
        OlapIdentifier CAMPAIGN_CREDIT_USED_GROSS_WG = MEASURES.append("[campaign_credit_used_gross_wg]");
        OlapIdentifier CAMPAIGN_CREDIT_USED_GROSS_FOROS = MEASURES.append("[campaign_credit_used_gross_foros]");
        OlapIdentifier DISPLAY_ADVERTISER_DAILY_UNIQUE_USERS = MEASURES.append("[advertiser_display_unique_users]");
        OlapIdentifier DISPLAY_ADVERTISER_MONTHLY_UNIQUE_USERS = MEASURES.append("[advertiser_display_monthly_unique_users]");
        OlapIdentifier DISPLAY_ADVERTISER_TOTAL_UNIQUE_USERS = MEASURES.append("[advertiser_display_total_unique_users]");
        OlapIdentifier DISPLAY_ADVERTISER_NEW_UNIQUE_USERS = MEASURES.append("[advertiser_display_new_unique_users]");
        OlapIdentifier TEXT_ADVERTISER_DAILY_UNIQUE_USERS = MEASURES.append("[advertiser_text_unique_users]");
        OlapIdentifier TEXT_ADVERTISER_MONTHLY_UNIQUE_USERS = MEASURES.append("[advertiser_text_monthly_unique_users]");
        OlapIdentifier TEXT_ADVERTISER_TOTAL_UNIQUE_USERS = MEASURES.append("[advertiser_text_total_unique_users]");
        OlapIdentifier TEXT_ADVERTISER_NEW_UNIQUE_USERS = MEASURES.append("[advertiser_text_new_unique_users]");
        OlapIdentifier CAMPAIGN_DAILY_UNIQUE_USERS = MEASURES.append("[campaign_unique_users]");
        OlapIdentifier CAMPAIGN_MONTHLY_UNIQUE_USERS = MEASURES.append("[campaign_monthly_unique_users]");
        OlapIdentifier CAMPAIGN_TOTAL_UNIQUE_USERS = MEASURES.append("[campaign_total_unique_users]");
        OlapIdentifier CAMPAIGN_NEW_UNIQUE_USERS = MEASURES.append("[campaign_new_unique_users]");
        OlapIdentifier CCG_DAILY_UNIQUE_USERS = MEASURES.append("[ccg_unique_users]");
        OlapIdentifier CCG_MONTHLY_UNIQUE_USERS = MEASURES.append("[ccg_monthly_unique_users]");
        OlapIdentifier CCG_TOTAL_UNIQUE_USERS = MEASURES.append("[ccg_total_unique_users]");
        OlapIdentifier CCG_NEW_UNIQUE_USERS = MEASURES.append("[ccg_new_unique_users]");
        OlapIdentifier CC_DAILY_UNIQUE_USERS = MEASURES.append("[cc_unique_users]");
        OlapIdentifier CC_MONTHLY_UNIQUE_USERS = MEASURES.append("[cc_monthly_unique_users]");
        OlapIdentifier CC_TOTAL_UNIQUE_USERS = MEASURES.append("[cc_total_unique_users]");
        OlapIdentifier CC_NEW_UNIQUE_USERS = MEASURES.append("[cc_new_unique_users]");
        OlapIdentifier DISPLAY_ADVERTISER_ECPU_NET = MEASURES.append("[advertiser_display_daily_ecpu_net]");
        OlapIdentifier TEXT_ADVERTISER_ECPU_NET = MEASURES.append("[advertiser_text_daily_ecpu_net]");
        OlapIdentifier DISPLAY_ADVERTISER_ECPU_GROSS = MEASURES.append("[advertiser_display_daily_ecpu_gross]");
        OlapIdentifier TEXT_ADVERTISER_ECPU_GROSS = MEASURES.append("[advertiser_text_daily_ecpu_gross]");
        OlapIdentifier CAMPAIGN_ECPU_NET = MEASURES.append("[campaign_daily_ecpu_net]");
        OlapIdentifier CAMPAIGN_ECPU_GROSS = MEASURES.append("[campaign_daily_ecpu_gross]");
        OlapIdentifier CCG_ECPU_NET = MEASURES.append("[ccg_daily_ecpu_net]");
        OlapIdentifier CCG_ECPU_GROSS = MEASURES.append("[ccg_daily_ecpu_gross]");
        OlapIdentifier CC_ECPU_NET = MEASURES.append("[cc_daily_ecpu_net]");
        OlapIdentifier CC_ECPU_GROSS = MEASURES.append("[cc_daily_ecpu_gross]");
        OlapIdentifier OPPORTUNITIES_TO_SERVE = MEASURES.append("[opportunities_to_serve]");
        OlapIdentifier AUCTIONS_LOST = MEASURES.append("[auctions_lost]");
        OlapIdentifier CURRENT_CPC_BID_NET = MEASURES.append("[average_actual_cpc_net]");
        OlapIdentifier CURRENT_CPC_BID_GROSS = MEASURES.append("[average_actual_cpc_gross]");
        OlapIdentifier WG_LICENSING_COST_NET = MEASURES.append("[wg_licensing_cost_net]");
        OlapIdentifier WG_LICENSING_COST_GROSS = MEASURES.append("[wg_licensing_cost_gross]");
        OlapIdentifier RATE_FOR_TARGETING_ECPM = MEASURES.append("[targeting_rate_ecpm]");
        OlapIdentifier RATE_FOR_INVENTORY_ECPM_NET = MEASURES.append("[inventory_rate_ecpm_net]");
        OlapIdentifier RATE_FOR_INVENTORY_ECPM_GROSS = MEASURES.append("[inventory_rate_ecpm_gross]");
        OlapIdentifier CTR_WG = MEASURES.append("[ctr_foros]");
        OlapIdentifier CTR_FOROS = MEASURES.append("[ctr_wg]");
        OlapIdentifier IMPRESSIONS_FOROS = MEASURES.append("[imps_foros]");
        OlapIdentifier IMPRESSIONS_WG = MEASURES.append("[imps_wg]");
        OlapIdentifier CLICKS_FOROS = MEASURES.append("[clicks_foros]");
        OlapIdentifier CLICKS_WG = MEASURES.append("[clicks_wg]");
        OlapIdentifier AVERAGE_ACTUAL_CPC_NET = MEASURES.append("[average_actual_cpc_net]");
        OlapIdentifier AVERAGE_ACTUAL_CPC_GROSS = MEASURES.append("[average_actual_cpc_gross]");
        OlapIdentifier COST_NET = MEASURES.append("[total_cost_net]");
        OlapIdentifier COST_GROSS = MEASURES.append("[total_cost_gross]");
        OlapIdentifier VALUE_NET = MEASURES.append("[total_value_net]");
        OlapIdentifier VALUE_GROSS = MEASURES.append("[total_value_gross]");
        OlapIdentifier KW_COST_NET = MEASURES.append("[cost_net]");
        OlapIdentifier KW_COST_GROSS = MEASURES.append("[cost_gross]");

        OlapIdentifier START = MEASURES.append("[start]");
        OlapIdentifier VIEW = MEASURES.append("[view]");
        OlapIdentifier Q1 = MEASURES.append("[q1]");
        OlapIdentifier MIDPOINT = MEASURES.append("[mid]");
        OlapIdentifier Q3 = MEASURES.append("[q3]");
        OlapIdentifier COMPLETE = MEASURES.append("[complete]");
        OlapIdentifier COMPLETION_RATE = MEASURES.append("[completion_rate]");
        OlapIdentifier SKIP = MEASURES.append("[skip]");
        OlapIdentifier PAUSE = MEASURES.append("[pause]");
        OlapIdentifier VIEW_RATE = MEASURES.append("[view_rate]");
        OlapIdentifier MUTE = MEASURES.append("[mute]");
        OlapIdentifier UNMUTE = MEASURES.append("[unmute]");

        OlapIdentifier PUB_AMOUNT = MEASURES.append("[pub_amount]");
        OlapIdentifier SELF_SERVICE_COST_NET = MEASURES.append("[self_service_cost]");
        OlapIdentifier SELF_SERVICE_COST_GROSS = MEASURES.append("[self_service_cost_gross]");

    }

    /* Output */
    OlapColumn CURRENCY = OlapMetaDataBuilder.buildRowMember("advCurrency", Levels.CURRENCY_ADV, ColumnTypes.string()).build();
    OlapColumn COUNTRY = OlapMetaDataBuilder.buildRowMember("country", Levels.COUNTRY, ColumnTypes.country()).build();

    OlapColumn ADVERTISER = OlapMetaDataBuilder.buildRowMember("adv", Levels.ADVERTISER_ADV, ColumnTypes.string()).build();
    OlapColumn ADVERTISER_ID = OlapMetaDataBuilder.buildRowMember("adv_account_id", Levels.ADVERTISER_ID_ADV, ColumnTypes.id()).dependency(ADVERTISER).build();

    OlapColumn AGENCY_ADV = OlapMetaDataBuilder.buildRowMember("agency", Levels.AGENCY_ADV, ColumnTypes.string()).build();
    OlapColumn AGENCY_ID = OlapMetaDataBuilder.buildRowMember("agency_account_id", Levels.AGENCY_ID_ADV, ColumnTypes.id()).dependency(AGENCY_ADV).build();

    OlapColumn CAMPAIGN = OlapMetaDataBuilder.buildRowMember("campaign", AdvCcgMemberResolver.of(Levels.CAMPAIGN_ADV, Levels.CAMPAIGN_CCG), ColumnTypes.string()).build();
    OlapColumn CAMPAIGN_ID = OlapMetaDataBuilder.buildRowMember("campaignId", AdvCcgMemberResolver.of(Levels.CAMPAIGN_ID_ADV, Levels.CAMPAIGN_ID_CCG), ColumnTypes.id())
            .dependency(CAMPAIGN).build();

    OlapColumn CREATIVE_GROUP = OlapMetaDataBuilder.buildRowMember("creativeGroup", AdvCcgMemberResolver.of(Levels.CREATIVE_GROUP_ADV, Levels.CREATIVE_GROUP_CCG), ColumnTypes.string()).build();
    OlapColumn CREATIVE_GROUP_ID = OlapMetaDataBuilder.buildRowMember("creativeGroupId", AdvCcgMemberResolver.of(Levels.CREATIVE_GROUP_ID_ADV, Levels.CREATIVE_GROUP_ID_CCG), ColumnTypes.id())
            .dependency(CREATIVE_GROUP).build();

    OlapColumn CREATIVE_ID = OlapMetaDataBuilder.rowMember("creativeId", Levels.CC_ID_ADV, ColumnTypes.id());
    OlapColumn CREATIVE = OlapMetaDataBuilder.rowMember("creative", Levels.CREATIVE_NAME_ADV, ColumnTypes.string(), CREATIVE_ID);

    OlapColumn TEXT_AD_GROUP_ID = OlapMetaDataBuilder.buildRowMember("textAdGroupId", AdvCcgMemberResolver.of(Levels.CREATIVE_GROUP_ID_ADV, Levels.CREATIVE_GROUP_ID_CCG), ColumnTypes.id()).build();
    OlapColumn TEXT_AD_GROUP = OlapMetaDataBuilder.buildRowMember("textAdGroup", AdvCcgMemberResolver.of(Levels.CREATIVE_GROUP_ADV, Levels.CREATIVE_GROUP_CCG), ColumnTypes.string())
            .dependency(TEXT_AD_GROUP_ID).build();

    OlapColumn TEXT_AD_ID = OlapMetaDataBuilder.rowMember("textAdId", Levels.CC_ID_ADV, ColumnTypes.id());
    OlapColumn TEXT_AD = OlapMetaDataBuilder.rowMember("textAd", Levels.CREATIVE_NAME_ADV, ColumnTypes.string(), TEXT_AD_ID);

    OlapColumn CREATIVE_SIZE = OlapMetaDataBuilder.rowMember("creativeSize", Levels.CREATIVE_SIZE, ColumnTypes.string());

    OlapColumn CHANNEL_TARGET_ID = OlapMetaDataBuilder.rowMember("channelTargetId", Levels.CHANNEL_TARGET_ID, ColumnTypes.string());
    OlapColumn CHANNEL_TARGET = OlapMetaDataBuilder.rowMember("channelTarget", Levels.CHANNEL_TARGET_NAME, ColumnTypes.string());
    OlapColumn DEVICE_CHANNEL_ID = OlapMetaDataBuilder.rowMember("device", Levels.CHANNEL_DEVICE_ID, ColumnTypes.id());
    OlapColumn DEVICE_CHANNEL_NAME = OlapMetaDataBuilder.rowMember("deviceName", Levels.CHANNEL_DEVICE_NAME, ColumnTypes.string(), DEVICE_CHANNEL_ID);
    OlapColumn KEYWORD_ID = OlapMetaDataBuilder.rowMember("keywordId", Levels.KEYWORD_ID, ColumnTypes.number());
    OlapColumn KEYWORD_NAME = OlapMetaDataBuilder.rowMember("keyword", Levels.KEYWORD_NAME, ColumnTypes.string(), KEYWORD_ID);
    OlapColumn KEYWORD_TYPE = OlapMetaDataBuilder.rowMember("keywordType", Levels.KEYWORD_TYPE, ColumnTypes.keywordType());

    OlapColumn YEAR = OlapMetaDataBuilder.rowMember("year", Levels.YEAR_VALUE_ADDITIONAL, ColumnTypes.string());
    OlapColumn QUARTER = OlapMetaDataBuilder.rowMember("quarter", Levels.QUARTER_VALUE, ColumnTypes.date());
    OlapColumn MONTH = OlapMetaDataBuilder.rowMember("month", Levels.MONTH_WITH_YEAR_VALUE, ColumnTypes.month());
    OlapColumn DATE = OlapMetaDataBuilder.rowMember("date", Levels.DATE_VALUE, ColumnTypes.date());
    OlapColumn DAY_OF_WEEK = OlapMetaDataBuilder.rowMember("dayOfWeek", Levels.DATE_VALUE, ColumnTypes.date());
    OlapColumn WEEK_MON_SUN = OlapMetaDataBuilder.rowMember("weekMonSun", Levels.WEEK_MON_SUN_VALUE, ColumnTypes.date());
    OlapColumn WEEK_SUN_SAT = OlapMetaDataBuilder.rowMember("weekSunSat", Levels.WEEK_SUN_SAT_VALUE, ColumnTypes.date());

    /* Measures */
    OlapColumn IMPRESSIONS = OlapMetaDataBuilder
        .buildCellValue("impressions", Levels.IMPS, ColumnTypes.number())
        .aggregateSum().build();

    OlapColumn CLICKS = OlapMetaDataBuilder.buildCellValue(
        "clicks", Levels.CLICKS, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CLICKS_KTG = OlapMetaDataBuilder.buildCellValue(
        "clicksKtg", Levels.CLICKS, ColumnTypes.number())
        .aggregateSum().build();

    OlapColumn CTR = OlapMetaDataBuilder
        .buildCellValue("CTR", Levels.CTR, ColumnTypes.percents())
        .dependencies(CLICKS, IMPRESSIONS)
        .aggregatePercent(CLICKS, IMPRESSIONS).build();

    OlapColumn TOTAL_COST = OlapMetaDataBuilder.buildCellValue("totalCost", new NetGrossMemberResolver(Levels.COST_NET, Levels.COST_GROSS), ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn TOTAL_COST_NET = OlapMetaDataBuilder.buildCellValue("netTotalCost", Levels.COST_NET, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn TOTAL_COST_GROSS = OlapMetaDataBuilder.buildCellValue("grossTotalCost", Levels.COST_GROSS, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();

    OlapColumn COST = OlapMetaDataBuilder.buildCellValue("cost", new NetGrossMemberResolver(Levels.COST_NET, Levels.COST_GROSS), ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn COST_NET = OlapMetaDataBuilder.buildCellValue("netCost", Levels.COST_NET, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn COST_GROSS = OlapMetaDataBuilder.buildCellValue("grossCost", Levels.COST_GROSS, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();

    OlapColumn TOTAL_VALUE = OlapMetaDataBuilder.buildCellValue("totalValue", new NetGrossMemberResolver(Levels.VALUE_NET, Levels.VALUE_GROSS), ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn TOTAL_VALUE_NET = OlapMetaDataBuilder.buildCellValue("netTotalValue", Levels.VALUE_NET, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn TOTAL_VALUE_GROSS = OlapMetaDataBuilder.buildCellValue("grossTotalValue", Levels.VALUE_GROSS, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();

    OlapColumn TARGETING_COST = OlapMetaDataBuilder.buildCellValue("targetingCost", Levels.TARGETING_COST, ColumnTypes.currency())
        .dependency(CURRENCY).build();
    OlapColumn INVENTORY_COST = OlapMetaDataBuilder.buildCellValue(
        "inventoryCost",
        new NetGrossMemberResolver(Levels.INVENTORY_COST_NET, Levels.INVENTORY_COST_GROSS),
        ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn INVENTORY_COST_NET = OlapMetaDataBuilder.buildCellValue(
        "netInventoryCost",
        Levels.INVENTORY_COST_NET,
        ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn INVENTORY_COST_GROSS = OlapMetaDataBuilder.buildCellValue(
        "grossInventoryCost",
        Levels.INVENTORY_COST_GROSS,
        ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn INVENTORY_COST_FOROS = OlapMetaDataBuilder.buildCellValue(
        "inventoryCostFOROS",
        new NetGrossMemberResolver(Levels.INVENTORY_COST_NET_FOROS, Levels.INVENTORY_COST_GROSS_FOROS),
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn INVENTORY_COST_NET_FOROS = OlapMetaDataBuilder.buildCellValue(
        "netInventoryCostFOROS",
        Levels.INVENTORY_COST_NET_FOROS,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn INVENTORY_COST_GROSS_FOROS = OlapMetaDataBuilder.buildCellValue(
        "grossInventoryCostFOROS",
        Levels.INVENTORY_COST_GROSS_FOROS,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn INVENTORY_COST_WG = OlapMetaDataBuilder.buildCellValue(
        "inventoryCostWG",
        new NetGrossMemberResolver(Levels.INVENTORY_COST_NET_WG, Levels.INVENTORY_COST_GROSS_WG),
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn INVENTORY_COST_NET_WG = OlapMetaDataBuilder.buildCellValue(
        "netInventoryCostWG",
        Levels.INVENTORY_COST_NET_WG,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn INVENTORY_COST_GROSS_WG = OlapMetaDataBuilder.buildCellValue(
        "grossInventoryCostWG",
        Levels.INVENTORY_COST_GROSS_WG,
        ColumnTypes.currency())
        .aggregateSum().build();

    OlapColumn ECPM = OlapMetaDataBuilder.buildCellValue("eCPM", new NetGrossMemberResolver(Levels.ECPM_NET, Levels.ECPM_GROSS), ColumnTypes.currency())
            .aggregate(ECPMAggregateFunction.factory(INVENTORY_COST, IMPRESSIONS))
            .dependency(CURRENCY).build();
    OlapColumn ECPM_NET = OlapMetaDataBuilder.buildCellValue("netECPM", Levels.ECPM_NET, ColumnTypes.currency())
            .dependency(CURRENCY).aggregate(ECPMAggregateFunction.factory(INVENTORY_COST_NET, IMPRESSIONS)).build();
    OlapColumn ECPM_GROSS = OlapMetaDataBuilder.buildCellValue("grossECPM", Levels.ECPM_GROSS, ColumnTypes.currency())
            .dependency(CURRENCY).aggregate(ECPMAggregateFunction.factory(INVENTORY_COST_GROSS, IMPRESSIONS)).build();

    OlapColumn IMPRESSIONS_HID = OlapMetaDataBuilder.buildCellValue("impressions.hid", Levels.IMPRESSIONS_HID, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CLICKS_HID = OlapMetaDataBuilder.buildCellValue("clicks.hid", Levels.CLICKS_HID, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CTR_HID = OlapMetaDataBuilder.buildCellValue("CTR.hid", Levels.CTR_HID, ColumnTypes.percents())
        .aggregatePercent(CLICKS_HID, IMPRESSIONS_HID).build();
    OlapColumn PUB_AMOUNT =  OlapMetaDataBuilder.buildCellValue("pub_amount", Levels.PUB_AMOUNT, ColumnTypes.number()).aggregateSum().build();
    OlapColumn SELF_SERVICE_COST =  OlapMetaDataBuilder.buildCellValue("selfServiceCost", Levels.SELF_SERVICE_COST_NET, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn SELF_SERVICE_COST_NET =  OlapMetaDataBuilder.buildCellValue("selfServiceCostNet", Levels.SELF_SERVICE_COST_NET, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn SELF_SERVICE_COST_GROSS =  OlapMetaDataBuilder.buildCellValue("selfServiceCostGross", Levels.SELF_SERVICE_COST_GROSS, ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn MARGIN = OlapMetaDataBuilder.buildCellValue("margin", Levels.MARGIN, ColumnTypes.percents())
        .aggregate(MarginAggregateFunction.factory(INVENTORY_COST_NET, PUB_AMOUNT)).build();
    OlapColumn CREDITED_IMPRESSIONS = OlapMetaDataBuilder.buildCellValue("creditedImpressions", Levels.CREDITED_IMPRESSIONS, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CREDITED_IMPRESSIONS_FOROS = OlapMetaDataBuilder.buildCellValue("creditedImpressions.foros", Levels.CREDITED_IMPRESSIONS_FOROS, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CREDITED_IMPRESSIONS_WG = OlapMetaDataBuilder.buildCellValue("creditedImpressions.wg", Levels.CREDITED_IMPRESSIONS_WG, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CREDITED_CLICKS = OlapMetaDataBuilder.buildCellValue("creditedClicks", Levels.CREDITED_CLICKS, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CREDITED_CLICKS_FOROS = OlapMetaDataBuilder.buildCellValue("creditedClicks.foros", Levels.CREDITED_CLICKS_FOROS, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CREDITED_CLICKS_WG = OlapMetaDataBuilder.buildCellValue("creditedClicks.wg", Levels.CREDITED_CLICKS_WG, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CREDITED_ACTIONS = OlapMetaDataBuilder.buildCellValue("creditedActions", Levels.CREDITED_ACTIONS, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CREDITED_ACTIONS_FOROS = OlapMetaDataBuilder.buildCellValue("creditedActions.foros", Levels.CREDITED_ACTIONS_FOROS, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CREDITED_ACTIONS_WG = OlapMetaDataBuilder.buildCellValue("creditedActions.wg", Levels.CREDITED_ACTIONS_WG, ColumnTypes.number())
        .aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED = OlapMetaDataBuilder.buildCellValue(
        "campaignCreditUsed",
        new NetGrossMemberResolver(Levels.CAMPAIGN_CREDIT_USED_NET, Levels.CAMPAIGN_CREDIT_USED_GROSS),
        ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED_NET = OlapMetaDataBuilder.buildCellValue(
        "netCampaignCreditUsed",
        Levels.CAMPAIGN_CREDIT_USED_NET,
        ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED_GROSS = OlapMetaDataBuilder.buildCellValue(
        "grossCampaignCreditUsed",
        Levels.CAMPAIGN_CREDIT_USED_GROSS,
        ColumnTypes.currency())
        .dependency(CURRENCY).aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED_FOROS = OlapMetaDataBuilder.buildCellValue(
        "campaignCreditUsed.foros",
        new NetGrossMemberResolver(Levels.CAMPAIGN_CREDIT_USED_NET_FOROS, Levels.CAMPAIGN_CREDIT_USED_GROSS_FOROS),
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED_NET_FOROS = OlapMetaDataBuilder.buildCellValue(
        "netCampaignCreditUsed.foros",
        Levels.CAMPAIGN_CREDIT_USED_NET_FOROS,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED_GROSS_FOROS = OlapMetaDataBuilder.buildCellValue(
        "grossCampaignCreditUsed.foros",
        Levels.CAMPAIGN_CREDIT_USED_GROSS_FOROS,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED_WG = OlapMetaDataBuilder.buildCellValue(
        "campaignCreditUsed.wg",
        new NetGrossMemberResolver(Levels.CAMPAIGN_CREDIT_USED_NET_WG, Levels.CAMPAIGN_CREDIT_USED_GROSS_WG),
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED_NET_WG = OlapMetaDataBuilder.buildCellValue(
        "netCampaignCreditUsed.wg",
        Levels.CAMPAIGN_CREDIT_USED_NET_WG,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn CAMPAIGN_CREDIT_USED_GROSS_WG = OlapMetaDataBuilder.buildCellValue(
        "grossCampaignCreditUsed.wg",
        Levels.CAMPAIGN_CREDIT_USED_GROSS_WG,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn TOTAL_UNIQUE_USERS = Builder.conditional(
            "totalUniqueUsers",
            new ReportTypeMemberResolver(
                    Levels.DISPLAY_ADVERTISER_TOTAL_UNIQUE_USERS,
                    Levels.CAMPAIGN_TOTAL_UNIQUE_USERS,
                    Levels.CCG_TOTAL_UNIQUE_USERS,
                    Levels.CC_TOTAL_UNIQUE_USERS
            ),
            new ReportTypeMemberResolver(
                    Levels.TEXT_ADVERTISER_TOTAL_UNIQUE_USERS,
                    Levels.CAMPAIGN_TOTAL_UNIQUE_USERS,
                    Levels.CCG_TOTAL_UNIQUE_USERS,
                    Levels.CC_TOTAL_UNIQUE_USERS
            ),
            OlapAdvertiserReportServiceBase.IS_DISPLAY,
            ColumnTypes.number());
    OlapColumn MONTHLY_UNIQUE_USERS = Builder.conditional(
            "monthlyUniqueUsers",
            new ReportTypeMemberResolver(
                    Levels.DISPLAY_ADVERTISER_MONTHLY_UNIQUE_USERS,
                    Levels.CAMPAIGN_MONTHLY_UNIQUE_USERS,
                    Levels.CCG_MONTHLY_UNIQUE_USERS,
                    Levels.CC_MONTHLY_UNIQUE_USERS
            ),
            new ReportTypeMemberResolver(
                    Levels.TEXT_ADVERTISER_MONTHLY_UNIQUE_USERS,
                    Levels.CAMPAIGN_MONTHLY_UNIQUE_USERS,
                    Levels.CCG_MONTHLY_UNIQUE_USERS,
                    Levels.CC_MONTHLY_UNIQUE_USERS
            ),
            OlapAdvertiserReportServiceBase.IS_DISPLAY,
            ColumnTypes.number());
    OlapColumn DAILY_UNIQUE_USERS = Builder.conditional(
            "dailyUniqueUsers",
            new ReportTypeMemberResolver(
                    Levels.DISPLAY_ADVERTISER_DAILY_UNIQUE_USERS,
                    Levels.CAMPAIGN_DAILY_UNIQUE_USERS,
                    Levels.CCG_DAILY_UNIQUE_USERS,
                    Levels.CC_DAILY_UNIQUE_USERS
            ),
            new ReportTypeMemberResolver(
                    Levels.TEXT_ADVERTISER_DAILY_UNIQUE_USERS,
                    Levels.CAMPAIGN_DAILY_UNIQUE_USERS,
                    Levels.CCG_DAILY_UNIQUE_USERS,
                    Levels.CC_DAILY_UNIQUE_USERS
            ),
            OlapAdvertiserReportServiceBase.IS_DISPLAY,
            ColumnTypes.number());
    OlapColumn NEW_UNIQUE_USERS = Builder.conditional(
            "newUniqueUsers",
            new ReportTypeMemberResolver(
                    Levels.DISPLAY_ADVERTISER_NEW_UNIQUE_USERS,
                    Levels.CAMPAIGN_NEW_UNIQUE_USERS,
                    Levels.CCG_NEW_UNIQUE_USERS,
                    Levels.CC_NEW_UNIQUE_USERS
            ),
            new ReportTypeMemberResolver(
                    Levels.TEXT_ADVERTISER_NEW_UNIQUE_USERS,
                    Levels.CAMPAIGN_NEW_UNIQUE_USERS,
                    Levels.CCG_NEW_UNIQUE_USERS,
                    Levels.CC_NEW_UNIQUE_USERS),
            OlapAdvertiserReportServiceBase.IS_DISPLAY,
            ColumnTypes.number());
    OlapColumn ECPU = Builder.conditional(
            "eCPU",
            new ConditionMemberResolver(
                    new ReportTypeMemberResolver(
                            Levels.DISPLAY_ADVERTISER_ECPU_GROSS,
                            Levels.CAMPAIGN_ECPU_GROSS,
                            Levels.CCG_ECPU_GROSS,
                            Levels.CC_ECPU_GROSS
                    ),
                    new ReportTypeMemberResolver(
                            Levels.DISPLAY_ADVERTISER_ECPU_NET,
                            Levels.CAMPAIGN_ECPU_NET,
                            Levels.CCG_ECPU_NET,
                            Levels.CC_ECPU_NET
                    ),
                    OlapDisplayAdvertiserReportService.IS_GROSS
            ),
            new ConditionMemberResolver(
                    new ReportTypeMemberResolver(
                            Levels.TEXT_ADVERTISER_ECPU_GROSS,
                            Levels.CAMPAIGN_ECPU_GROSS,
                            Levels.CCG_ECPU_GROSS,
                            Levels.CC_ECPU_GROSS
                    ),
                    new ReportTypeMemberResolver(
                            Levels.TEXT_ADVERTISER_ECPU_NET,
                            Levels.CAMPAIGN_ECPU_NET,
                            Levels.CCG_ECPU_NET,
                            Levels.CC_ECPU_NET
                    ),
                    OlapDisplayAdvertiserReportService.IS_GROSS
            ),
            OlapAdvertiserReportServiceBase.IS_DISPLAY,
            ColumnTypes.currency());
    OlapColumn ECPU_NET = Builder.conditional(
            "netECPU",
            new ReportTypeMemberResolver(
                    Levels.DISPLAY_ADVERTISER_ECPU_NET,
                    Levels.CAMPAIGN_ECPU_NET,
                    Levels.CCG_ECPU_NET,
                    Levels.CC_ECPU_NET
            ),
            new ReportTypeMemberResolver(
                    Levels.TEXT_ADVERTISER_ECPU_NET,
                    Levels.CAMPAIGN_ECPU_NET,
                    Levels.CCG_ECPU_NET,
                    Levels.CC_ECPU_NET
            ),
            OlapAdvertiserReportServiceBase.IS_DISPLAY,
            ColumnTypes.currency());
    OlapColumn ECPU_GROSS = Builder.conditional(
            "grossECPU",
            new ReportTypeMemberResolver(
                    Levels.DISPLAY_ADVERTISER_ECPU_GROSS,
                    Levels.CAMPAIGN_ECPU_GROSS,
                    Levels.CCG_ECPU_GROSS,
                    Levels.CC_ECPU_GROSS
            ),
            new ReportTypeMemberResolver(
                    Levels.TEXT_ADVERTISER_ECPU_GROSS,
                    Levels.CAMPAIGN_ECPU_GROSS,
                    Levels.CCG_ECPU_GROSS,
                    Levels.CC_ECPU_GROSS
            ),
            OlapAdvertiserReportServiceBase.IS_DISPLAY,
            ColumnTypes.currency());
    OlapColumn OPPORTUNITIES_TO_SERVE = OlapMetaDataBuilder.buildCellValue(
        "opportunitiesToServe",
        Levels.OPPORTUNITIES_TO_SERVE,
        ColumnTypes.number()).build();
    OlapColumn AUCTIONS_LOST = OlapMetaDataBuilder.buildCellValue(
        "auctionsLost",
        Levels.AUCTIONS_LOST,
        ColumnTypes.number()).build();
    OlapColumn CURRENT_CPC_BID = OlapMetaDataBuilder.buildCellValue(
        "currentCPCBid",
        new NetGrossMemberResolver(Levels.CURRENT_CPC_BID_NET, Levels.CURRENT_CPC_BID_GROSS),
        ColumnTypes.currency()).build();
    OlapColumn CURRENT_CPC_BID_NET = OlapMetaDataBuilder.buildCellValue(
        "netCurrentCPCBid",
        Levels.CURRENT_CPC_BID_NET,
        ColumnTypes.currency()).build();
    OlapColumn CURRENT_CPC_BID_GROSS = OlapMetaDataBuilder.buildCellValue(
        "grossCurrentCPCBid",
        Levels.CURRENT_CPC_BID_GROSS,
        ColumnTypes.currency()).build();
    OlapColumn WG_LICENSING_COST = OlapMetaDataBuilder.buildCellValue(
        "wgLicensingCost",
        new NetGrossMemberResolver(Levels.WG_LICENSING_COST_NET, Levels.WG_LICENSING_COST_GROSS),
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn WG_LICENSING_COST_NET = OlapMetaDataBuilder.buildCellValue(
        "wgLicensingCostNet",
        Levels.WG_LICENSING_COST_NET,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn WG_LICENSING_COST_GROSS = OlapMetaDataBuilder.buildCellValue(
        "wgLicensingCostGross",
        Levels.WG_LICENSING_COST_GROSS,
        ColumnTypes.currency())
        .aggregateSum().build();
    OlapColumn RATE_FOR_TARGETING_ECPM = OlapMetaDataBuilder.buildCellValue(
        "rateForTargetingEcpm",
        Levels.RATE_FOR_TARGETING_ECPM,
        ColumnTypes.currency())
        .aggregate(ECPMAggregateFunction.factory(TARGETING_COST, IMPRESSIONS)).build();
    OlapColumn RATE_FOR_INVENTORY_ECPM = OlapMetaDataBuilder.buildCellValue(
        "rateForInventoryEcpm",
        new NetGrossMemberResolver(Levels.RATE_FOR_INVENTORY_ECPM_NET, Levels.RATE_FOR_INVENTORY_ECPM_GROSS),
        ColumnTypes.currency())
        .aggregate(ECPMAggregateFunction.factory(INVENTORY_COST, IMPRESSIONS)).build();
    OlapColumn RATE_FOR_INVENTORY_ECPM_NET = OlapMetaDataBuilder.buildCellValue(
        "rateForInventoryEcpmNet",
        Levels.RATE_FOR_INVENTORY_ECPM_NET,
        ColumnTypes.currency())
        .aggregate(ECPMAggregateFunction.factory(INVENTORY_COST_NET, IMPRESSIONS))
        .build();
    OlapColumn RATE_FOR_INVENTORY_ECPM_GROSS = OlapMetaDataBuilder.buildCellValue(
        "rateForInventoryEcpmGross",
        Levels.RATE_FOR_INVENTORY_ECPM_GROSS,
        ColumnTypes.currency())
        .aggregate(ECPMAggregateFunction.factory(INVENTORY_COST_GROSS, IMPRESSIONS))
        .build();
    OlapColumn IMPRESSIONS_WG = OlapMetaDataBuilder.buildCellValue(
        "impressions.wg",
        Levels.IMPRESSIONS_WG,
        ColumnTypes.number()).aggregateSum().build();
    OlapColumn IMPRESSIONS_FOROS = OlapMetaDataBuilder.buildCellValue(
        "impressions.foros",
        Levels.IMPRESSIONS_FOROS,
        ColumnTypes.number()).aggregateSum().build();
    OlapColumn CLICKS_WG = OlapMetaDataBuilder.buildCellValue(
        "clicks.wg",
        Levels.CLICKS_WG,
        ColumnTypes.number()).aggregateSum().build();
    OlapColumn CLICKS_FOROS = OlapMetaDataBuilder.buildCellValue(
        "clicks.foros",
        Levels.CLICKS_FOROS,
        ColumnTypes.number()).aggregateSum().build();
    OlapColumn CTR_WG = OlapMetaDataBuilder.buildCellValue(
        "CTR.wg",
        Levels.CTR_WG,
        ColumnTypes.number()).aggregateSum().build();
    OlapColumn CTR_FOROS = OlapMetaDataBuilder.buildCellValue(
        "CTR.foros",
        Levels.CTR_FOROS,
        ColumnTypes.number()).aggregateSum().build();
    OlapColumn AVERAGE_ACTUAL_CPC = OlapMetaDataBuilder.buildCellValue(
        "averageActualCPC",
        new NetGrossMemberResolver(Levels.AVERAGE_ACTUAL_CPC_NET, Levels.AVERAGE_ACTUAL_CPC_GROSS),
        ColumnTypes.currency()).build();
    OlapColumn AVERAGE_ACTUAL_CPC_NET = OlapMetaDataBuilder.buildCellValue(
        "netAverageActualCPC",
        Levels.AVERAGE_ACTUAL_CPC_NET,
        ColumnTypes.currency()).build();
    OlapColumn AVERAGE_ACTUAL_CPC_GROSS = OlapMetaDataBuilder.buildCellValue(
        "grossAverageActualCPC",
        Levels.AVERAGE_ACTUAL_CPC_GROSS,
        ColumnTypes.currency()).build();
    OlapColumn KW_COST = OlapMetaDataBuilder.buildCellValue(
        "cost",
        Levels.KW_COST_NET,
        ColumnTypes.currency()).build();
    OlapColumn KW_COST_NET = OlapMetaDataBuilder.buildCellValue(
        "netCost",
        Levels.KW_COST_NET,
        ColumnTypes.currency()).build();
    OlapColumn KW_COST_GROSS = OlapMetaDataBuilder.buildCellValue(
        "grossCost",
        Levels.KW_COST_GROSS,
        ColumnTypes.currency()).build();

    OlapColumn MARGIN_KW = OlapMetaDataBuilder.buildCellValue("margin", Levels.MARGIN, ColumnTypes.percents())
            .aggregate(MarginAggregateFunction.factory(KW_COST_NET, PUB_AMOUNT)).build();
    OlapColumn ECPM_KW = OlapMetaDataBuilder.buildCellValue("eCPM", new NetGrossMemberResolver(Levels.ECPM_NET, Levels.ECPM_GROSS), ColumnTypes.currency())
            .dependency(CURRENCY).build();
    OlapColumn ECPM_KW_NET = OlapMetaDataBuilder.buildCellValue("netECPM", Levels.ECPM_NET, ColumnTypes.currency())
            .dependency(CURRENCY).aggregate(ECPMAggregateFunction.factory(KW_COST_NET, IMPRESSIONS)).build();
    OlapColumn ECPM_KW_GROSS = OlapMetaDataBuilder.buildCellValue("grossECPM", Levels.ECPM_GROSS, ColumnTypes.currency())
            .dependency(CURRENCY).aggregate(ECPMAggregateFunction.factory(KW_COST_GROSS, IMPRESSIONS)).build();

    OlapColumn VIDEO_START = OlapMetaDataBuilder
            .buildCellValue("start", Levels.START, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_VIEW = OlapMetaDataBuilder
            .buildCellValue("view", Levels.VIEW, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_Q1 = OlapMetaDataBuilder
            .buildCellValue("q1", Levels.Q1, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_MIDPOINT = OlapMetaDataBuilder
            .buildCellValue("midpoint", Levels.MIDPOINT, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_Q3 = OlapMetaDataBuilder
            .buildCellValue("q3", Levels.Q3, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_COMPLETE = OlapMetaDataBuilder
            .buildCellValue("complete", Levels.COMPLETE, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_COMPLETION_RATE = OlapMetaDataBuilder
            .buildCellValue("completionRate", Levels.COMPLETION_RATE, ColumnTypes.percents())
            .aggregatePercent(VIDEO_COMPLETE, IMPRESSIONS).build();
    OlapColumn VIDEO_SKIP = OlapMetaDataBuilder
            .buildCellValue("skip", Levels.SKIP, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_PAUSE = OlapMetaDataBuilder
            .buildCellValue("pause", Levels.PAUSE, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_VIEW_RATE = OlapMetaDataBuilder
            .buildCellValue("viewRate", Levels.VIEW_RATE, ColumnTypes.percents())
            .aggregate(WeightedAggregateFunction.factory(VIDEO_START, VIDEO_START, 0.125, VIDEO_Q1, 0.25, VIDEO_MIDPOINT, 0.25, VIDEO_Q3, 0.25, VIDEO_COMPLETE, 0.125)).build();
    OlapColumn VIDEO_MUTE = OlapMetaDataBuilder
            .buildCellValue("mute", Levels.MUTE, ColumnTypes.number())
            .aggregateSum().build();
    OlapColumn VIDEO_UNMUTE = OlapMetaDataBuilder
            .buildCellValue("unmute", Levels.UNMUTE, ColumnTypes.number())
            .aggregateSum().build();

    // settings
    OlapColumn RATE_FOR_INVENTORY = OlapMetaDataBuilder.buildRowMember("inventoryRate", Levels.INVENTORY_RATE, ColumnTypes.currency()).
        build();
    OlapColumn RATE_FOR_INVENTORY_NET = OlapMetaDataBuilder.buildRowMember(
            "netInventoryRate",
            RateNetGrossMemberResolver.of(Levels.INVENTORY_RATE_NTG, Levels.INVENTORY_RATE),
            ColumnTypes.currency())
            .build();
    OlapColumn RATE_FOR_INVENTORY_GROSS = OlapMetaDataBuilder.buildRowMember(
            "grossInventoryRate",
            RateNetGrossMemberResolver.of(Levels.INVENTORY_RATE, Levels.INVENTORY_RATE_GTN),
            ColumnTypes.currency())
            .build();

    Set<OlapColumn> DATE_COLUMNS = Collections.unmodifiableSet(Helper.set(
        DATE, WEEK_MON_SUN, WEEK_SUN_SAT, MONTH, QUARTER, YEAR
    ));

    /* MetaData */
    Set<OlapColumn> SUMMARY_COLUMNS = new HashSet<>(Arrays.asList(
            IMPRESSIONS, CLICKS, CTR, MARGIN, SELF_SERVICE_COST, SELF_SERVICE_COST_NET, SELF_SERVICE_COST_GROSS,
            COST_NET, COST_GROSS, TOTAL_VALUE_NET, TOTAL_VALUE_GROSS,
            ECPM_NET, ECPM_GROSS, AVERAGE_ACTUAL_CPC_NET, AVERAGE_ACTUAL_CPC_GROSS, TARGETING_COST, INVENTORY_COST,
            INVENTORY_COST_NET, INVENTORY_COST_GROSS, RATE_FOR_TARGETING_ECPM,
            RATE_FOR_INVENTORY_ECPM, RATE_FOR_INVENTORY_ECPM_NET, RATE_FOR_INVENTORY_ECPM_GROSS,
            VIDEO_START, VIDEO_VIEW, VIDEO_Q1, VIDEO_MIDPOINT, VIDEO_Q3, VIDEO_COMPLETE, VIDEO_COMPLETION_RATE,
            VIDEO_SKIP, VIDEO_PAUSE, VIDEO_VIEW_RATE, VIDEO_MUTE, VIDEO_UNMUTE,
            TOTAL_COST, TOTAL_COST_GROSS, TOTAL_COST_NET, ECPM_KW, ECPM_KW_NET, ECPM_KW_GROSS
        ));

    Map<OlapColumn, List<OlapColumn>> WG_TRIPLETS = Collections.unmodifiableMap(new HashMap<OlapColumn, List<OlapColumn>>() {
        {
            add(IMPRESSIONS, IMPRESSIONS_WG, IMPRESSIONS_FOROS);
            add(CLICKS, CLICKS_WG, CLICKS_FOROS);
            add(CTR, CTR_WG, CTR_FOROS);
            add(INVENTORY_COST, INVENTORY_COST_WG, INVENTORY_COST_FOROS);
            add(INVENTORY_COST_GROSS, INVENTORY_COST_GROSS_WG, INVENTORY_COST_GROSS_FOROS);
            add(INVENTORY_COST_NET, INVENTORY_COST_NET_WG, INVENTORY_COST_NET_FOROS);
            add(CREDITED_IMPRESSIONS, CREDITED_IMPRESSIONS_WG, CREDITED_IMPRESSIONS_FOROS);
            add(CREDITED_CLICKS, CREDITED_CLICKS_WG, CREDITED_CLICKS_FOROS);
            add(CREDITED_ACTIONS, CREDITED_ACTIONS_WG, CREDITED_ACTIONS_FOROS);
            add(CAMPAIGN_CREDIT_USED, CAMPAIGN_CREDIT_USED_WG, CAMPAIGN_CREDIT_USED_FOROS);
            add(CAMPAIGN_CREDIT_USED_NET, CAMPAIGN_CREDIT_USED_NET_WG, CAMPAIGN_CREDIT_USED_NET_FOROS);
            add(CAMPAIGN_CREDIT_USED_GROSS, CAMPAIGN_CREDIT_USED_GROSS_WG, CAMPAIGN_CREDIT_USED_GROSS_FOROS);
        }

        private void add(OlapColumn general, OlapColumn wg, OlapColumn foros) {
            put(general, Collections.unmodifiableList(Arrays.asList(wg, foros)));
        }
    });

    Map<OlapColumn, NetGrossPair> NET_GROSS_TRIPLETS = Collections.unmodifiableMap(new HashMap<OlapColumn, NetGrossPair>() {
        {
            add(RATE_FOR_INVENTORY, RATE_FOR_INVENTORY_NET, RATE_FOR_INVENTORY_GROSS);
            add(RATE_FOR_INVENTORY_ECPM, RATE_FOR_INVENTORY_ECPM_NET, RATE_FOR_INVENTORY_ECPM_GROSS);
            add(ECPM, ECPM_NET, ECPM_GROSS);
            add(ECPU, ECPU_NET, ECPU_GROSS);
            add(COST, COST_NET, COST_GROSS);
            add(TOTAL_COST, TOTAL_COST_NET, TOTAL_COST_GROSS);
            add(TOTAL_VALUE, TOTAL_VALUE_NET, TOTAL_VALUE_GROSS);
            add(CURRENT_CPC_BID, CURRENT_CPC_BID_NET, CURRENT_CPC_BID_GROSS);
            add(AVERAGE_ACTUAL_CPC, AVERAGE_ACTUAL_CPC_NET, AVERAGE_ACTUAL_CPC_GROSS);
            add(INVENTORY_COST_WG, INVENTORY_COST_NET_WG, INVENTORY_COST_GROSS_WG);
            add(INVENTORY_COST_FOROS, INVENTORY_COST_NET_FOROS, INVENTORY_COST_GROSS_FOROS);
            add(INVENTORY_COST, INVENTORY_COST_NET, INVENTORY_COST_GROSS);
            add(CAMPAIGN_CREDIT_USED_WG, CAMPAIGN_CREDIT_USED_NET_WG, CAMPAIGN_CREDIT_USED_GROSS_WG);
            add(CAMPAIGN_CREDIT_USED_FOROS, CAMPAIGN_CREDIT_USED_NET_FOROS, CAMPAIGN_CREDIT_USED_GROSS_FOROS);
            add(CAMPAIGN_CREDIT_USED, CAMPAIGN_CREDIT_USED_NET, CAMPAIGN_CREDIT_USED_GROSS);
            add(WG_LICENSING_COST, WG_LICENSING_COST_NET, WG_LICENSING_COST_GROSS);
        }

        private void add(OlapColumn general, OlapColumn net, OlapColumn gross) {
            put(general, new NetGrossPair(net, gross));
        }
    });

    class NetGrossPair {
        private OlapColumn net;
        private OlapColumn gross;

        public NetGrossPair(OlapColumn net, OlapColumn gross) {
            this.net = net;
            this.gross = gross;
        }

        public OlapColumn getNet() {
            return net;
        }

        public OlapColumn getGross() {
            return gross;
        }
    }

    Set<OlapColumn> UNIQUE_USERS_COLUMNS = Collections.unmodifiableSet(Helper.set(
        DAILY_UNIQUE_USERS,
        MONTHLY_UNIQUE_USERS,
        TOTAL_UNIQUE_USERS,
        NEW_UNIQUE_USERS
        ));

    Set<OlapColumn> HID_COLUMNS = Collections.unmodifiableSet(Helper.set(
        IMPRESSIONS_HID,
        CLICKS_HID,
        CTR_HID
        ));

    Set<OlapColumn> CREDITED_COLUMNS = Collections.unmodifiableSet(Helper.set(
        CREDITED_IMPRESSIONS, CREDITED_IMPRESSIONS_FOROS, CREDITED_IMPRESSIONS_WG,
        CREDITED_CLICKS, CREDITED_CLICKS_FOROS, CREDITED_CLICKS_WG,
        CREDITED_ACTIONS, CREDITED_ACTIONS_FOROS, CREDITED_ACTIONS_WG,
        CAMPAIGN_CREDIT_USED, CAMPAIGN_CREDIT_USED_FOROS, CAMPAIGN_CREDIT_USED_WG
        ));

    abstract class Builder {
        public static OlapColumn conditional(
                String id,
                MemberResolver<Context> first,
                MemberResolver<Context> second,
                ExtensionProperty<Boolean> condition,
                ColumnType columnType) {
            return OlapMetaDataBuilder.buildCellValue(
                id,
                new ConditionMemberResolver(first, second, condition),
                columnType).build();
        }
    }
}
