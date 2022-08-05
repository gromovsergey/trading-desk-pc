package app.programmatic.ui.reporting.segments;

import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.reporting.model.ReportColumn;
import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;
import app.programmatic.ui.reporting.service.GenericReportServiceImpl;
import app.programmatic.ui.reporting.tools.ReportColumnFormatter;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.sql.Array;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

@Service
@Validated
@Slf4j
public class SegmentsReportServiceImpl extends GenericReportServiceImpl<SegmentsReportParameters> implements SegmentsReportService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private DataSourceService dsService;

    @Override
    @Restrict(restriction = "report.segments", parameters = "parameters.accountId")
    public byte[] runReportSegments(SegmentsReportParameters parameters, ReportFormat format) {
        log.info("F: runReportNew; Report Name: " + "report.segments" + parameters);
        return super.runReport(parameters, format);
    }

    @Override
    protected ReportColumnFormatter getReportColumnFormatter(SegmentsReportParameters parameters) {
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
    protected List<ColumnValue[]> executeQuery(SegmentsReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        boolean includeDate = parameters.getSelectedColumns().contains(ReportColumn.DATE);
        boolean includeFlight = parameters.getSelectedColumns().contains(ReportColumn.FLIGHT);
        boolean includeLineItem = parameters.getSelectedColumns().contains(ReportColumn.LINE_ITEM);
        Array flightIds = CollectionUtils.isEmpty(parameters.getFlightIds()) ? null : getIdsArray(parameters.getFlightIds());
        Array lineItemIds = CollectionUtils.isEmpty(parameters.getLineItemIds()) ? null : getIdsArray(parameters.getLineItemIds());

        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.ersatz_channel_usage_report_large(?::date, ?::date, ?::int[], ?::int[], " +
                                                                                   "?::boolean, ?::boolean, ?::boolean)",
                        new Object[]{
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                flightIds,
                                lineItemIds,
                                includeDate,
                                includeFlight,
                                includeLineItem
                        },
                        rowMapper
                )
        );
    }

    @Override
    protected List<ColumnValue[]> executeTotalQuery(SegmentsReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        Array flightIds = CollectionUtils.isEmpty(parameters.getFlightIds()) ? null : getIdsArray(parameters.getFlightIds());
        Array lineItemIds = CollectionUtils.isEmpty(parameters.getLineItemIds()) ? null : getIdsArray(parameters.getLineItemIds());

        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.ersatz_channel_usage_report_large_total(?::date, ?::date, ?::int[], ?::int[])",
                        new Object[]{
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                flightIds,
                                lineItemIds
                        },
                        rowMapper
                ));
    }

    private Array getIdsArray(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ids.toArray()));
    }
}
