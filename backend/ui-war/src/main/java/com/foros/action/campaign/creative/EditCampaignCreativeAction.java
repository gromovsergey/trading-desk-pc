package com.foros.action.campaign.creative;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.action.campaign.campaignGroup.CampaignGroupBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.campaign.CampaignCreative;

import java.security.AccessControlException;

public class EditCampaignCreativeAction extends EditSaveCampaignCreativeActionSupport implements BreadcrumbsSupport {

    private Long id;

    private Breadcrumbs breadcrumbs = new Breadcrumbs();

    @ReadOnly
    public String create() {
        campaignCreative = new CampaignCreative();
        breadcrumbs
                .add(new CampaignBreadcrumbsElement(getExistingGroup().getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(getExistingGroup()))
                .add("campaign.breadcrumbs.group.createCreativeLink");
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        campaignCreative = campaignCreativeService.view(id);
        existingGroup = campaignCreative.getCreativeGroup();

        breadcrumbs
                .add(new CampaignBreadcrumbsElement(getExistingGroup().getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(getExistingGroup()))
                .add(new CreativeLinkBreadcrumbsElement(getModel()))
                .add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
