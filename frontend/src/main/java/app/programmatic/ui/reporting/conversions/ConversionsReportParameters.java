package app.programmatic.ui.reporting.conversions;

import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportParameters;

import java.util.List;

public class ConversionsReportParameters extends ReportParameters {
    private List<Long> flightIds;
    private List<Long> lineItemIds;
    private List<Long> conversionIds;

    @Override
    public Report getReport() {
        return Report.CONVERSIONS;
    }

    public List<Long> getFlightIds() {
        return flightIds;
    }

    public void setFlightIds(List<Long> flightIds) {
        this.flightIds = flightIds;
    }

    public List<Long> getLineItemIds() {
        return lineItemIds;
    }

    public void setLineItemIds(List<Long> lineItemIds) {
        this.lineItemIds = lineItemIds;
    }

    public List<Long> getConversionIds() {
        return conversionIds;
    }

    public void setConversionIds(List<Long> conversionIds) {
        this.conversionIds = conversionIds;
    }


    @Override
    public String toString()
    {
        return super.toString() +
                "\nInner Report Name = " + getReport() +
                "\n FlightIds = " + getFlightIds() +
                "\n LineItemIds = " + getLineItemIds() +
                "\n ConversionIds = " + getConversionIds();
    }
}
