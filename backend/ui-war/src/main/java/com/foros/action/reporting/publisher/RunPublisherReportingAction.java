package com.foros.action.reporting.publisher;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.publisher.PublisherReportParameters;
import com.foros.session.reporting.publisher.PublisherReportService;

import javax.ejb.EJB;

public class RunPublisherReportingAction extends RunReportingActionSupport<PublisherReportParameters> {
    @EJB
    private PublisherReportService reportService;

    @Override
    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportService, "publisher");
    }
}
