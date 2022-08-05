package app.programmatic.ui.reporting.advertiser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.reporting.model.ReportColumn;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;
import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.service.GenericReportServiceImpl;
import app.programmatic.ui.reporting.tools.ReportColumnFormatter;

import java.sql.Array;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

@Service
@Validated
@Slf4j
public class AdvertiserReportServiceImpl extends GenericReportServiceImpl<AdvertiserReportParameters> implements AdvertiserReportService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private DataSourceService dsService;

    @Override
    @Restrict(restriction = "report.generalAdvertising", parameters = "parameters.accountId")
    public byte[] runReportAdvertiser(AdvertiserReportParameters parameters, ReportFormat format) {
        log.info("F: runReportNew; Report Name: " + "report.generalAdvertising" + parameters);
        return super.runReport(parameters, format);
    }

    @Override
    protected ReportColumnFormatter getReportColumnFormatter(AdvertiserReportParameters parameters) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(parameters.getAccountId());
        return new ReportColumnFormatter(
                // Issue #243: report is moved to hard-coded locale, please
                // return back when multi-language will be needed
                LOCALE_RU,
                account.getCurrencyCode(),
                account.getCurrencyAccuracy()
        );
    }

    @Override
    protected List<ColumnValue[]> executeQuery(AdvertiserReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        boolean includeDate = parameters.getSelectedColumns().contains(ReportColumn.DATE);
        boolean includeFlight = parameters.getSelectedColumns().contains(ReportColumn.FLIGHT);
        boolean includeLineItem = parameters.getSelectedColumns().contains(ReportColumn.LINE_ITEM);
        boolean includeCreative = parameters.getSelectedColumns().contains(ReportColumn.CREATIVE);
        boolean includeGeolocation = parameters.getSelectedColumns().contains(ReportColumn.GEOLOCATION);

        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.general_advertising_report_brief(?::int, ?::date, ?::date, ?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::int[])",
                        new Object[]{
                                parameters.getAccountId(),
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                includeDate,
                                Boolean.TRUE,
                                includeFlight,
                                includeLineItem,
                                includeCreative,
                                includeGeolocation,
                                getFlightIdsArray(parameters.getFlightIds())
                        },
                        rowMapper
                )
        );
    }

    @Override
    protected List<ColumnValue[]> executeTotalQuery(AdvertiserReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.general_advertising_report_brief_total(?::int, ?::date, ?::date, ?::int[])",
                        new Object[]{
                                parameters.getAccountId(),
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                getFlightIdsArray(parameters.getFlightIds())},
                        rowMapper
                ));
    }

    private Array getFlightIdsArray(List<Long> flightIds) {
        if (flightIds == null) {
            return null;
        }
        return jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", flightIds.toArray()));
    }
}
