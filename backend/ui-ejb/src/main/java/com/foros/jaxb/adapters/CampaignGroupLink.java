package com.foros.jaxb.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class CampaignGroupLink extends EntityLink {
    private EntityLink campaign;

    public CampaignGroupLink() {
    }

    public CampaignGroupLink(Long ccgId, Long campaignId) {
        super(ccgId);
        campaign = new EntityLink(campaignId);
    }

    public EntityLink getCampaign() {
        return campaign;
    }

    public void setCampaign(EntityLink campaign) {
        this.campaign = campaign;
    }
}
