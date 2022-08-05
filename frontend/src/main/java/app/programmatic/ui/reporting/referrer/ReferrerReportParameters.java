package app.programmatic.ui.reporting.referrer;

import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportParameters;

import java.util.List;

public class ReferrerReportParameters extends ReportParameters {

    @Override
    public Report getReport() {
        return Report.REFERRER;
    }

    private List<Long> tagIds;

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    @Override
    public String toString()
    {
        return super.toString() +
                "\nInner Report Name = " + getReport() +
                "\ntagIds = " + getTagIds();
    }
}
