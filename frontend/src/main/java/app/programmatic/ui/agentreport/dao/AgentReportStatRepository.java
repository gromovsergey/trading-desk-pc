package app.programmatic.ui.agentreport.dao;

import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.agentreport.dao.model.AgentReportStat;

public interface AgentReportStatRepository extends CrudRepository<AgentReportStat, Long> {
}
