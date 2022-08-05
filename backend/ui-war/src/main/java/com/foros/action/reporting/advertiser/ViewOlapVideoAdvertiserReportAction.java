package com.foros.action.reporting.advertiser;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;
import com.foros.session.reporting.advertiser.olap.OlapDisplayAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapVideoAdvertiserReportService;

import javax.ejb.EJB;
import java.util.Collection;

public class ViewOlapVideoAdvertiserReportAction extends ViewOlapAdvertiserReportActionBase {
    @EJB
    private OlapVideoAdvertiserReportService reportService;

    public OlapVideoAdvertiserReportService getService() {
        return reportService;
    }

    @Override
    protected OlapDetailLevel getGroupLevel() {
        return OlapDetailLevel.CreativeGroup;
    }

    @Override
    protected Collection<OlapDetailLevel> getAllDetailLevels() {
        return OlapDetailLevel.VIDEO_DETAIL_LEVELS;
    }

    @ReadOnly
    @Restrict(restriction = "Report.runVideoAdvertisingReport", parameters = "#target.model.accountId")
    public String view() throws Exception {
        return super.view();
    }
}
