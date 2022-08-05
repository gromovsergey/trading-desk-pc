package com.foros.action.campaign.creative;

import com.foros.action.BaseActionSupport;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.campaign.CampaignCreativeService;

import java.util.Set;
import javax.ejb.EJB;

public class StatusCampaignCreativeBulkAction extends BaseActionSupport {
    @EJB
    private CampaignCreativeService campaignCreativeService;

    private CampaignCreativeGroup creativeGroup = new CampaignCreativeGroup();
    private Set<Long> creativesIds;

    public CampaignCreativeGroup getCreativeGroup() {
        return creativeGroup;
    }

    public String activateAll() {
        campaignCreativeService.activateAll(creativeGroup.getId(), creativesIds);
        return SUCCESS;
    }

    public String inactivateAll() {
        campaignCreativeService.inactivateAll(creativeGroup.getId(), creativesIds);
        return SUCCESS;
    }

    public String deleteAll() {
        campaignCreativeService.deleteAll(creativeGroup.getId(), creativesIds);
        return SUCCESS;
    }

    public String undeleteAll() {
        campaignCreativeService.undeleteAll(creativeGroup.getId(), creativesIds);
        return SUCCESS;
    }

    public void setCcgId(Long ccgId) {
        creativeGroup.setId(ccgId);
    }

    public Long getCcgId() {
        return creativeGroup.getId();
    }

    public Set<Long> getCreativesIds() {
        return creativesIds;
    }

    public void setCreativesIds(Set<Long> creativesIds) {
        this.creativesIds = creativesIds;
    }
}

