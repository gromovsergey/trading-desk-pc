package app.programmatic.ui.reporting.advertiser;

import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.validation.ValidateReportParameters;
import app.programmatic.ui.reporting.view.ReportMeta;

public interface AdvertiserReportService {

    byte[] runReportAdvertiser(@ValidateReportParameters AdvertiserReportParameters parameters, ReportFormat format);

    ReportMeta getReportMeta(AdvertiserReportParameters parameters);
}
