package com.foros.session.campaign;

import com.foros.model.EntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.util.Stats;
import com.foros.util.UploadUtils;

import java.util.HashSet;
import java.util.Set;

public class ValidationResultTO {
    private Stats campaigns = new Stats();
    private Stats groups = new Stats();
    private Stats ads = new Stats();
    private Stats creatives = new Stats();
    private Stats keywords = new Stats();
    private Set<Long> linesWithErrors = new HashSet<Long>();
    private String id;

    public Stats getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(Stats campaigns) {
        this.campaigns = campaigns;
    }

    public Stats getGroups() {
        return groups;
    }

    public void setGroups(Stats groups) {
        this.groups = groups;
    }

    public Stats getAds() {
        return ads;
    }

    public void setAds(Stats ads) {
        this.ads = ads;
    }

    public Stats getKeywords() {
        return keywords;
    }

    public void setKeywords(Stats keywords) {
        this.keywords = keywords;
    }

    public long getLineWithErrors() {
        return linesWithErrors.size();
    }

    public void addLineWithErrors(Long lineWithErrors) {
        this.linesWithErrors.add(lineWithErrors);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Stats getCreatives() {
        return creatives;
    }

    public void setCreatives(Stats creatives) {
        this.creatives = creatives;
    }

    public void add(EntityBase entity) {
        UploadContext context = UploadUtils.getUploadContext(entity);
        if (UploadUtils.isLink(entity)) {
            return;
        }
        switch (context.getStatus()) {
        case NEW:
            getStats(entity).appendCreated();
            break;
        case UPDATE:
            getStats(entity).appendUpdated();
            break;
        case REJECTED:
            addLineWithErrors(UploadUtils.getRowNumber(entity));
            break;
        }

    }

    private Stats getStats(EntityBase entity) {
        if (entity instanceof Campaign) {
            return getCampaigns();
        } else if (entity instanceof CampaignCreativeGroup) {
            return getGroups();
        } else if (entity instanceof CampaignCreative) {
            return getAds();
        } else if (entity instanceof Creative) {
            return getCreatives();
        } else if (entity instanceof CCGKeyword) {
            return getKeywords();
        }

        throw new IllegalArgumentException();
    }
}
