package com.foros.action.campaign.creative;

import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.util.context.RequestContexts;

import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;

public abstract class EditSaveCampaignCreativeActionSupport extends CampaignCreativeActionSupport implements RequestContextsAware {
    @EJB
    private DisplayCreativeService displayCreativeService;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private CurrentUserService userService;

    protected Long ccgId;

    private List<EntityTO> creatives;

    protected CampaignCreativeGroup existingGroup;

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public CampaignCreativeGroup getExistingGroup() {
        if (existingGroup != null) {
            return existingGroup;
        }

        if (campaignCreative.getId() != null) {
            existingGroup = campaignCreativeService.find(campaignCreative.getId()).getCreativeGroup();
        } else {
            existingGroup = campaignCreativeGroupService.find(ccgId);
        }

        return existingGroup;
    }

    public List<EntityTO> getCreatives() {
        if (creatives != null) {
            return creatives;
        }

        creatives = displayCreativeService.findForLink(getExistingGroup(), campaignCreative.getId());

        return creatives;
    }

    @Override
    public void switchContext(RequestContexts context) {
        if (campaignCreative.getId() == null && ccgId == null) {
            throw new EntityNotFoundException("Cannot parse Creative Group Id");
        }

        context.getAdvertiserContext().switchTo(getExistingGroup().getAccount());
    }

    public boolean canUpdateWeight() {
        return userService.isInternal() || CCGType.TEXT != getExistingGroup().getCcgType();
    }

}
