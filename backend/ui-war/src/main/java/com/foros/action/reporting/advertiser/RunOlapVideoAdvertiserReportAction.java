package com.foros.action.reporting.advertiser;

import com.foros.session.reporting.advertiser.olap.OlapVideoAdvertiserReportService;

import javax.ejb.EJB;

public class RunOlapVideoAdvertiserReportAction extends RunOlapAdvertiserReportActionBase {
    @EJB
    private OlapVideoAdvertiserReportService reportService;

    @Override
    protected OlapVideoAdvertiserReportService getService() {
        return reportService;
    }

    @Override
    protected String getFileName() {
        return "VideoAdvertising";
    }
}
