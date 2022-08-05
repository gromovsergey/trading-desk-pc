package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.session.campaign.CampaignCreativeGroupService;

import javax.ejb.EJB;

public class CreateCopyCampaignGroupAction extends BaseActionSupport {
    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    private Long id;

    public String createCopy() {
        id = campaignCreativeGroupService.createCopy(id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
