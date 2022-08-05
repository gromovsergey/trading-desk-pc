package app.programmatic.ui.reporting.detailed;

import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.validation.ValidateReportParameters;
import app.programmatic.ui.reporting.view.ReportMeta;

public interface DetailedReportService {

    byte[] runReportDetailed(@ValidateReportParameters DetailedReportParameters parameters, ReportFormat format);

    ReportMeta getReportMeta(DetailedReportParameters parameters);
}
