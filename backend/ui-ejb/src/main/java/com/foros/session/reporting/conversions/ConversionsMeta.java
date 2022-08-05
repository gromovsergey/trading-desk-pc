package com.foros.session.reporting.conversions;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.MetaDataBuilder;
import com.foros.reporting.meta.ResolvableMetaData;

import java.util.Arrays;
import java.util.List;

public interface ConversionsMeta {
    // statistics
    DbColumn IMPRESSIONS = buildColumn("impressions", "imps", ColumnTypes.number());
    DbColumn CLICKS = buildColumn("clicks", "clicks", ColumnTypes.number());
    DbColumn CTR = buildColumn("CTR", "ctr", ColumnTypes.percents());

    DbColumn POST_IMP_CONV = buildColumn("post_imp_conv", "imp_conv", ColumnTypes.number());//imp_conv
    DbColumn POST_IMP_CR = buildColumn("post_imp_cr", "imp_cr", ColumnTypes.percents());//imp_cr
    DbColumn POST_CLICK_CONV = buildColumn("post_click_conv", "click_conv", ColumnTypes.number());//click_conv
    DbColumn POST_CLICK_CR = buildColumn("post_click_cr", "click_cr", ColumnTypes.percents());//click_cr

    DbColumn COST = buildColumn("cost", "total_cost", ColumnTypes.currency());
    DbColumn REVENUE = buildColumn("revenue", "revenue", ColumnTypes.currency());
    DbColumn ROI = buildColumn("roi", "roi", ColumnTypes.percents());
    DbColumn TTC_IMPRESSIONS = buildColumn("ttcImpressions", "time_conv_imp", ColumnTypes.number());
    DbColumn TTC_CLICKS = buildColumn("ttcClicks", "time_conv_click", ColumnTypes.number());
    DbColumn POST_IMP_1 = buildColumn("postImp1", "post_imp_1", ColumnTypes.number());
    DbColumn POST_IMP_2_7 = buildColumn("postImp2_7", "post_imp_2", ColumnTypes.number());
    DbColumn POST_IMP_8_30 = buildColumn("postImp8_30", "post_imp_8", ColumnTypes.number());
    DbColumn POST_CLICK_1 = buildColumn("postClick1", "post_click_1", ColumnTypes.number());
    DbColumn POST_CLICK_2_7 = buildColumn("postClick2_7", "post_click_2", ColumnTypes.number());
    DbColumn POST_CLICK_8_30 = buildColumn("postClick8_30", "post_click_8", ColumnTypes.number());

    // settings
    DbColumn DATE = buildColumn("date", "adv_sdate", ColumnTypes.date());

    DbColumn ADVERTISER_ID = buildColumn("advertiserId", "adv_id", ColumnTypes.id());
    DbColumn ADVERTISER_VISIBLE = buildColumn("advertiserVisible", "adv_visible", ColumnTypes.bool());
    DbColumn ADVERTISER = buildColumn("advertiser", "adv_name", ColumnTypes.string(), ADVERTISER_ID, ADVERTISER_VISIBLE);

    DbColumn CAMPAIGN_ID = buildColumn("campaignId", "campaign_id", ColumnTypes.id());
    DbColumn CAMPAIGN_VISIBLE = buildColumn("campaignVisible", "campaign_visible", ColumnTypes.bool());
    DbColumn CAMPAIGN = buildColumn("campaign", "campaign_name", ColumnTypes.string(), CAMPAIGN_ID, CAMPAIGN_VISIBLE);

    DbColumn CREATIVE_GROUP_ID = buildColumn("creativeGroupId", "ccg_id", ColumnTypes.id());
    DbColumn CREATIVE_GROUP_VISIBLE = buildColumn("creativeGroupVisible", "ccg_visible", ColumnTypes.bool());
    DbColumn CREATIVE_GROUP = buildColumn("creativeGroup", "ccg_name", ColumnTypes.string(), CREATIVE_GROUP_ID, CREATIVE_GROUP_VISIBLE);

    DbColumn CHANNEL_ID = buildColumn("channelId", "channel_id", ColumnTypes.id());
    DbColumn CHANNEL_VISIBLE = buildColumn("channelVisible", "channel_visible", ColumnTypes.bool());
    DbColumn CHANNEL_ACCOUNT_ROLE_ID = buildColumn("channelAccountRoleId", "channel_account_role_id", ColumnTypes.id());
    DbColumn CHANNEL = buildColumn("channel", "channel_name", ColumnTypes.string(), CHANNEL_ID, CHANNEL_VISIBLE, CHANNEL_ACCOUNT_ROLE_ID);

