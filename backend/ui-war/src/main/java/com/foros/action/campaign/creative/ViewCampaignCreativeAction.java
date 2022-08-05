package com.foros.action.campaign.creative;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.action.campaign.campaignGroup.CampaignGroupBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.CurrentUserService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

public class ViewCampaignCreativeAction extends CampaignCreativeActionSupport implements RequestContextsAware, BreadcrumbsSupport {

    private Long id;
    private boolean showNewMessage;

    @EJB
    private CurrentUserService userService;

    @ReadOnly
    public String view() {
        campaignCreative = campaignCreativeService.view(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isShowNewMessage() {
        return showNewMessage;
    }

    public void setShowNewMessage(boolean showNewMessage) {
        this.showNewMessage = showNewMessage;
    }

    public CampaignCreativeGroup getExistingGroup() {
        return campaignCreative.getCreativeGroup();
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getAdvertiserContext().switchTo(campaignCreative.getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new CampaignBreadcrumbsElement(getModel().getCreativeGroup().getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(getModel().getCreativeGroup()))
                .add(new CreativeLinkBreadcrumbsElement(getModel()));
    }

    @Override
    public boolean canUpdateWeight() {
        CCGType ccgType = campaignCreative.getCreativeGroup().getCcgType();
        return userService.isInternal() || CCGType.TEXT != ccgType;
    }
}
