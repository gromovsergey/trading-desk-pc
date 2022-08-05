package app.programmatic.ui.reporting.conversions;

import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.validation.ValidateReportParameters;
import app.programmatic.ui.reporting.view.ReportMeta;

public interface ConversionsReportService {

    byte[] runReportConversions(@ValidateReportParameters ConversionsReportParameters parameters, ReportFormat format);

    ReportMeta getReportMeta(ConversionsReportParameters parameters);
}
