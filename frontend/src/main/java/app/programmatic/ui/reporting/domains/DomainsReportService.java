package app.programmatic.ui.reporting.domains;

import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.validation.ValidateReportParameters;
import app.programmatic.ui.reporting.view.ReportMeta;

public interface DomainsReportService {
    byte[] runReportDomains(@ValidateReportParameters DomainsReportParameters parameters, ReportFormat format);

    ReportMeta getReportMeta(DomainsReportParameters parameters);
}
