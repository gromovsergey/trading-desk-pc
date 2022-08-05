package app.programmatic.ui.reporting.model;

import java.util.EnumSet;

import static app.programmatic.ui.reporting.model.ReportColumn.*;
import static app.programmatic.ui.reporting.model.ReportColumnLocation.STATISTIC;
import static app.programmatic.ui.reporting.model.ReportColumnType.CURRENCY_COLUMN;

public enum Report{

    ADVERTISER(
            EnumSet.of(
                    DATE,
                    FLIGHT,
                    LINE_ITEM,
                    CREATIVE,
                    GEOLOCATION,
                    IMPRESSIONS,
                    CLICKS,
                    CTR,
                    PUB_AMOUNT,
                    COST,
                    TOTAL_COST,
                    AGENCY_MARGIN,
                    MARGINALITY,
                    TOTAL_UNIQUE_USERS,
                    DAILY_UNIQUE_USERS,
                    ECPM,
                    START,
                    FIRST_QUARTILE,
                    MIDPOINT,
                    THIRD_QUARTILE,
                    COMPLETE,
                    COMPLETION_RATE,
                    SKIP,
                    PAUSE,
                    VIEW_RATE,
                    MUTE,
                    UNMUTE,
                    RESUME,
                    FULLSCREEN,
                    ERROR
            ),
            EnumSet.of(
                    DATE,
                    FLIGHT,
                    IMPRESSIONS,
                    CLICKS,
                    CTR
            ),
            EnumSet.noneOf(ReportColumn.class)
    ),
    DETAILED(
            EnumSet.of(
                    DATE,
                    HOUR,
                    SSP,
                    ReportColumn.ADVERTISER,
                    AGENCY,
                    FLIGHT,
                    LINE_ITEM,
                    CREATIVE_SIZE,
                    DEVICE,
                    USER_COUNTRY,
                    USER_STATUS,
                    IMPRESSIONS,
                    CLICKS,
                    CTR,
                    REQUESTS,
                    INVALID_IMPRESSIONS,
                    INVALID_CLICKS,
                    INVALID_REQUESTS,
                    TOTAL_VALUE_NET,
                    TOTAL_VALUE_GROSS,
                    PAYOUTS,
                    MARGIN
            ),
            EnumSet.of(
                    DATE,
                    IMPRESSIONS,
                    CLICKS,
                    CTR,
                    REQUESTS
            ),
            EnumSet.noneOf(ReportColumn.class)
    ),
    SEGMENTS(
            EnumSet.of(
                    FLIGHT,
                    LINE_ITEM,
                    DATE,
                    CHANNEL,
                    IMPRESSIONS,
                    CLICKS,
                    CTR
            ),
            EnumSet.of(
                    DATE,
                    CHANNEL,
                    IMPRESSIONS,
                    CLICKS,
                    CTR
            ),
            EnumSet.of(
                    CHANNEL,
                    IMPRESSIONS,
                    CLICKS,
                    CTR
            )
    ),
    DOMAINS(
            EnumSet.of(
                    FLIGHT,
                    SITE,
                    DOMAIN,
                    IMPRESSIONS,
                    CLICKS,
                    CTR,
                    PUB_AMOUNT,
                    COST,
                    TOTAL_COST,
                    AGENCY_MARGIN,
                    MARGINALITY,
                    START,
                    FIRST_QUARTILE,
                    MIDPOINT,
                    THIRD_QUARTILE,
                    COMPLETE,
                    COMPLETION_RATE,
                    SKIP,
                    PAUSE,
                    VIEW_RATE,
                    MUTE,
                    UNMUTE,
                    RESUME,
                    FULLSCREEN,
                    ERROR
            ),
            EnumSet.of(
                    FLIGHT,
                    SITE,
                    DOMAIN,
                    IMPRESSIONS,
                    CLICKS,
                    CTR
            ),
            EnumSet.of(
                    SITE
            )
    ),
    PUBLISHER(
            EnumSet.of(
                    DATE,
                    SITE,
                    TAG,
                    REQUESTS,
                    IMPRESSIONS,
                    PUB_AMOUNT,
                    CLICKS,
                    CTR,
                    INVALID_REQUESTS,
                    INVALID_IMPRESSIONS,
                    INVALID_CLICKS
            ),
            EnumSet.of(
                    DATE,
                    REQUESTS,
                    IMPRESSIONS,
                    PUB_AMOUNT
            ),
            EnumSet.noneOf(ReportColumn.class)
    ),
    REFERRER(
            EnumSet.of(
                    DOMAIN,
                    EXTERNAL_TAG_ID,
                    REQUESTS,
                    OPTED_IN_REQUESTS,
                    BIDS,
                    IMPRESSIONS,
                    CLICKS,
                    FLOOR,
                    FLOOR_ALL,
                    FLOOR_WIN,
                    FLOOR_LOST,
                    BIDS_WIN,
                    BIDS_LOST,
                    SPENT
            ),
            EnumSet.of(
                    DOMAIN,
                    EXTERNAL_TAG_ID,
                    REQUESTS,
                    OPTED_IN_REQUESTS,
                    BIDS,
                    IMPRESSIONS,
                    CLICKS,
                    FLOOR,
                    FLOOR_ALL,
                    FLOOR_WIN,
                    FLOOR_LOST,
                    BIDS_WIN,
                    BIDS_LOST,
                    SPENT
            ),
            EnumSet.noneOf(ReportColumn.class)
    ),
    CONVERSIONS(
            EnumSet.of(
                    DATE,
                    FLIGHT,
                    LINE_ITEM,
                    CHANNEL,
                    CREATIVE,
                    CONVERSION,
                    ORDER_ID,
                    SSP,
                    IMPRESSIONS,
                    CLICKS,
                    CTR,
                    REVENUE,
                    POST_IMP_CONV,
                    POST_IMP_CR,
                    POST_CLICK_CONV,
                    POST_CLICK_CR,
                    CLIENT_BUDGET,
                    TIME_TO_CONV_IMP,
                    TIME_TO_CONV_CLICK,
                    POST_IMP_1_DAY,
                    POST_IMP_7_DAYS,
                    POST_IMP_30_DAYS,
                    POST_CLICK_1_DAY,
                    POST_CLICK_7_DAYS,
                    POST_CLICK_30_DAYS
            ),
            EnumSet.of(
                    DATE,
                    CONVERSION,
                    POST_IMP_CONV,
                    POST_IMP_CR,
                    POST_CLICK_CONV,
                    POST_CLICK_CR,
                    CLIENT_BUDGET
            ),
            EnumSet.of(
                    POST_IMP_CONV,
                    POST_IMP_CR,
                    POST_CLICK_CONV,
                    POST_CLICK_CR
            )
    );

    private EnumSet<ReportColumn> available;
    private EnumSet<ReportColumn> defaults;
    private EnumSet<ReportColumn> required;

    Report(EnumSet<ReportColumn> available, EnumSet<ReportColumn> defaults, EnumSet<ReportColumn> required) {
        this.available = available;
        this.defaults = defaults;
        this.required = required;
    }

    public EnumSet<ReportColumn> getAvailable() {
        return available;
    }

    public EnumSet<ReportColumn> getDefaults() {
        return defaults;
    }

    public EnumSet<ReportColumn> getRequired() {
        return required;
    }
}
