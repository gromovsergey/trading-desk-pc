package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.service.ReportService;

public interface ForosReportService {
    ReportService getReportService();

    ReportService getAdminReportService();
}
