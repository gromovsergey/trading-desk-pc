package app.programmatic.ui.reporting.model;

import static app.programmatic.ui.reporting.model.ReportColumnLocation.*;
import static app.programmatic.ui.reporting.model.ReportColumnType.*;

public enum ReportColumn {
    DATE                (SETTINGS, DATE_COLUMN, "sdate"),
    HOUR                (SETTINGS, INT_COLUMN, "hour"),
    FLIGHT              (SETTINGS, TEXT_COLUMN, "campaign_name"),
    LINE_ITEM           (SETTINGS, TEXT_COLUMN, "ccg_name"),
    CHANNEL             (SETTINGS, TEXT_COLUMN, "channel_name"),
    CONVERSION          (SETTINGS, TEXT_COLUMN, "conversion_name"),
    CREATIVE            (SETTINGS, TEXT_COLUMN, "creative_name"),
    CREATIVE_SIZE       (SETTINGS, TEXT_COLUMN, "creative_size_name"),
    GEOLOCATION         (SETTINGS, TEXT_COLUMN, "geolocation"),
    ORDER_ID            (SETTINGS, TEXT_COLUMN, "order_id"),
    SITE                (SETTINGS, TEXT_COLUMN, "site_name"),
    SSP                 (SETTINGS, TEXT_COLUMN, "publisher_name"),
    DOMAIN              (SETTINGS, TEXT_COLUMN, "domain"),
    TAG                 (SETTINGS, TEXT_COLUMN, "tag_name"),
    EXTERNAL_TAG_ID     (SETTINGS, TEXT_COLUMN, "ext_tag_id"),
    ADVERTISER          (SETTINGS, TEXT_COLUMN, "advertiser_name"),
    AGENCY              (SETTINGS, TEXT_COLUMN, "agency_name"),
    DEVICE              (SETTINGS, TEXT_COLUMN, "device_channel_name"),
    USER_COUNTRY        (SETTINGS, TEXT_COLUMN, "user_country"),
    USER_STATUS         (SETTINGS, TEXT_COLUMN, "user_status"),

    REQUESTS            (STATISTIC, INT_COLUMN, "requests"),
    IMPRESSIONS         (STATISTIC, INT_COLUMN, "imps"),
    CLICKS              (STATISTIC, INT_COLUMN, "clicks"),
    CTR                 (STATISTIC, PERCENT_COLUMN, "ctr"),
    PUB_AMOUNT          (STATISTIC, CURRENCY_COLUMN, "pub_amount"/*, "currency_code"*/),
    COST                (STATISTIC, CURRENCY_COLUMN, "cost"),
    TOTAL_COST          (STATISTIC, CURRENCY_COLUMN, "total_cost"),
    AGENCY_MARGIN       (STATISTIC, CURRENCY_COLUMN, "agency_margin"),
    MARGINALITY         (STATISTIC, PERCENT_COLUMN, "marginality"),
    TOTAL_UNIQUE_USERS  (STATISTIC, INT_COLUMN, "total_unique_users"),
    DAILY_UNIQUE_USERS  (STATISTIC, INT_COLUMN, "daily_unique_users"),
    ECPM                (STATISTIC, CURRENCY_COLUMN, "ecpm"),
    SPENT               (STATISTIC, CURRENCY_COLUMN, "spent"),
    REVENUE             (STATISTIC, CURRENCY_COLUMN, "value"),
    POST_IMP_CONV       (STATISTIC, INT_COLUMN, "post_imp_conv"),
    POST_IMP_CR         (STATISTIC, PERCENT_COLUMN, "post_imp_cr"),
    POST_CLICK_CONV     (STATISTIC, INT_COLUMN, "post_click_conv"),
    POST_CLICK_CR       (STATISTIC, PERCENT_COLUMN, "post_click_cr"),
    CLIENT_BUDGET       (STATISTIC, CURRENCY_COLUMN, "client_budget"),
    INVALID_REQUESTS    (STATISTIC, INT_COLUMN, "invalid_requests"),
    INVALID_IMPRESSIONS (STATISTIC, INT_COLUMN, "invalid_imps"),
    INVALID_CLICKS      (STATISTIC, INT_COLUMN, "invalid_clicks"),
    OPTED_IN_REQUESTS   (STATISTIC, INT_COLUMN, "opted_in_requests"),
    BIDS                (STATISTIC, INT_COLUMN, "bids"),
    BIDS_WIN            (STATISTIC, CURRENCY_COLUMN, "bids_win"),
    BIDS_LOST           (STATISTIC, CURRENCY_COLUMN, "bids_lost"),
    FLOOR               (STATISTIC, CURRENCY_COLUMN, "floor"),
    FLOOR_ALL           (STATISTIC, CURRENCY_COLUMN, "floor_all"),
    FLOOR_WIN           (STATISTIC, CURRENCY_COLUMN, "floor_win"),
    FLOOR_LOST          (STATISTIC, CURRENCY_COLUMN, "floor_lost"),
    TOTAL_VALUE_NET     (STATISTIC, CURRENCY_COLUMN, "total_value_net"),
    TOTAL_VALUE_GROSS   (STATISTIC, CURRENCY_COLUMN, "total_value_gross"),
    PAYOUTS             (STATISTIC, CURRENCY_COLUMN, "payouts"/*, "currency_code"*/),
    MARGIN              (STATISTIC, CURRENCY_COLUMN, "margin"),

