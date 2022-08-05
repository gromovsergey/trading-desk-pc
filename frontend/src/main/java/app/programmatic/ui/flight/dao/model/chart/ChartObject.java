package app.programmatic.ui.flight.dao.model.chart;

import app.programmatic.ui.common.i18n.MessageInterpolator;

import java.sql.ResultSet;
import java.sql.SQLException;

public enum ChartObject {
    FLIGHT(           "statqueries.flight_stats_daily",
                      "statqueries.flight_total_stats",
                      "statqueries.flight_total_stats_to_date",
                      rs -> -1l,
                      rs -> localize("report.column.FLIGHT"),
                      ")--"
    ),
    LINE_ITEM(        "statqueries.line_item_stats_daily",
                      "statqueries.line_item_total_stats",
                      "statqueries.line_item_total_stats_to_date",
                      rs -> -2l,
                      rs -> localize("report.column.LINE_ITEM"),
                      ")--"
    ),
    FLIGHT_CHANNEL(   "statqueries.flight_channel_stats_daily",
                      "statqueries.flight_channel_stats_total",
                      "statqueries.flight_channel_stats_total_to_date",
                      (ResultSet rs) -> rs.getLong("channel_id"),
                      (ResultSet rs) -> rs.getString("channel_name"),
                      ", 'ABE'"
    ),
    LINE_ITEM_CHANNEL("statqueries.line_item_channel_stats_daily",
                      "statqueries.line_item_channel_stats_total",
                      "statqueries.line_item_channel_stats_total_to_date",
                      (ResultSet rs) -> rs.getLong("channel_id"),
                      (ResultSet rs) -> rs.getString("channel_name"),
                      ", 'ABE'"
    ),
    FLIGHT_SITE(      "statqueries.flight_sites_stats_daily",
                      "statqueries.flight_site_stats_total",
                      "statqueries.flight_site_stats_total_to_date",
                      (ResultSet rs) -> rs.getLong("site_id"),
                      (ResultSet rs) -> rs.getString("site_name")
    ),
    LINE_ITEM_SITE(   "statqueries.line_item_sites_stats_daily",
                      "statqueries.line_item_site_stats_total",
                      "statqueries.line_item_site_stats_total_to_date",
                      (ResultSet rs) -> rs.getLong("site_id"),
                      (ResultSet rs) -> rs.getString("site_name")
    ),
    FLIGHT_DEVICE(    "statqueries.flight_channel_stats_daily",
                      "statqueries.flight_channel_stats_total",
                      "statqueries.flight_channel_stats_total_to_date",
                      (ResultSet rs) -> rs.getLong("channel_id"),
                      (ResultSet rs) -> rs.getString("channel_name"),
                      ", 'V'"
    ),
    LINE_ITEM_DEVICE( "statqueries.line_item_channel_stats_daily",
                      "statqueries.line_item_channel_stats_total",
                      "statqueries.line_item_channel_stats_total_to_date",
                      (ResultSet rs) -> rs.getLong("channel_id"),
                      (ResultSet rs) -> rs.getString("channel_name"),
                      ", 'V'"
    ),
    FLIGHT_GEO(       "statqueries.flight_channel_stats_daily",
                      "statqueries.flight_channel_stats_total",
                      "statqueries.flight_channel_stats_total_to_date",
                      (ResultSet rs) -> rs.getLong("channel_id"),
                      (ResultSet rs) -> rs.getString("channel_name"),
                      ", 'G'"
    ),
    LINE_ITEM_GEO(    "statqueries.line_item_channel_stats_daily",
                      "statqueries.line_item_channel_stats_total",
                      "statqueries.line_item_channel_stats_total_to_date",
                      (ResultSet rs) -> rs.getLong("channel_id"),
                      (ResultSet rs) -> rs.getString("channel_name"),
                      ", 'G'"
    );

    private static final int MAX_RESULTS = 10;
    private static final String TOTAL_PARAMS_TEMPL = "(?::int %s, ?::text, %d)";
    private static final String TOTAL_TO_DATE_PARAMS_TEMPL = "(?::int, ?::date %s)";
    private static final String DAILY_PARAMS_TEMPL = "(?::int, ?::date, ?::date %s, ?::text, %d)";

    private final String dailyProcedureName;
    private final String totalProcedureName;
    private final String totalToDateProcedureName;
    private final ChartObjectIdFetcher idFetcher;
    private final ChartObjectNameFetcher nameFetcher;
    private final String fixedParam;

    ChartObject(String dailyProcedureName, String totalProcedureName, String totalToDateProcedureName,
                ChartObjectIdFetcher idFetcher, ChartObjectNameFetcher nameFetcher) {
        this(dailyProcedureName, totalProcedureName, totalToDateProcedureName, idFetcher, nameFetcher, "");
    }

    ChartObject(String dailyProcedureName, String totalProcedureName, String totalToDateProcedureName,
                ChartObjectIdFetcher idFetcher, ChartObjectNameFetcher nameFetcher, String fixedParam) {
        this.dailyProcedureName = dailyProcedureName;
        this.totalProcedureName = totalProcedureName;
        this.totalToDateProcedureName = totalToDateProcedureName;
        this.idFetcher = idFetcher;
        this.nameFetcher = nameFetcher;
        this.fixedParam = fixedParam;
    }

    public String getDailyProcedureName() {
        return dailyProcedureName;
    }

    public String getTotalProcedureName() {
        return totalProcedureName;
    }

    public String getTotalToDateProcedureName() {
        return totalToDateProcedureName;
    }

    public String getDailyProcedureSignature() {
        return getDailyProcedureName() + String.format(DAILY_PARAMS_TEMPL, fixedParam, MAX_RESULTS);
    }

    public String getTotalProcedureSignature() {
        return getTotalProcedureName() + String.format(TOTAL_PARAMS_TEMPL, fixedParam, MAX_RESULTS);
    }

    public String getTotalToDateProcedureSignature() {
        return getTotalToDateProcedureName() + String.format(TOTAL_TO_DATE_PARAMS_TEMPL, fixedParam);
    }

    public ChartObjectIdFetcher getIdFetcher() {
        return idFetcher;
    }

    public ChartObjectNameFetcher getNameFetcher() {
        return nameFetcher;
    }

    public interface ChartObjectIdFetcher {
        Long fetchId(ResultSet rs) throws SQLException;
    }

    public interface ChartObjectNameFetcher {
        String fetchName(ResultSet rs) throws SQLException;
    }

    private static String localize(String template) {
        return MessageInterpolator.getDefaultMessageInterpolator().interpolate(template);
    }
}
