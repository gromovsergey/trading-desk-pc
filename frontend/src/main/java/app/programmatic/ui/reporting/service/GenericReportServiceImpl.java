package app.programmatic.ui.reporting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.reporting.model.*;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;
import app.programmatic.ui.reporting.tools.*;
import app.programmatic.ui.reporting.view.ReportMeta;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static app.programmatic.ui.reporting.model.ReportColumnLocation.SETTINGS;

public abstract class GenericReportServiceImpl<P extends ReportParameters> {

    @Autowired
    private ColumnAvailabilityHelper columnAvailabilityHelper;

    @Value("#{T(java.time.LocalDate).parse('${report.theStartOfHistory}')}")
    private LocalDate theStartOfHistory = LocalDate.of(2020, Month.JANUARY, 1);
    private LocalDateTime theStartOfHistoryTime;

    private static final JsonReportFormatter jsonReportFormatter = new JsonReportFormatter();
    private static final ExcelReportFormatter excelReportFormatter = new ExcelReportFormatter();
    private static final CsvReportFormatter csvReportFormatter = new CsvReportFormatter();

    private static final MessageInterpolator MESSAGE_INTERPOLATOR = MessageInterpolator.getDefaultMessageInterpolator();

    protected abstract ReportColumnFormatter getReportColumnFormatter(P parameters);

    protected abstract List<ColumnValue[]> executeQuery(P parameters, RowMapper<ColumnValue[]> rowMapper);

    protected List<ColumnValue[]> executeTotalQuery(P parameters, RowMapper<ColumnValue[]> rowMapper) {
        return null;
    }

    public ReportMeta getReportMeta(P parameters) {
        ReportMeta meta = new ReportMeta(parameters.getReport());
        EnumSet<ReportColumn> unacceptableColumns = columnAvailabilityHelper.getUnacceptableColumns(parameters);
        meta.getAvailable().removeAll(unacceptableColumns);
        return meta;
    }

    protected byte[] runReport(P parameters, ReportFormat format) {
        ReportFormatter reportFormatter = getReportFormatter(format);
        ReportColumnFormatter columnFormatter = getReportColumnFormatter(parameters);

        ReportRows rows = getReportData(parameters);
        ByteArrayOutputStream reportResult = reportFormatter.formatRows(
                rows,
                columnFormatter
        );

        return reportResult.toByteArray();
    }

    private ReportRows getReportData(P parameters) {
        String[] header = parameters.getSelectedColumns().stream()
                .map(column -> MESSAGE_INTERPOLATOR.interpolate(column.getKey()))
                .collect(Collectors.toList())
                .toArray(new String[parameters.getSelectedColumns().size()]);

        List<ColumnValue[]> result = getMainReportData(parameters);
        if (result.isEmpty()) {
            return new ReportRows(header, result);
        }

        List<ColumnValue[]> totalList = getTotalReportData(parameters);
        ColumnValue[] total = totalList != null && !totalList.isEmpty() ? totalList.get(0) : null;
        return new ReportRows(header, result, total);
    }

    private List<ColumnValue[]> getMainReportData(P parameters) {
        return executeQuery(parameters, new RowMapper<ColumnValue[]>() {
            @Override
            public ColumnValue[] mapRow(ResultSet rs, int ind) throws SQLException {
                ReportColumn[] columns = parameters.getSelectedColumns().toArray(new ReportColumn[parameters.getSelectedColumns().size()]);
                ColumnValue[] row = new ColumnValue[columns.length];

                boolean isRowNotEmpty = false;
                for (int i = 0; i < columns.length; i++) {
                    ReportColumn column = columns[i];
                    ColumnValue value = readColumnValue(rs, column);
                    row[i] = value;
                    isRowNotEmpty = isRowNotEmpty || column.getLocation() != SETTINGS && !value.isZero();
                }

                return isRowNotEmpty ? row : null;
            }
        }).stream()
                .filter(row -> row != null)
                .collect(Collectors.toList());
    }

    private List<ColumnValue[]> getTotalReportData(P parameters) {
        return executeTotalQuery(parameters, new RowMapper<ColumnValue[]>() {
            @Override
            public ColumnValue[] mapRow(ResultSet rs, int ind) throws SQLException {
                ReportColumn[] columns = parameters.getSelectedColumns().toArray(new ReportColumn[parameters.getSelectedColumns().size()]);
                ColumnValue[] row = new ColumnValue[columns.length];

                for (int i = 0; i < columns.length; i++) {
                    ReportColumn column = columns[i];

                    if (column.getLocation() != SETTINGS) {
                        ColumnValue value = readColumnValue(rs, column);
                        row[i] = value;
                    }
                }

                return row;
            }
        });
    }

    protected ColumnValue readColumnValue(ResultSet rs, ReportColumn column) throws SQLException {
        String columnName = column.getColumnName();
        ReportColumnType columnType = column.getColumnType();
        switch (columnType) {
            case DATE_COLUMN:
                return new ColumnValue(rs.getTimestamp(columnName).toLocalDateTime().toLocalDate(), column, false);
            case TEXT_COLUMN:
                return new ColumnValue(rs.getString(columnName), column, false);
            case INT_COLUMN:
                Long result1 = rs.getLong(columnName);
                result1 = !rs.wasNull() ? result1 : null;
                return new ColumnValue(result1, column, columnType.getZeroValue().equals(result1));
            case FLOAT_COLUMN:
            case CURRENCY_COLUMN:
            case PERCENT_COLUMN:
                BigDecimal result2 = rs.getBigDecimal(columnName);
                boolean isZero = result2 != null && ((BigDecimal) columnType.getZeroValue()).compareTo(result2) == 0;
                return new ColumnValue(result2, column, isZero);
            default:
                throw new IllegalArgumentException("Unexpected ReportColumnType: " + columnType);
        }
    }

    protected LocalDateTime getRestrictedDayStart(P parameters) {
        if (parameters == null
                || parameters.getDateStart() == null
                || getStartOfHistoryTime().isAfter(parameters.getDateStart())) {
            return getStartOfHistoryTime();
        }

        return parameters.getDateStart();
    }

    private static ReportFormatter getReportFormatter(ReportFormat format) {
        switch (format) {
            case JSON: return jsonReportFormatter;
            case CSV: return csvReportFormatter;
            case EXCEL: return excelReportFormatter;
        }

        throw new IllegalArgumentException("Unexpected report format: " + format);
    }

    private LocalDateTime getStartOfHistoryTime() {
        if (theStartOfHistoryTime == null) {
            theStartOfHistoryTime = theStartOfHistory.atStartOfDay();
        }
        return theStartOfHistoryTime;
    }
}
