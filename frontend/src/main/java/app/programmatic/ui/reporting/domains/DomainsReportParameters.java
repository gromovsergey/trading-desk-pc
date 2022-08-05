package app.programmatic.ui.reporting.domains;

import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportParameters;

public class DomainsReportParameters extends ReportParameters {

    @Override
    public Report getReport() {
        return Report.DOMAINS;
    }
}
