package com.foros.action.reporting.advertiser;

import com.foros.session.reporting.advertiser.olap.OlapDisplayAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import java.util.Collection;
import javax.ejb.EJB;

public class ViewOlapDisplayAdvertiserReportAction extends ViewOlapAdvertiserReportActionBase {
    @EJB
    private OlapDisplayAdvertiserReportService reportService;

    public OlapAdvertiserReportService getService() {
        return reportService;
    }

    @Override
    protected OlapDetailLevel getGroupLevel() {
        return OlapDetailLevel.CreativeGroup;
    }

    @Override
    protected Collection<OlapDetailLevel> getAllDetailLevels() {
        return OlapDetailLevel.DISPLAY_DETAIL_LEVELS;
    }
}
