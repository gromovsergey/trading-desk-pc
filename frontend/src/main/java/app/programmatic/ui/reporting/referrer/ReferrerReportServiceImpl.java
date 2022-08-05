package app.programmatic.ui.reporting.referrer;

import app.programmatic.ui.reporting.model.ReportColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import app.programmatic.ui.account.dao.model.PublisherAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;
import app.programmatic.ui.reporting.service.GenericReportServiceImpl;
import app.programmatic.ui.reporting.tools.ReportColumnFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;
import static app.programmatic.ui.reporting.model.ReportColumnType.CURRENCY_COLUMN;

@Service
@Validated
@Slf4j
public class ReferrerReportServiceImpl extends GenericReportServiceImpl<ReferrerReportParameters> implements ReferrerReportService {
    private static final Logger logger = Logger.getLogger(ReferrerReportServiceImpl.class.getName());

    private static final int STORED_CURRENCY_ACCURACY = 8;
    private static final int SHOW_CURRENCY_ACCURACY = 2;

    @Autowired
    private AccountService accountService;

    @Autowired
    @Qualifier("bigDataOperations")
    private JdbcOperations bigDataJdbcOperations;

    @Override
    @Restrict(restriction = "report.referrer", parameters = "parameters.accountId")
    public byte[] runReportReferrer(ReferrerReportParameters parameters, ReportFormat format) {
        log.info("F: runReportNew; Report Name: " + "report.referrer" + parameters);
        return super.runReport(parameters, format);
    }

    @Override
    protected ReportColumnFormatter getReportColumnFormatter(ReferrerReportParameters parameters) {
        PublisherAccount account = accountService.findPublisherUnchecked(parameters.getAccountId());
        return new ReportColumnFormatter(
                // Issue #243: report is moved to hard-coded locale, please
                // return back when multi-language will be needed
                LOCALE_RU,
                account.getCurrencyCode(),
                account.getCurrencyAccuracy()
        );
    }

    protected List<ColumnValue[]> executeQuery(ReferrerReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        return bigDataJdbcOperations.query(
                "select " +
                        "    domain, " +
                        "    ext_tag_id, " +
                        "    requestsum as requests, " +
                        "    opted_in_requests, " +
                        "    (bids_won_count + bids_lost_count) as bids, " +
                        "    imps, " +
                        "    clicks, " +
                        "    if (opted_in_requests == 0, 0, (i_floor_won_cost + i_floor_lost_cost + i_floor_no_bid_cost )* 1000 / opted_in_requests) as floor, " +
                        "    if (requestsum == 0, 0, (a_floor_won_cost + a_floor_lost_cost + a_floor_no_bid_cost) * 1000 / requestsum) as floor_all, " +
                        "    if (imps == 0, 0, a_floor_won_cost*1000/imps) as floor_win, " +
                        "    if ((bids_won_count + bids_lost_count - imps) == 0, 0, a_floor_lost_cost*1000/(bids_won_count + bids_lost_count - imps)) as floor_lost, " +
                        "    if (imps == 0, 0, a_cost) as spent, " +
                        "    if (imps == 0, 0, bid_won_amount*1000/imps) as bids_win, " +
                        "    if ((bids_won_count + bids_lost_count - imps) == 0, 0, bid_lost_amount*1000/(bids_won_count + bids_lost_count - imps)) as bids_lost " +
                        "from (" +
                        "    select " +
                        "        domain(url) as domain, " +
                        "        ext_tag_id, " +
                        "        sum(requests) as requestsum, " +
                        "        sum(if (user_status == 'I', requests, 0)) as opted_in_requests, " +
                        "        sum(if (user_status == 'I', floor_won_cost, 0)) as i_floor_won_cost, " +
                        "        sum(if (user_status == 'I', floor_lost_cost, 0)) as i_floor_lost_cost, " +
                        "        sum(if (user_status == 'I', floor_no_bid_cost, 0)) as i_floor_no_bid_cost, " +
                        "        sum(imps) as imps, " +
                        "        sum(clicks) as clicks, " +
                        "        sum(bids_won_count) as bids_won_count, " +
                        "        sum(bids_lost_count) as bids_lost_count, " +
                        "        sum(floor_won_cost) as a_floor_won_cost, " +
                        "        sum(floor_lost_cost) as a_floor_lost_cost, " +
                        "        sum(floor_no_bid_cost) as a_floor_no_bid_cost, " +
                        "        sum(cost) as a_cost, " +
                        "        sum(bid_won_amount) as bid_won_amount, " +
                        "        sum(bid_lost_amount) as bid_lost_amount " +
                        "    from site_referrer " +
                        "    where " +
                        "        has(?, tag_id) = 1 " +
                        "        and sdate >= toDate(?) " +
                        "        and sdate <= toDate(?) " +
                        "    group by domain(url), ext_tag_id) " +
                        "order by requests desc",
                new Object[]{
                        parameters.getTagIds(),
                        Timestamp.valueOf(getRestrictedDayStart(parameters)),
                        Timestamp.valueOf(parameters.getDateEnd())
                },
                rowMapper
        );
    }

