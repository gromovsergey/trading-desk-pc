package app.programmatic.ui.reporting.detailed;

import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.reporting.model.ReportColumnType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.reporting.model.ReportColumn;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;
import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.service.GenericReportServiceImpl;
import app.programmatic.ui.reporting.tools.ReportColumnFormatter;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

@Service
@Validated
@Slf4j
public class DetailedReportServiceImpl extends GenericReportServiceImpl<DetailedReportParameters> implements DetailedReportService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private DataSourceService dsService;

    @Override
    @Restrict(restriction = "report.detailed", parameters = "parameters.accountId")
    public byte[] runReportDetailed(DetailedReportParameters parameters, ReportFormat format) {
        log.info("F: runReportNew; Report Name: " + "report.detailed" + parameters);
        return super.runReport(parameters, format);
    }

    @Override
    protected ReportColumnFormatter getReportColumnFormatter(DetailedReportParameters parameters) {
        //Account account = accountService.findAccountUnchecked(parameters.getAccountId());
        return new ReportColumnFormatter(
                // Issue #243: report is moved to hard-coded locale, please
                // return back when multi-language will be needed
                LOCALE_RU,
                "RUB",//account.getCurrencyCode(),
                2//account.getCurrencyAccuracy()
        );
    }

    @Override
    protected List<ColumnValue[]> executeQuery(DetailedReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        boolean includeDate = parameters.getSelectedColumns().contains(ReportColumn.DATE);
        boolean includeHour = parameters.getSelectedColumns().contains(ReportColumn.HOUR);
        boolean includePublisher = parameters.getSelectedColumns().contains(ReportColumn.SSP);
        boolean includeAdvertiser = parameters.getSelectedColumns().contains(ReportColumn.ADVERTISER);
        boolean includeFlight = parameters.getSelectedColumns().contains(ReportColumn.FLIGHT);
        boolean includeLineItem = parameters.getSelectedColumns().contains(ReportColumn.LINE_ITEM);
        boolean includeCreativeSize = parameters.getSelectedColumns().contains(ReportColumn.CREATIVE_SIZE);
        boolean includeDevice = parameters.getSelectedColumns().contains(ReportColumn.DEVICE);
        boolean includeCountry = parameters.getSelectedColumns().contains(ReportColumn.USER_COUNTRY);
        boolean includeStatus = parameters.getSelectedColumns().contains(ReportColumn.USER_STATUS);

        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.ersatz_custom_report(?::date, ?::date, ?::int, ?::int, ?::boolean, " +
                                                                             "?::boolean, ?::boolean, ?::boolean, ?::boolean, " +
                                                                             "?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::boolean)",
                        new Object[]{
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                parameters.getAdvertiserAccountId(), // may be null = all
                                parameters.getPublisherAccountId(), // may be null = all
                                includeDate,
                                includeHour,
                                includePublisher,
                                includeAdvertiser,
                                includeFlight,
                                includeLineItem,
                                includeCreativeSize,
                                includeDevice,
                                includeCountry,
                                includeStatus
                        },
                        rowMapper
                )
        );
    }

    @Override
    protected ColumnValue readColumnValue(ResultSet rs, ReportColumn column) throws SQLException {
        ReportColumnType columnType = column.getColumnType();
        if (columnType == ReportColumnType.CURRENCY_COLUMN
                && column.getCurrencySignColumnName() != null) {
            String columnName = column.getColumnName();
            BigDecimal result2 = rs.getBigDecimal(columnName);
            boolean isZero = result2 != null && ((BigDecimal) columnType.getZeroValue()).compareTo(result2) == 0;
            String currencySign = rs.getString(column.getCurrencySignColumnName());
            return new ColumnValue(result2, column, isZero, currencySign);
        }

        return super.readColumnValue(rs, column);
    }

    @Override
    protected List<ColumnValue[]> executeTotalQuery(DetailedReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        return null;
//        return dsService.executeWithAuth(jdbcOperations, () ->
//                jdbcOperations.query(
//                        "select * from report.ersatz_custom_report_total(?::date, ?::date, ?::int, ?::int, ?::boolean, " +
//                                                                                   "?::boolean, ?::boolean, ?::boolean, ?::boolean, " +
//                                                                                   "?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::boolean)",
//                        new Object[]{
//                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
//                                Timestamp.valueOf(parameters.getDateEnd())},
//                        rowMapper
//                ));
    }
}
