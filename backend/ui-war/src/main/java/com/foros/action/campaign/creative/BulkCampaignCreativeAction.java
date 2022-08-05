package com.foros.action.campaign.creative;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.util.context.RequestContexts;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.EJB;

public class BulkCampaignCreativeAction extends BaseActionSupport implements RequestContextsAware {

    @EJB
    protected CampaignCreativeService campaignCreativeService;

    @EJB
    protected CampaignCreativeGroupService campaignCreativeGroupService;

    private long ccgId;
    private List<Long> ccIds;
    private Timestamp creativesMaxVersion;
    private long setNumber;
    private CampaignCreativeGroup creativeGroup;

    public String insertCreativesToSet() {
        campaignCreativeService.moveCreativesToNewSet(ccgId, ccIds, creativesMaxVersion, setNumber);
        return SUCCESS;
    }

    public String insertCreativesToLastSet() {
        setNumber = campaignCreativeService.getCreativeSetCountByCcgId(ccgId) + 1;
        campaignCreativeService.moveCreativesToNewSet(ccgId, ccIds, creativesMaxVersion, setNumber);
        return SUCCESS;
    }

    public String moveCreativesToExistingSet() {
        campaignCreativeService.moveCreativesToExistingSet(ccgId, ccIds, creativesMaxVersion, setNumber);
        return SUCCESS;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public Long getCcgId() {
        return ccgId;
    }

    public void setTextAdIds(List<Long> ccIds) {
        this.ccIds = ccIds;
    }

    public void setCreativesIds(List<Long> ccIds) {
        this.ccIds = ccIds;
    }

    public void setCreativesMaxVersion(Timestamp creativeMaxVersion) {
        this.creativesMaxVersion = creativeMaxVersion;
    }

    public void setNumberOfSet(long setNumber) {
        this.setNumber = setNumber;
    }

    public CampaignCreativeGroup getCreativeGroup() {
        if (creativeGroup == null) {
            creativeGroup = campaignCreativeGroupService.find(ccgId);
        }
        return creativeGroup;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getCreativeGroup().getAccount());
    }

}