    DbColumn CREATIVE_ID = buildColumn("creativeId", "cc_id", ColumnTypes.id());
    DbColumn CREATIVE_VISIBLE = buildColumn("creativeVisible", "cc_visible", ColumnTypes.bool());
    DbColumn CREATIVE = buildColumn("creative", "creative_name", ColumnTypes.string(), CREATIVE_ID, CREATIVE_VISIBLE);

    DbColumn CONVERSION_ID = buildColumn("conversionId", "action_id", ColumnTypes.id());
    DbColumn CONVERSION_VISIBLE = buildColumn("conversionVisible", "action_visible", ColumnTypes.bool());
    DbColumn CONVERSION = buildColumn("conversion", "action_name", ColumnTypes.string(), CONVERSION_ID, CONVERSION_VISIBLE);

    DbColumn CONVERSION_CATEGORY = buildColumn("conversionCategory", "conv_category_id", ColumnTypes.id());
    DbColumn ORDER_ID = buildColumn("orderID", "order_id", ColumnTypes.string());

    DbColumn PUBLISHER_ID = buildColumn("publisherId", "pub_id", ColumnTypes.id());
    DbColumn PUBLISHER_VISIBLE = buildColumn("publisherVisible", "pub_visible", ColumnTypes.bool());
    DbColumn PUBLISHER = buildColumn("publisher", "pub_name", ColumnTypes.string(), PUBLISHER_ID, PUBLISHER_VISIBLE);

    ResolvableMetaData<DbColumn> META = MetaDataBuilder.metaData("conversionsReport")
        .metricsColumns(
            IMPRESSIONS, CLICKS, CTR,
            POST_IMP_CONV, POST_IMP_CR,
            POST_CLICK_CONV, POST_CLICK_CR,
            COST, REVENUE, ROI,
            TTC_IMPRESSIONS, TTC_CLICKS,
            POST_IMP_1, POST_IMP_2_7, POST_IMP_8_30,
            POST_CLICK_1, POST_CLICK_2_7, POST_CLICK_8_30
        )
        .outputColumns(
            ADVERTISER, CAMPAIGN, CREATIVE_GROUP, CHANNEL, CREATIVE,
            CONVERSION, CONVERSION_ID, CONVERSION_CATEGORY, ORDER_ID, PUBLISHER
        )
        .build();

    ResolvableMetaData<DbColumn> META_BY_DATE = MetaDataBuilder.metaData("conversionsReport")
        .metricsColumns(
            IMPRESSIONS, CLICKS, CTR,
            POST_IMP_CONV, POST_IMP_CR,
            POST_CLICK_CONV, POST_CLICK_CR,
            COST, REVENUE, ROI,
            TTC_IMPRESSIONS, TTC_CLICKS,
            POST_IMP_1, POST_IMP_2_7, POST_IMP_8_30,
            POST_CLICK_1, POST_CLICK_2_7, POST_CLICK_8_30
        )
        .outputColumns(
            DATE, ADVERTISER, CAMPAIGN, CREATIVE_GROUP, CHANNEL, CREATIVE,
            CONVERSION, CONVERSION_CATEGORY, ORDER_ID, PUBLISHER
        )
        .build();

    List<DbColumn> NOT_DEFAULT = Arrays.asList(
            COST,
            POST_IMP_CONV,
            POST_CLICK_CONV,
            REVENUE, TTC_IMPRESSIONS, TTC_CLICKS,
            POST_IMP_1, POST_IMP_2_7, POST_IMP_8_30,
            POST_CLICK_1, POST_CLICK_2_7, POST_CLICK_8_30,
            CONVERSION, CONVERSION_CATEGORY);

    static final String ADVERTISER_URL_PATTERN = "../../advertiser/account/view.action?id=%d";
    static final String EX_ADVERTISER_URL_PATTERN = "../../myAccount/agencyAdvertiserView.action?id=%d";
    static final String CAMPAIGN_URL_PATTERN = "../../campaign/view.action?id=%d";
    static final String CREATIVE_GROUP_URL_PATTERN = "../../campaign/group/view.action?id=%d";
    static final String CHANNEL_URL_PATTERN = "../../channel/view.action?id=%d";
    static final String CONVERSION_URL_PATTERN = "../../Action/view.action?id=%d";
    static final String CREATIVE_URL_PATTERN = "../../campaign/group/creative/view.action?id=%d";
    static final String PUBLISHER_URL_PATTERN = "/admin/publisher/account/view.action?id=%d";
}
