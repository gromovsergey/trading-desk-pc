package com.foros.action.reporting.advertiser;

import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;
import com.foros.session.reporting.advertiser.olap.OlapTextAdvertiserReportService;

import java.util.Collection;
import javax.ejb.EJB;

public class ViewOlapTextAdvertiserReportAction extends ViewOlapAdvertiserReportActionBase {
    @EJB
    private OlapTextAdvertiserReportService reportService;

    public OlapAdvertiserReportService getService() {
        return reportService;
    }

    @Override
    protected OlapDetailLevel getGroupLevel() {
        return OlapDetailLevel.AdGroup;
    }

    @Override
    protected Collection<OlapDetailLevel> getAllDetailLevels() {
        return OlapDetailLevel.TEXT_DETAIL_LEVELS;
    }
}
