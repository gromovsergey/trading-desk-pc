package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.session.campaign.CampaignService;

import javax.ejb.EJB;

public class CreateCopyCampaignAction extends BaseActionSupport {
    @EJB
    private CampaignService campaignService;

    private Long id;

    public String createCopy() {
        id = campaignService.createCopy(id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
