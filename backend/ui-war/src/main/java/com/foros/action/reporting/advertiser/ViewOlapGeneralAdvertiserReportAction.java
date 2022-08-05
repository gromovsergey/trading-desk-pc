package com.foros.action.reporting.advertiser;

import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;
import com.foros.session.reporting.advertiser.olap.OlapGeneralAdvertiserReportService;

import java.util.Collection;
import javax.ejb.EJB;

public class ViewOlapGeneralAdvertiserReportAction extends ViewOlapAdvertiserReportActionBase {
    @EJB
    private OlapGeneralAdvertiserReportService reportService;

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

