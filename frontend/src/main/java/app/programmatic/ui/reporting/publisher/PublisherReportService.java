package app.programmatic.ui.reporting.publisher;

import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.validation.ValidateReportParameters;
import app.programmatic.ui.reporting.view.ReportMeta;

public interface PublisherReportService {
    byte[] runReportNew(@ValidateReportParameters PublisherReportParameters parameters, ReportFormat format);

    ReportMeta getReportMeta(PublisherReportParameters parameters);
}
