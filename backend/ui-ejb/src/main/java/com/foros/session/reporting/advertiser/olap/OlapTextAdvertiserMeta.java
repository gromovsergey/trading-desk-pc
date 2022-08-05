package com.foros.session.reporting.advertiser.olap;

import static com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta.Helper.getTextMetricColumns;
import static com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta.Helper.getTextMetricColumnsCCG;
import static com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta.Helper.set;
import static com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta.Helper.withAllDates;
import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.meta.olap.OlapMetaDataBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface OlapTextAdvertiserMeta extends OlapAdvertiserMeta {
    List<Set<OlapColumn>> SUBTOTAL_LEVELS = Collections.unmodifiableList(Arrays.asList(
            DATE_COLUMNS,
            set(ADVERTISER),
            set(CAMPAIGN),
            // Current CPC Bid is available only when Text Ad Group is on
            set(TEXT_AD_GROUP, COUNTRY),
            set(TEXT_AD),
            // Current CPC Bid is available only when Keyword is on
            set(KEYWORD_NAME)
    ));

    static class TextAdvertiserReportDescription extends OlapAdvertiserReportDescription {
        public TextAdvertiserReportDescription(ResolvableMetaData<OlapColumn> resolvableMetaData, Set<OlapColumn> fixedColumns, Set<OlapColumn> defaultColumns) {
            super(resolvableMetaData, fixedColumns, defaultColumns, SUBTOTAL_LEVELS);
        }
    }

    ResolvableMetaData<OlapColumn> ACCOUNT_PERFORMANCE = OlapMetaDataBuilder.metaData("textAdvertisingReports")
            .outputColumns(withAllDates())
            .metricsColumns(getTextMetricColumns())
            .build();

    OlapAdvertiserReportDescription ACCOUNT_PERFORMANCE_DESC = new TextAdvertiserReportDescription(
            ACCOUNT_PERFORMANCE,
            set(ADVERTISER),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, COST, ECPM, AVERAGE_ACTUAL_CPC, MARGIN)
    );

    ResolvableMetaData<OlapColumn> ADVERTISER_PERFORMANCE = OlapMetaDataBuilder.metaData("textAdvertisingReports")
            .outputColumns(withAllDates(ADVERTISER))
            .metricsColumns(getTextMetricColumns())
            .build();

    OlapAdvertiserReportDescription ADVERTISER_PERFORMANCE_DESC = new TextAdvertiserReportDescription(
            ADVERTISER_PERFORMANCE,
            set(ADVERTISER),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, COST, ECPM, AVERAGE_ACTUAL_CPC, MARGIN)
    );

    ResolvableMetaData<OlapColumn> CAMPAIGN_PERFORMANCE = OlapMetaDataBuilder.metaData("textAdvertisingReports")
            .outputColumns(withAllDates(
                    ADVERTISER,
                    CAMPAIGN,
                    COUNTRY,
                    DEVICE_CHANNEL_NAME))
            .metricsColumns(getTextMetricColumns())
            .build();

    OlapAdvertiserReportDescription CAMPAIGN_PERFORMANCE_DESC = new TextAdvertiserReportDescription(
            CAMPAIGN_PERFORMANCE,
            set(CAMPAIGN),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, COST, ECPM, AVERAGE_ACTUAL_CPC, MARGIN)
    );

    ResolvableMetaData<OlapColumn> CCG_PERFORMANCE = OlapMetaDataBuilder.metaData("textAdvertisingReports")
            .outputColumns(withAllDates(
                    ADVERTISER,
                    CAMPAIGN,
                    TEXT_AD_GROUP,
                    COUNTRY,
                    DEVICE_CHANNEL_NAME,
                    CURRENT_CPC_BID, CURRENT_CPC_BID_NET, CURRENT_CPC_BID_GROSS))
            .metricsColumns(getTextMetricColumnsCCG())
            .build();

    OlapAdvertiserReportDescription CCG_PERFORMANCE_DESC = new TextAdvertiserReportDescription(
            CCG_PERFORMANCE,
            set(TEXT_AD_GROUP),
            set(CURRENT_CPC_BID, IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, COST, ECPM, AVERAGE_ACTUAL_CPC, MARGIN)
    );

    ResolvableMetaData<OlapColumn> TEXT_AD_PERFORMANCE = OlapMetaDataBuilder.metaData("textAdvertisingReports")
            .outputColumns(withAllDates(ADVERTISER, CAMPAIGN, TEXT_AD_GROUP, TEXT_AD, DEVICE_CHANNEL_NAME))
            .metricsColumns(getTextMetricColumns())
            .build();

    OlapAdvertiserReportDescription TEXT_AD_PERFORMANCE_DESC = new TextAdvertiserReportDescription(
            TEXT_AD_PERFORMANCE,
            set(TEXT_AD),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, COST, ECPM, AVERAGE_ACTUAL_CPC, MARGIN)
    );

    ResolvableMetaData<OlapColumn> KEYWORD_PERFORMANCE = OlapMetaDataBuilder.metaData("textAdvertisingReports")
            .outputColumns(withAllDates(
                    ADVERTISER, CAMPAIGN, TEXT_AD_GROUP, TEXT_AD,
                    KEYWORD_NAME, KEYWORD_TYPE, CURRENT_CPC_BID,
                    CURRENT_CPC_BID_NET, CURRENT_CPC_BID_GROSS))
            .metricsColumns(CREDITED_IMPRESSIONS, IMPRESSIONS, IMPRESSIONS_HID, CREDITED_CLICKS,
                    CLICKS, CLICKS_HID, CTR, CTR_HID, CREDITED_ACTIONS, MARGIN_KW,
                    TOTAL_UNIQUE_USERS, MONTHLY_UNIQUE_USERS, DAILY_UNIQUE_USERS,
                    NEW_UNIQUE_USERS, KW_COST, KW_COST_NET, KW_COST_GROSS,
                    CAMPAIGN_CREDIT_USED, CAMPAIGN_CREDIT_USED_NET, CAMPAIGN_CREDIT_USED_GROSS,
                    ECPM_KW, ECPM_KW_NET, ECPM_KW_GROSS, ECPU, ECPU_NET, ECPU_GROSS,
                    AVERAGE_ACTUAL_CPC, AVERAGE_ACTUAL_CPC_NET, AVERAGE_ACTUAL_CPC_GROSS)
            .build();

    OlapAdvertiserReportDescription KEYWORD_PERFORMANCE_DESC = new TextAdvertiserReportDescription(
            KEYWORD_PERFORMANCE,
            set(KEYWORD_NAME),
            set(CURRENT_CPC_BID, IMPRESSIONS, CLICKS, CTR, MARGIN_KW, KW_COST, ECPM_KW, AVERAGE_ACTUAL_CPC)
    );
}
