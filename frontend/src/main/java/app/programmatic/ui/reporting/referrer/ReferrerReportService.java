package app.programmatic.ui.reporting.referrer;

import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.validation.ValidateReportParameters;
import app.programmatic.ui.reporting.view.ReportMeta;

public interface ReferrerReportService {
    byte[] runReportReferrer(@ValidateReportParameters ReferrerReportParameters parameters, ReportFormat format);

    ReportMeta getReportMeta(ReferrerReportParameters parameters);
}
