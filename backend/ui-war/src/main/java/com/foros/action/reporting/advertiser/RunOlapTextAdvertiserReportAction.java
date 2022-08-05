package com.foros.action.reporting.advertiser;

import com.foros.session.reporting.advertiser.olap.OlapTextAdvertiserReportService;

import javax.ejb.EJB;

public class RunOlapTextAdvertiserReportAction extends RunOlapAdvertiserReportActionBase {
    @EJB
    private OlapTextAdvertiserReportService reportService;

    @Override
    protected OlapTextAdvertiserReportService getService() {
        return reportService;
    }

    @Override
    protected String getFileName() {
        return "TextAdvertising";
    }
}
