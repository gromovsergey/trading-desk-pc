package com.foros.action.reporting.dashboard;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.site.Site;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.reporting.dashboard.PublisherDashboardService;
import com.foros.session.reporting.dashboard.SiteDashboardParameters;
import com.foros.session.reporting.dashboard.TagDashboardTO;
import com.foros.session.site.SiteService;
import com.foros.util.context.RequestContexts;

import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class TagDashboardAction extends BaseActionSupport implements RequestContextsAware, ModelDriven<SiteDashboardParameters> {

    @EJB
    private SiteService siteService;

    @EJB
    private PublisherDashboardService dashboardService;

    private SiteDashboardParameters parameters = new SiteDashboardParameters();

    private List<TagDashboardTO> result;

    private Site site;

    private boolean showCreditedImps;

    @Override
    @ReadOnly
    @Restrict(restriction = "PublisherEntity.view", parameters = "#target.getSite()")
    public String execute() {
        result = dashboardService.generateTagDashboard(parameters);
        return SUCCESS;
    }

    public Site getSite() {
        if (site == null) {
            site = siteService.find(parameters.getSiteId());
        }
        return site;
    }

    public boolean isShowCreditedImps() {
        return showCreditedImps;
    }

    public void setShowCreditedImps(boolean showCreditedImps) {
        this.showCreditedImps = showCreditedImps;
    }

    @Override
    public SiteDashboardParameters getModel() {
        return parameters;
    }

    public List<TagDashboardTO> getResult() {
        return result;
    }

    public boolean isClicksDataAvailable(){
        return isInternal() || getSite().getAccount().getAccountType().isClicksDataVisibleToExternal();
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(getSite().getAccount());
    }
}