    START               (VIDEO_STATISTIC, INT_COLUMN, "start_count"),
    FIRST_QUARTILE      (VIDEO_STATISTIC, INT_COLUMN, "q1_count"),
    MIDPOINT            (VIDEO_STATISTIC, INT_COLUMN, "mid_count"),
    THIRD_QUARTILE      (VIDEO_STATISTIC, INT_COLUMN, "q3_count"),
    COMPLETE            (VIDEO_STATISTIC, INT_COLUMN, "complete_count"),
    COMPLETION_RATE     (VIDEO_STATISTIC, INT_COLUMN, "completion_rate"),
    SKIP                (VIDEO_STATISTIC, INT_COLUMN, "skip_count"),
    PAUSE               (VIDEO_STATISTIC, INT_COLUMN, "pause_count"),
    VIEW_RATE           (VIDEO_STATISTIC, INT_COLUMN, "view_rate"),
    MUTE                (VIDEO_STATISTIC, INT_COLUMN, "mute_count"),
    UNMUTE              (VIDEO_STATISTIC, INT_COLUMN, "unmute_count"),
    RESUME              (VIDEO_STATISTIC, INT_COLUMN, "resume_count"),
    FULLSCREEN          (VIDEO_STATISTIC, INT_COLUMN, "fullscreen_count"),
    ERROR               (VIDEO_STATISTIC, INT_COLUMN, "error_count"),

    TIME_TO_CONV_IMP    (TIME_STATISTIC, INT_COLUMN, "time_to_conv_imp"),
    TIME_TO_CONV_CLICK  (TIME_STATISTIC, INT_COLUMN, "time_to_conv_click"),
    POST_IMP_1_DAY      (TIME_STATISTIC, INT_COLUMN, "post_imp_conv_1d"),
    POST_IMP_7_DAYS     (TIME_STATISTIC, INT_COLUMN, "post_imp_conv_2d_7d"),
    POST_IMP_30_DAYS    (TIME_STATISTIC, INT_COLUMN, "post_imp_conv_2d_7d"),
    POST_CLICK_1_DAY    (TIME_STATISTIC, INT_COLUMN, "post_click_conv_1d"),
    POST_CLICK_7_DAYS   (TIME_STATISTIC, INT_COLUMN, "post_click_conv_2d_7d"),
    POST_CLICK_30_DAYS  (TIME_STATISTIC, INT_COLUMN, "post_click_conv_8d_30d");

    private static final String KEY_PREFIX = "report.column.";

    private ReportColumnLocation location;
    private ReportColumnType columnType;
    private String columnName;
    private String currencySignColumnName;

    ReportColumn(ReportColumnLocation location, ReportColumnType columnType, String columnName) {
        this(location, columnType, columnName, null);
    }

    ReportColumn(ReportColumnLocation location, ReportColumnType columnType, String columnName, String currencySignColumnName) {
        this.location = location;
        this.columnType = columnType;
        this.columnName = columnName;
        this.currencySignColumnName = currencySignColumnName;
    }

    public ReportColumnLocation getLocation() {
        return location;
    }

    public ReportColumnType getColumnType() {
        return columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getCurrencySignColumnName() {
        return currencySignColumnName;
    }

    public String getKey() {
        return KEY_PREFIX + toString();
    }
}
