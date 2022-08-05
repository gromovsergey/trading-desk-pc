package app.programmatic.ui.reporting.advertiser;

import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportParameters;

import java.util.List;
import java.util.Optional;

public class AdvertiserReportParameters extends ReportParameters {

    @Override
    public Report getReport() {
        return Report.ADVERTISER;
    }

    private List<Long> flightIds;

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
                "\nFlightIds = " + getFlightIds();
    }
}
