package app.programmatic.ui.flight.dao.model.chart;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum ChartMetric {
    IMPS(  "imps",
           (ResultSet rs, String paramName) -> rs.getLong(paramName),
           Long.valueOf(0),
           (Number v1, Number v2) -> (Long)v1 + (Long)v2,
           (Number v1, Number v2) -> ((Long)v1).compareTo((Long)v2)),
    CLICKS("clicks",
           (ResultSet rs, String paramName) -> rs.getLong(paramName),
           Long.valueOf(0),
           (Number v1, Number v2) -> (Long)v1 + (Long)v2,
           (Number v1, Number v2) -> ((Long)v1).compareTo((Long)v2)),
    CTR(   "ctr",
           (ResultSet rs, String paramName) -> rs.getBigDecimal(paramName).setScale(2, BigDecimal.ROUND_HALF_UP),
           BigDecimal.ZERO,
           (Number v1, Number v2) -> ((BigDecimal)v1).add((BigDecimal)v2),
           (Number v1, Number v2) -> ((BigDecimal)v1).compareTo((BigDecimal)v2)),
    POST_IMP_CONV("post_imp_conv",
            (ResultSet rs, String paramName) -> rs.getLong(paramName),
            Long.valueOf(0),
            (Number v1, Number v2) -> (Long)v1 + (Long)v2,
            (Number v1, Number v2) -> ((Long)v1).compareTo((Long)v2)),
    POST_CLICK_CONV("post_click_conv",
            (ResultSet rs, String paramName) -> rs.getLong(paramName),
            Long.valueOf(0),
            (Number v1, Number v2) -> (Long)v1 + (Long)v2,
            (Number v1, Number v2) -> ((Long)v1).compareTo((Long)v2));

    private final String procParamName;
    private final FetchOp fetchOp;
    private final Number zero;
    private final AddOp addOp;
    private final CompareOp compareOp;

    ChartMetric(String procParamName, FetchOp fetchOp, Number zero, AddOp addOp, CompareOp compareOp) {
        this.procParamName = procParamName;
        this.fetchOp = fetchOp;
        this.zero = zero;
        this.addOp = addOp;
        this.compareOp = compareOp;
    }

    public String getProcParamName() {
        return procParamName;
    }

    public <T extends Number> T fetch(ResultSet rs) throws SQLException {
        return (T)fetchOp.fetch(rs, getProcParamName());
    }

    public Number getZero() {
        return zero;
    }

    public <T extends Number> T add(T v1, T v2) {
        return (T)addOp.add(v1, v2);
    }

    public <T extends Number> int compare(T v1, T v2) {
        return compareOp.compare(v1, v2);
    }

    private interface AddOp<T extends Number> {
        T add(T v1, T v2);
    }

    private interface FetchOp<T extends Number> {
        T fetch(ResultSet rs, String paramName) throws SQLException;
    }

    private interface CompareOp<T extends Number> {
        int compare(T v1, T v2);
    }
}
