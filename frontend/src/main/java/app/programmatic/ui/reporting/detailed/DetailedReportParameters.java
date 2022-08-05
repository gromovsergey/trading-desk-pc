package app.programmatic.ui.reporting.detailed;

import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportParameters;


public class DetailedReportParameters extends ReportParameters {

    private Long advertiserAccountId;
    private Long publisherAccountId;

    @Override
    public Report getReport() {
        return Report.DETAILED;
    }

    public Long getAdvertiserAccountId() {
        return advertiserAccountId;
    }

    public void setAdvertiserAccountId(Long advertiserAccountId) {
        this.advertiserAccountId = advertiserAccountId;
    }

    public Long getPublisherAccountId() {
        return publisherAccountId;
    }

    public void setPublisherAccountId(Long publisherAccountId) {
        this.publisherAccountId = publisherAccountId;
    }

    @Override
    public String toString()
    {
        return super.toString() +
                "\nInner Report Name = " + getReport() +
                "\nPublisherAccountId = " + getPublisherAccountId() +
                "\nAdvertiserAccountId = " + getAdvertiserAccountId();
    }
}
