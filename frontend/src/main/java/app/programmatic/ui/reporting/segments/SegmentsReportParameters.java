package app.programmatic.ui.reporting.segments;

import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportParameters;

import java.util.List;


public class SegmentsReportParameters extends ReportParameters {

    private List<Long> lineItemIds;
    private List<Long> flightIds;

    @Override
    public Report getReport() {
        return Report.SEGMENTS;
    }

    public List<Long> getLineItemIds() {
        return lineItemIds;
    }

    public void setLineItemIds(List<Long> lineItemIds) {
        this.lineItemIds = lineItemIds;
    }

    public List<Long> getFlightIds() {
        return flightIds;
    }

    public void setFlightIds(List<Long> flightIds) {
        this.flightIds = flightIds;
    }

    @Override
    public String toString()
    {
        return super.toString() +
                "\nInner Report Name = " + getReport() +
                "\nLineItemIds = " + getLineItemIds() +
                "\nFlightIds = " + getFlightIds();
    }
}
