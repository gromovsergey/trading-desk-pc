package com.foros.session.reporting.dashboard;

import com.foros.session.reporting.parameters.DatedReportParameters;

public class DashboardParameters extends DatedReportParameters {

    private boolean withActivityOnly;

    public boolean isWithActivityOnly() {
        return withActivityOnly;
    }

    public void setWithActivityOnly(boolean withActivityOnly) {
        this.withActivityOnly = withActivityOnly;
    }

    public boolean isShowZeroStat() {
        return !isWithActivityOnly();
    }

    public void setShowZeroStat(boolean showZeroStat ) {
        setWithActivityOnly(!showZeroStat);
    }


}
