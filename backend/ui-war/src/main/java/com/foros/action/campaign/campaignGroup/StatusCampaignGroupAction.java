package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.session.CurrentUserService;
import com.foros.session.campaign.CampaignCreativeGroupService;

import javax.ejb.EJB;

public class StatusCampaignGroupAction extends BaseActionSupport {
    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private CurrentUserService currentUserService;

    private Long id;
    private Long campaignId;
    private String declinationReason;

    public String activate() {
        campaignCreativeGroupService.activate(id);

        return SUCCESS;
    }

    public String inactivate() {
        campaignCreativeGroupService.inactivate(id);

        return SUCCESS;
    }

    public String delete() {
        if (!currentUserService.isInternal()) {
            campaignId = campaignCreativeGroupService.find(id).getCampaign().getId();
        }

        campaignCreativeGroupService.delete(id);

        return SUCCESS;
    }

    public String undelete() {
        campaignCreativeGroupService.undelete(id);

        return SUCCESS;
    }

    public String approve() {
        campaignCreativeGroupService.approve(id);

        return SUCCESS;
    }

    public String decline() {
        campaignCreativeGroupService.decline(id, declinationReason);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getDeclinationReason() {
        return declinationReason;
    }

    public void setDeclinationReason(String declinationReason) {
        this.declinationReason = declinationReason;
    }
}
