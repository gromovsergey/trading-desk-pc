package app.programmatic.ui.agentreport.dao.model;

import java.util.List;

public class MonthlyStats {
    private AgentReportStatus status;
    private List<MonthlyStat> stats;
    private MonthlyStat total;

    public MonthlyStats(AgentReportStatus status, List<MonthlyStat> stats, MonthlyStat total) {
        this.status = status;
        this.stats = stats;
        this.total = total;
    }

    public AgentReportStatus getStatus() {
        return status;
    }

    public void setStatus(AgentReportStatus status) {
        this.status = status;
    }

    public List<MonthlyStat> getStats() {
        return stats;
    }

    public void setStats(List<MonthlyStat> stats) {
        this.stats = stats;
    }

    public MonthlyStat getTotal() {
        return total;
    }

    public void setTotal(MonthlyStat total) {
        this.total = total;
    }
}
