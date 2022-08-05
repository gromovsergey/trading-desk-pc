package com.foros.action.reporting.advertiser;

import com.foros.session.reporting.advertiser.olap.OlapDisplayAdvertiserReportService;

import javax.ejb.EJB;

public class RunOlapDisplayAdvertiserReportAction extends RunOlapAdvertiserReportActionBase {
    @EJB
    private OlapDisplayAdvertiserReportService reportService;

    @Override
    protected OlapDisplayAdvertiserReportService getService() {
        return reportService;
    }

    @Override
    protected String getFileName() {
        return "DisplayAdvertising";
    }
}
