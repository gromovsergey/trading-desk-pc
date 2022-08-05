package app.programmatic.ui.reporting.segments;

import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.validation.ValidateReportParameters;
import app.programmatic.ui.reporting.view.ReportMeta;

public interface SegmentsReportService {

    byte[] runReportSegments(@ValidateReportParameters SegmentsReportParameters parameters, ReportFormat format);

    ReportMeta getReportMeta(SegmentsReportParameters parameters);
}
