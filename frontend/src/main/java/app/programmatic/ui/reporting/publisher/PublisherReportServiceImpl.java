package app.programmatic.ui.reporting.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import app.programmatic.ui.account.dao.model.PublisherAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportColumn;
import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;
import app.programmatic.ui.reporting.service.GenericReportServiceImpl;
import app.programmatic.ui.reporting.tools.*;

import java.sql.Timestamp;
import java.util.List;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

@Service
@Validated
@Slf4j
public class PublisherReportServiceImpl extends GenericReportServiceImpl<PublisherReportParameters> implements PublisherReportService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private DataSourceService dsService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Override
    @Restrict(restriction = "report.publisher", parameters = "parameters.accountId")
    public byte[] runReportNew(PublisherReportParameters parameters, ReportFormat format) {
        log.info("F: runReportNew; Report Name: " + "report.publisher" + parameters);
        return super.runReport(parameters, format);
    }

    @Override
    protected ReportColumnFormatter getReportColumnFormatter(PublisherReportParameters parameters) {
        PublisherAccount account = accountService.findPublisherUnchecked(parameters.getAccountId());
        return new ReportColumnFormatter(
                // Issue #243: report is moved to hard-coded locale, please
                // return back when multi-language will be needed
                LOCALE_RU,
                account.getCurrencyCode(),
                account.getCurrencyAccuracy()
        );
    }

    @Override
    protected List<ColumnValue[]> executeQuery(PublisherReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        boolean includeDate = parameters.getSelectedColumns().contains(ReportColumn.DATE);
        boolean includeSite = parameters.getSelectedColumns().contains(ReportColumn.SITE);
        boolean includeTag = parameters.getSelectedColumns().contains(ReportColumn.TAG);

        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.spa_publisher_report(?::int, ?::date, ?::date, ?::boolean, ?::boolean, ?::boolean)",
                        new Object[]{
                                parameters.getAccountId(),
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd()),
                                includeDate,
                                includeSite,
                                includeTag
                        },
                        rowMapper
                )
        );
    }

    @Override
    protected List<ColumnValue[]> executeTotalQuery(PublisherReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        return dsService.executeWithAuth(jdbcOperations, () ->
                jdbcOperations.query(
                        "select * from report.spa_publisher_report_total(?::int, ?::date, ?::date)",
                        new Object[]{
                                parameters.getAccountId(),
                                Timestamp.valueOf(getRestrictedDayStart(parameters)),
                                Timestamp.valueOf(parameters.getDateEnd())
                        },
                        rowMapper
                ));
    }
}
