package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class UserSampleGroupsSupportAction extends BaseActionSupport implements ModelDriven<CampaignCreativeGroup>, RequestContextsAware, BreadcrumbsSupport {

    protected CampaignCreativeGroup group;

    @EJB
    protected CampaignCreativeGroupService groupService; 

    @EJB
    protected AccountService accountService;

    public UserSampleGroupsSupportAction() {
        group = new CampaignCreativeGroup();
    }
    
    @Override
    public CampaignCreativeGroup getModel() {
        return group;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if(group.getAccount() == null) {
            group = groupService.view(group.getId());
        }
        contexts.getAdvertiserContext().switchTo(group.getAccount().getId());
    }

    public CampaignCreativeGroup getGroup() {
        return group;
    }

    public void setGroup(CampaignCreativeGroup group) {
        this.group = group;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new CampaignBreadcrumbsElement(group.getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(group))
                .add("channel.geoTarget.edit");
    }
}
