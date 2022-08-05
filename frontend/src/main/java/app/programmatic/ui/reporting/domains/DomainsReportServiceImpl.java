package app.programmatic.ui.reporting.domains;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.service.GenericReportServiceImpl;
import app.programmatic.ui.reporting.tools.*;
import app.programmatic.ui.reporting.model.ReportColumn;

import java.sql.Timestamp;
import java.util.List;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

@Service
@Validated
@Slf4j
public class DomainsReportServiceImpl extends GenericReportServiceImpl<DomainsReportParameters> implements DomainsReportService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private DataSourceService dsService;

    @Override
    @Restrict(restriction = "report.generalAdvertising", parameters = "parameters.accountId")
    public byte[] runReportDomains(DomainsReportParameters parameters, ReportFormat format) {
        log.info("F: runReportNew; Report Name: " + "report.generalAdvertising" + parameters);
        return super.runReport(parameters, format);
    }

    @Override
    protected ReportColumnFormatter getReportColumnFormatter(DomainsReportParameters parameters) {
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
    protected List<ColumnValue[]> executeQuery(DomainsReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        boolean includeFlight = parameters.getSelectedColumns().contains(ReportColumn.FLIGHT);
        boolean includeDomain = parameters.getSelectedColumns().contains(ReportColumn.DOMAIN);

        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.campaign_referrer_stats_report(?::int, ?::date, ?::date, ?::boolean, ?::boolean)",
                        new Object[]{
                                parameters.getAccountId(),
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                includeFlight,
                                includeDomain
                        },
                        rowMapper
                )
        );
    }
}
