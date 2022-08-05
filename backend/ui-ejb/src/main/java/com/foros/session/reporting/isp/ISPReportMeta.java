package com.foros.session.reporting.isp;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface ISPReportMeta {

    DbColumn DATE = buildColumn("date", "sdate", ColumnTypes.date());
    DbColumn COLOCATION_ID = buildColumn("colocationId", "colo_id", ColumnTypes.id());
    DbColumn COLOCATION_NAME = buildColumn("colocation", "colocation_name", ColumnTypes.string(), COLOCATION_ID);
    DbColumn IMPRESSIONS = buildColumn("impressions", "imps", ColumnTypes.number());
//    DbColumn HID_IMPRESSIONS = buildColumn("impressions.hid.percent", "hid_imps", ColumnTypes.number());
    DbColumn REVENUE = buildColumn("revenue", "revenue", ColumnTypes.currency());
//    DbColumn HID_REVENUE = buildColumn("revenue.hid", "hid_revenue", ColumnTypes.number());
    DbColumn ARPM = buildColumn("ARPM", "arpm", ColumnTypes.currency());
//    DbColumn HID_ARPM = buildColumn("ARPM.hid", "hid_arpm", ColumnTypes.currency());
    DbColumn ARPU_DAILY = buildColumn("ARPU", "arpu_daily", ColumnTypes.currency());
    DbColumn ARPU_RANGE = buildColumn("ARPU", "arpu_range", ColumnTypes.currency());
    DbColumn ACTIVE_DAILY_HIDS = buildColumn("activeDaily.hid", "active_daily_hids", ColumnTypes.number());
    DbColumn TOTAL_UNIQUE_USERS = buildColumn("totalUniqueUsers", "unique_users_range", ColumnTypes.number());
    DbColumn DAILY_UNIQUE_USERS = buildColumn("dailyUniqueUsers", "unique_users_daily", ColumnTypes.number());
    DbColumn UNIQUE_USERS_7DAYS = buildColumn("uniqueUsers7Days", "unique_users_7days", ColumnTypes.number());
    DbColumn UNIQUE_USERS_30DAYS = buildColumn("uniqueUsers30Days", "unique_users_30days", ColumnTypes.number());
    DbColumn UNIQUE_USERS_CALENDAR_WEEKLY = buildColumn("uniqueUsersCalendarWeekly", "unique_users_calendar_weekly", ColumnTypes.number());
    DbColumn UNIQUE_USERS_CALENDAR_MONTHLY = buildColumn("uniqueUsersCalendarMonthly", "unique_users_calendar_monthly", ColumnTypes.number());
    DbColumn AVG_USERS_PER_DAY = buildColumn("averageUsersPerDay", "avg_users_per_day", ColumnTypes.number());

    ResolvableMetaData<DbColumn> BY_DATE = metaData("ISPReport")
            .outputColumns(DATE)
            .metricsColumns(IMPRESSIONS, /*HID_IMPRESSIONS,*/
                    REVENUE, /*HID_REVENUE,*/ ARPM, /*HID_ARPM,*/ ACTIVE_DAILY_HIDS,
                    DAILY_UNIQUE_USERS, UNIQUE_USERS_CALENDAR_WEEKLY, UNIQUE_USERS_CALENDAR_MONTHLY, UNIQUE_USERS_7DAYS, UNIQUE_USERS_30DAYS, ARPU_DAILY)
            .build(); 

    ResolvableMetaData<DbColumn> BY_COLOCATION = metaData("ISPReport")
            .outputColumns(COLOCATION_NAME)
            .metricsColumns(IMPRESSIONS, /*HID_IMPRESSIONS,*/
                    REVENUE, /*HID_REVENUE,*/ ARPM, /*HID_ARPM,*/ ARPU_RANGE, ACTIVE_DAILY_HIDS,
                    AVG_USERS_PER_DAY, TOTAL_UNIQUE_USERS)
            .build();

    Collection<DbColumn> EXCLUDABLE_FROM_SUMMARY = Collections.unmodifiableCollection(
            Arrays.asList(ACTIVE_DAILY_HIDS, DAILY_UNIQUE_USERS, UNIQUE_USERS_CALENDAR_WEEKLY, UNIQUE_USERS_CALENDAR_MONTHLY, UNIQUE_USERS_7DAYS, UNIQUE_USERS_30DAYS));

    Collection<DbColumn> EXCLUDABLE_FOR_EXTERNAL = Collections.unmodifiableCollection(
            Arrays.asList(/*HID_IMPRESSIONS, HID_REVENUE, HID_ARPM,*/ ACTIVE_DAILY_HIDS));

    Collection<DbColumn> EXCLUDABLE_BY_MIN_DATE_RANGE = Collections.unmodifiableCollection(
            Arrays.asList(ARPU_RANGE, TOTAL_UNIQUE_USERS));

    int MIN_DATE_RANGE_FOR_TOTALS = 31;
}