    @Override
    protected List<ColumnValue[]> executeTotalQuery(ReferrerReportParameters parameters, RowMapper<ColumnValue[]> rowMapper) {
        return bigDataJdbcOperations.query(
                "select " +
                        "    domain,  " +
                        "    ext_tag_id, " +
                        "    requestsum as requests, " +
                        "    opted_in_requests, " +
                        "    (bids_won_count + bids_lost_count) as bids, " +
                        "    clicks, " +
                        "    imps, " +
                        "    if (opted_in_requests == 0, 0, ( i_floor_won_cost + i_floor_lost_cost + i_floor_no_bid_cost) * 1000 / opted_in_requests) as floor, " +
                        "    if (requestsum == 0, 0, (a_floor_won_cost + a_floor_lost_cost + a_floor_no_bid_cost) * 1000 / requestsum) as floor_all, " +
                        "    if (imps == 0, 0, a_floor_won_cost*1000/imps) as floor_win, " +
                        "    if (bids_won_count + bids_lost_count - imps == 0, 0, a_floor_lost_cost*1000/(bids_won_count + bids_lost_count - imps)) as floor_lost, " +
                        "    if (imps == 0, 0, a_cost) as spent, " +
                        "    if (imps == 0, 0, bid_won_amount*1000/imps) as bids_win, " +
                        "    if (bids_won_count + bids_lost_count - imps == 0, 0, bid_lost_amount*1000/(bids_won_count + bids_lost_count - imps)) as bids_lost " +
                        "from (" +
                        "    select " +
                        "        '' as domain, " +
                        "        '' as ext_tag_id, " +
                        "        sum(requests) as requestsum, " +
                        "        sum(if (user_status == 'I', requests, 0)) as opted_in_requests, " +
                        "        sum(if (user_status == 'I', floor_won_cost, 0)) as i_floor_won_cost, " +
                        "        sum(if (user_status == 'I', floor_lost_cost, 0)) as i_floor_lost_cost, " +
                        "        sum(if (user_status == 'I', floor_no_bid_cost, 0)) as i_floor_no_bid_cost, " +
                        "        sum(imps) as imps, " +
                        "        sum(clicks) as clicks, " +
                        "        sum(bids_won_count) as bids_won_count, " +
                        "        sum(bids_lost_count) as bids_lost_count, " +
                        "        sum(floor_won_cost) as a_floor_won_cost, " +
                        "        sum(floor_lost_cost) as a_floor_lost_cost, " +
                        "        sum(floor_no_bid_cost) as a_floor_no_bid_cost, " +
                        "        sum(cost) as a_cost, " +
                        "        sum(bid_won_amount) as bid_won_amount, " +
                        "        sum(bid_lost_amount) as bid_lost_amount" +
                        "    from site_referrer " +
                        "    where" +
                        "        has(?, tag_id) = 1 " +
                        "        and sdate >= toDate(?) " +
                        "        and sdate <= toDate(?) " +
                        "    group by domain, ext_tag_id) ",
                new Object[]{
                        parameters.getTagIds(),
                        Timestamp.valueOf(getRestrictedDayStart(parameters)),
                        Timestamp.valueOf(parameters.getDateEnd())
                },
                rowMapper
        );
    }

    @Override
    protected ColumnValue readColumnValue(ResultSet rs, ReportColumn column) throws SQLException {
        if (column.getColumnType() == CURRENCY_COLUMN) {
            BigDecimal result = rs.getBigDecimal(column.getColumnName());
            result = !rs.wasNull() ? result : null;

            if (result != null) {
                result = result.movePointLeft(STORED_CURRENCY_ACCURACY);
                result = result.setScale(SHOW_CURRENCY_ACCURACY, RoundingMode.HALF_UP);
            }

            boolean isZero = result != null && ((BigDecimal) CURRENCY_COLUMN.getZeroValue()).compareTo(result) == 0;

            return new ColumnValue(result, column, isZero);
        }

        return super.readColumnValue(rs, column);
    }
}
