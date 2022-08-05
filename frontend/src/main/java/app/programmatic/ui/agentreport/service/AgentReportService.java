package app.programmatic.ui.agentreport.service;

import app.programmatic.ui.agentreport.dao.model.AgentReportStat;
import app.programmatic.ui.agentreport.dao.model.MonthlyStat;
import app.programmatic.ui.agentreport.dao.model.TotalStat;
import app.programmatic.ui.agentreport.validation.ValidateStats;

import java.util.List;

public interface AgentReportService {
    List<TotalStat> getTotalStats();

    List<MonthlyStat> getMonthlyStats(Integer year, Integer month);

    void updateStats(@ValidateStats List<AgentReportStat> stats);

    Long getContractAmount(Integer year, Integer month);
}
