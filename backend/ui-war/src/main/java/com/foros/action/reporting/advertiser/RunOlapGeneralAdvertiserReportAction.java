package com.foros.action.reporting.advertiser;

import com.foros.session.reporting.advertiser.olap.OlapGeneralAdvertiserReportService;

import javax.ejb.EJB;

public class RunOlapGeneralAdvertiserReportAction extends RunOlapAdvertiserReportActionBase {
    @EJB
    private OlapGeneralAdvertiserReportService reportService;

    @Override
    protected OlapGeneralAdvertiserReportService getService() {
        return reportService;
    }

    @Override
    protected String getFileName() {
        return "GeneralAdvertising";
    }
}
