package app.programmatic.ui.agentreport.service;

import app.programmatic.ui.agentreport.dao.model.AgentReport;

public interface AgentReportDocumentService {
    byte[] generateDocument(AgentReport report);
}
