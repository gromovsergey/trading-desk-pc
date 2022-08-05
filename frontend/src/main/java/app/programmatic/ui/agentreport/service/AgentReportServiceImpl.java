package app.programmatic.ui.agentreport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.common.model.RateType;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.agentreport.dao.AgentReportStatRepository;
import app.programmatic.ui.agentreport.dao.model.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;

@Service
@Validated
public class AgentReportServiceImpl implements AgentReportService {

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    private AgentReportStatRepository agentReportStatRepository;

    @Value("#{new java.lang.Integer((\"${agentReport.startDate}\").split(\"/\")[0])}")
    private Integer startMonth;

    @Value("#{new java.lang.Integer((\"${agentReport.startDate}\").split(\"/\")[1])}")
    private Integer startYear;

    @Override
    @Restrict(restriction = "agentReport.view")
    public List<TotalStat> getTotalStats() {
        return jdbcOperations.query("select * from report.agent_report(?::int, ?::int)",
                new Object[]{startYear, startMonth},
                (ResultSet rs, int index) -> {
                    TotalStat stat = new TotalStat();
                    stat.setMonth(rs.getDate("fin_sdate").toLocalDate().getMonthValue());
                    stat.setYear(rs.getDate("fin_sdate").toLocalDate().getYear());
                    stat.setInvoiceAmount(rs.getBigDecimal("invoiced_amount"));
                    stat.setPublisherAmount(rs.getBigDecimal("pub_amount"));
                    stat.setAgencyAmount(rs.getBigDecimal("agent_amount"));
                    stat.setPrincipalAmount(rs.getBigDecimal("principal_amount"));
                    stat.setStatus(AgentReportStatus.valueOfLetter(rs.getString("status")));
                    return stat;
                });
    }

    @Override
    @Restrict(restriction = "agentReport.view")
    public List<MonthlyStat> getMonthlyStats(Integer year, Integer month) {
        return jdbcOperations.query("select * from report.agent_report_monthly(?::int, ?::int)",
                new Object[] { year, month },
                (ResultSet rs, int index) -> {
                    MonthlyStat stat = new MonthlyStat();
                    Long statId = rs.getLong("stat_id");
                    stat.setId(rs.wasNull() ? null : statId);
                    stat.setCampaignId(rs.getLong("campaign_id"));
                    stat.setVersion(XmlDateTimeConverter.convertToEpochTime(rs.getTimestamp("version")));

                    stat.setAdvertiserName(rs.getString("advertiser_name"));
                    stat.setContractNumber(rs.getString("contract_number"));
                    stat.setClientName(rs.getString("client_name"));
                    stat.setCampaignName(rs.getString("campaign_name"));
                    stat.setInvoiceNumber(rs.getString("invoice_number"));
                    stat.setStatus(AgentReportStatus.valueOfLetter(rs.getString("status")));
                    stat.setRateType(RateType.valueOf(rs.getString("rate_type")));
                    stat.setRateValue(rs.getBigDecimal("rate_value"));

                    stat.setInventoryAmount(rs.getLong("inventory"));
                    Long inventoryAmountConfirmed = rs.getLong("inventory_confirmed");
                    stat.setInventoryAmountConfirmed(rs.wasNull() ? null : inventoryAmountConfirmed);
                    stat.setInventoryAmountComment(rs.getString("inventory_comment"));

                    stat.setTotalAmount(rs.getBigDecimal("total_amount"));
                    stat.setTotalAmountConfirmed(rs.getBigDecimal("total_amount_confirmed"));

                    stat.setPubAmount(rs.getBigDecimal("pub_amount"));
                    stat.setPubAmountConfirmed(rs.getBigDecimal("pub_amount_confirmed"));
                    stat.setPubAmountComment(rs.getString("pub_amount_comment"));

                    stat.setPrepaymentAmount(BigDecimal.ZERO);
                    stat.setTotalNetAmount(rs.getBigDecimal("total_net_amount"));

                    stat.setAgentAmount(rs.getBigDecimal("agent_amount"));
                    stat.setPrincipalAmount(rs.getBigDecimal("principal_amount"));

                    return stat;
                });
    }


    @Override
    @Restrict(restriction = "agentReport.edit")
    public void updateStats(List<AgentReportStat> stats) {
        agentReportStatRepository.saveAll(stats);
    }

    @Override
    @Restrict(restriction = "agentReport.view")
    public Long getContractAmount(Integer year, Integer month) {
        return jdbcOperations.queryForObject("select count(*) " +
                        "from account " +
                        "where " +
                        "  (role_id = ? or role_id = ? and agency_account_id is null) " +
                        "  and contract_date >= make_date(?, ?, 1) " +
                        "  and contract_date < (make_date(?, ?, 1) + interval '1 month')",
                new Object[]{
                        AccountRole.AGENCY.getId(),
                        AccountRole.ADVERTISER.getId(),
                        year,
                        month,
                        year,
                        month},
                Long.class);
    }
}
