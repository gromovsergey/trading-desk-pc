package app.programmatic.ui.reporting.conversions;

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
import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;
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
public class ConversionsReportServiceImpl extends GenericReportServiceImpl<ConversionsReportParameters> implements ConversionsReportService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private DataSourceService dsService;

    @Override
    @Restrict(restriction = "report.conversions", parameters = "parameters.accountId")
    public byte[] runReportConversions(ConversionsReportParameters parameters, ReportFormat format) {
        log.info("F: runReportNew; Report Name: " + "report.conversions" + parameters);
        return super.runReport(parameters, format);
    }

    @Override
    protected ReportColumnFormatter getReportColumnFormatter(ConversionsReportParameters parameters) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(parameters.getAccountId());
        return new ReportColumnFormatter(
                LOCALE_RU,
                account.getCurrencyCode(),
                account.getCurrencyAccuracy()
        );
    }

    @Override
    protected List<ColumnValue[]> executeQuery(ConversionsReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        boolean includeDate = parameters.getSelectedColumns().contains(ReportColumn.DATE);
        boolean includeFlight = parameters.getSelectedColumns().contains(ReportColumn.FLIGHT);
        boolean includeLineItem = parameters.getSelectedColumns().contains(ReportColumn.LINE_ITEM);
        boolean includeChannel = parameters.getSelectedColumns().contains(ReportColumn.CHANNEL);
        boolean includeCreative = parameters.getSelectedColumns().contains(ReportColumn.CREATIVE);
        boolean includeConversion = parameters.getSelectedColumns().contains(ReportColumn.CONVERSION);
        boolean includeOrderId = parameters.getSelectedColumns().contains(ReportColumn.ORDER_ID);
        boolean includeSSP = parameters.getSelectedColumns().contains(ReportColumn.SSP);

        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.general_conversions_report(?::int, ?::date, ?::date, ?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::boolean, ?::int[], ?::int[], ?::int[])",
                        new Object[]{
                                parameters.getAccountId(),
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                includeDate,
                                includeFlight,
                                includeLineItem,
                                includeCreative,
                                includeChannel,
                                includeConversion,
                                includeOrderId,
                                includeSSP,
                                getIdsArray(parameters.getFlightIds()),
                                getIdsArray(parameters.getLineItemIds()),
                                getIdsArray(parameters.getConversionIds())
                        },
                        rowMapper
                )
        );
    }

    private Array getIdsArray(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ids.toArray()));
    }

    @Override
    protected List<ColumnValue[]> executeTotalQuery(ConversionsReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.general_conversions_report_total(?::int, ?::date, ?::date, ?::int[], ?::int[], ?::int[])",
                        new Object[]{
                                parameters.getAccountId(),
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                getIdsArray(parameters.getFlightIds()),
                                getIdsArray(parameters.getLineItemIds()),
                                getIdsArray(parameters.getConversionIds())
                        },
                        rowMapper
                ));
    }
}
