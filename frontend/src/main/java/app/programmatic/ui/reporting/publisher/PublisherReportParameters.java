package app.programmatic.ui.reporting.publisher;

import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportParameters;

public class PublisherReportParameters extends ReportParameters {

    @Override
    public Report getReport() {
        return Report.PUBLISHER;
    }

    @Override
    public String toString()
    {
        return super.toString() + "\nInner Report Name = " + getReport();
    }
}
