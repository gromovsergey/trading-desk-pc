package com.foros.action.reporting.waterfall;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.action.campaign.campaignGroup.CampaignGroupBreadcrumbsElement;
import com.foros.action.reporting.CancellablePageReportingAction;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

public class CancellableSelectionFailuresReportAction extends CancellablePageReportingAction implements BreadcrumbsSupport, RequestContextsAware {

    @EJB
    private CampaignCreativeGroupService groupService;

    private Long ccgId;

    @Override
    public Breadcrumbs getBreadcrumbs() {
        CampaignCreativeGroup group = groupService.find(ccgId);
        return new Breadcrumbs().add(new CampaignBreadcrumbsElement(group.getCampaign())).add(new CampaignGroupBreadcrumbsElement(group)).add("reports.selectionFailuresReport");
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(groupService.find(ccgId).getAccount());
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }
}
