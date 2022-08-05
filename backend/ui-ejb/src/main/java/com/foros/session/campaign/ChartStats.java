package com.foros.session.campaign;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;

public class ChartStats {
    private List<ChartEntry> rows;
    private String y1Type;
    private String y2Type;
    private TimeZone timeZone;
    private String currencyCode;

    public ChartStats(List<ChartEntry> rows, TimeZone timeZone, String y1Type, String y2Type, String currencyCode) {
        this.rows = rows;
        this.timeZone = timeZone;
        this.y1Type = y1Type;
        this.y2Type = y2Type;
        this.currencyCode = currencyCode;
    }

    public List<ChartEntry> getRows() {
        return rows;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public String getY1Type() {
        return y1Type;
    }

    public String getY2Type() {
        return y2Type;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public static class ChartEntry {
        private Date date;
        private Number y1;
        private Number y2;

        public ChartEntry(Date date, Number y1, Number y2) {
            this.date = date;
            this.y1 = y1;
            this.y2 = y2;
        }

        public Date getDate() {
            return date;
        }

        public Number getY1() {
            return y1;
        }

        public Number getY2() {
            return y2;
        }
    }

    public static class ChartHelper{
        private final static List<String> SPECS = Arrays.asList(
            "imps",
            "imps_rt",
            "clicks",
            "clicks_rt",
            "ctr",
            "ctr_rt",
            "inv_cost",
            "inv_cost_rt",
            "tgt_cost",
            "tgt_cost_rt",
            "total_cost",
            "total_cost_rt",
            "uniq",
            "uniq_rt");

        private String y1spec;
        private String y2spec;
        private String xspec;
        private LocalDate fromDate;
        private LocalDate toDate;

        public ChartHelper(TimeZone timeZone, String xspec, String y1spec, String y2spec) {
            if (!SPECS.contains(y1spec)) {
                throw new IllegalArgumentException("Invalid data type: " + y1spec);
            }

            if (!SPECS.contains(y2spec)) {
                throw new IllegalArgumentException("Invalid data type: " + y2spec);
            }

            this.xspec = xspec;
            this.y1spec = y1spec;
            this.y2spec = y2spec;

            if ("30days".equalsIgnoreCase(xspec)) {
                this.fromDate = new LocalDate(DateTimeZone.forTimeZone(timeZone)).minusDays(29);
            }
            this.toDate = new LocalDate(DateTimeZone.forTimeZone(timeZone));
        }

        public String getY1spec() {
            return y1spec;
        }

        public String getY2spec() {
            return y2spec;
        }

        public String getXspec() {
            return xspec;
        }

        public LocalDate getFromDate() {
            return fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }

        public RowMapper<ChartEntry> getRowMapper() {
            return new RowMapper<ChartEntry>() {
                @Override
                public ChartEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new ChartStats.ChartEntry(rs.getDate(1),
                            getNumber(y1spec, rs.getBigDecimal(2)),
                            getNumber(y2spec, rs.getBigDecimal(3)));
                }
            };
        }

        private static Number getNumber(String yspec, BigDecimal y) {
            if (yspec.toLowerCase().startsWith("ctr")) {
                return y.setScale(2, RoundingMode.HALF_EVEN);
            }
            if (!yspec.toLowerCase().contains("cost")) {
                return y.longValue();
            }
            return y;
        }
    }
}
