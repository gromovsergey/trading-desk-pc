package com.foros.session.reporting.advertiser.olap;

import static com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta.Helper.getDisplayMetricColumns;
import static com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta.Helper.getDisplayMetricColumnsCCG;
import static com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta.Helper.set;
import static com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta.Helper.withAllDates;

import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.meta.olap.OlapMetaDataBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface OlapVideoAdvertiserMeta extends OlapAdvertiserMeta {
    List<Set<OlapColumn>> SUBTOTAL_LEVELS = Collections.unmodifiableList(Arrays.asList(
            DATE_COLUMNS,
            set(ADVERTISER),
            set(CAMPAIGN),
            set(CREATIVE_GROUP),
            // Rate for Inventory, Rate for Targeting, Total Rate and Channel Target are available only when Creative Group is on
            set(CREATIVE_GROUP, COUNTRY),
            set(CREATIVE, CREATIVE_SIZE)
    ));

    List<Set<OlapColumn>> CCG_SUBTOTAL_LEVELS = Collections.unmodifiableList(Arrays.asList(
            DATE_COLUMNS,
            set(ADVERTISER),
            set(CAMPAIGN),
            // Rate for Inventory, Rate for Targeting, Total Rate and Channel Target are available only when Creative Group is on
            set(CREATIVE_GROUP, COUNTRY),
            set(CREATIVE, CREATIVE_SIZE)
    ));

    static class VideoAdvertiserReportDescription extends OlapAdvertiserReportDescription {
        public VideoAdvertiserReportDescription(ResolvableMetaData<OlapColumn> resolvableMetaData, Set<OlapColumn> fixedColumns, Set<OlapColumn> defaultColumns) {
            super(resolvableMetaData, fixedColumns, defaultColumns, SUBTOTAL_LEVELS);
        }

        public VideoAdvertiserReportDescription(ResolvableMetaData<OlapColumn> resolvableMetaData, Set<OlapColumn> fixedColumns,
                                                Set<OlapColumn> defaultColumns, List<Set<OlapColumn>> subtotalLevels) {
            super(resolvableMetaData, fixedColumns, defaultColumns, subtotalLevels);
        }
    }

    ResolvableMetaData<OlapColumn> ACCOUNT_PERFORMANCE = OlapMetaDataBuilder.metaData("displayAdvertisingReports")
            .outputColumns(withAllDates())
            .metricsColumns(getDisplayMetricColumns())
            .build();

    OlapAdvertiserReportDescription ACCOUNT_PERFORMANCE_DESC = new VideoAdvertiserReportDescription(
            ACCOUNT_PERFORMANCE,
            set(ADVERTISER),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, ECPM, MARGIN, VIDEO_START, VIDEO_COMPLETE, VIDEO_COMPLETION_RATE, VIDEO_VIEW_RATE)
    );

    ResolvableMetaData<OlapColumn> ADVERTISER_PERFORMANCE = OlapMetaDataBuilder.metaData("displayAdvertisingReports")
            .outputColumns(withAllDates(ADVERTISER))
            .metricsColumns(getDisplayMetricColumns())
            .build();

    OlapAdvertiserReportDescription ADVERTISER_PERFORMANCE_DESC = new VideoAdvertiserReportDescription(
            ADVERTISER_PERFORMANCE,
            set(ADVERTISER),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, ECPM, MARGIN, VIDEO_START, VIDEO_COMPLETE, VIDEO_COMPLETION_RATE, VIDEO_VIEW_RATE)
    );

    ResolvableMetaData<OlapColumn> CAMPAIGN_PERFORMANCE = OlapMetaDataBuilder.metaData("displayAdvertisingReports")
            .outputColumns(withAllDates(
                    ADVERTISER,
                    CAMPAIGN,
                    COUNTRY,
                    DEVICE_CHANNEL_NAME,
                    CREATIVE_SIZE))
            .metricsColumns(getDisplayMetricColumns())
            .build();

    OlapAdvertiserReportDescription CAMPAIGN_PERFORMANCE_DESC = new VideoAdvertiserReportDescription(
            CAMPAIGN_PERFORMANCE,
            set(CAMPAIGN),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, ECPM, MARGIN, VIDEO_START, VIDEO_COMPLETE, VIDEO_COMPLETION_RATE, VIDEO_VIEW_RATE)
    );

    ResolvableMetaData<OlapColumn> CCG_PERFORMANCE = OlapMetaDataBuilder.metaData("displayAdvertisingReports")
            .outputColumns(withAllDates(
                    ADVERTISER,
                    CAMPAIGN,
                    CREATIVE_GROUP,
                    COUNTRY,
                    DEVICE_CHANNEL_NAME,
                    CHANNEL_TARGET,
                    RATE_FOR_INVENTORY, RATE_FOR_INVENTORY_NET, RATE_FOR_INVENTORY_GROSS))
            .metricsColumns(getDisplayMetricColumnsCCG())
            .build();

    OlapAdvertiserReportDescription CCG_PERFORMANCE_DESC = new VideoAdvertiserReportDescription(
            CCG_PERFORMANCE,
            set(CREATIVE_GROUP),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, ECPM, MARGIN, VIDEO_START, VIDEO_COMPLETE, VIDEO_COMPLETION_RATE, VIDEO_VIEW_RATE),
            CCG_SUBTOTAL_LEVELS
    );

    ResolvableMetaData<OlapColumn> CREATIVE_PERFORMANCE = OlapMetaDataBuilder.metaData("displayAdvertisingReports")
            .outputColumns(withAllDates(
                    ADVERTISER,
                    CAMPAIGN,
                    CREATIVE_GROUP,
                    CREATIVE,
                    CREATIVE_SIZE,
                    DEVICE_CHANNEL_NAME))
            .metricsColumns(getDisplayMetricColumns())
            .build();

    OlapAdvertiserReportDescription CREATIVE_PERFORMANCE_DESC = new VideoAdvertiserReportDescription(
            CREATIVE_PERFORMANCE,
            set(CREATIVE),
            set(IMPRESSIONS, CLICKS, CTR, TOTAL_UNIQUE_USERS, ECPM, MARGIN, VIDEO_START, VIDEO_COMPLETE, VIDEO_COMPLETION_RATE, VIDEO_VIEW_RATE)
    );
}
