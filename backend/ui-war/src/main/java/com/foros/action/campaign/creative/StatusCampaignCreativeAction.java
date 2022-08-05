package com.foros.action.campaign.creative;

import com.foros.action.BaseActionSupport;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.security.principal.SecurityContext;
import com.foros.session.campaign.CampaignCreativeService;

import javax.ejb.EJB;

public class StatusCampaignCreativeAction extends BaseActionSupport {
    @EJB
    private CampaignCreativeService campaignCreativeService;

    private Long id;
    private CampaignCreativeGroup creativeGroup = new CampaignCreativeGroup();

    public String activate() {
        campaignCreativeService.activate(id);
        return SUCCESS;
    }

    public String inactivate() {
        campaignCreativeService.inactivate(id);
        return SUCCESS;
    }

    public String delete() {
        campaignCreativeService.delete(id);

        if (!SecurityContext.isInternal()) {
            creativeGroup = campaignCreativeService.find(id).getCreativeGroup();

            return "successExternal";
        }

        return SUCCESS;
    }

    public String undelete() {
        campaignCreativeService.undelete(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampaignCreativeGroup getCreativeGroup() {
        return creativeGroup;
    }

    public void setCcgId(Long ccgId) {
        creativeGroup.setId(ccgId);
    }

    public Long getCcgId() {
        return creativeGroup.getId();
    }
}
