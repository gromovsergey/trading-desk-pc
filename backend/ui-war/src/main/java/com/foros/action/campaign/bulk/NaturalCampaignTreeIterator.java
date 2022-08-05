package com.foros.action.campaign.bulk;

import com.foros.model.EntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;

import java.util.Collections;
import java.util.Iterator;

public class NaturalCampaignTreeIterator implements Iterator<EntityBase> {

    public static final Iterator<? extends EntityBase> EMPTY = Collections.<EntityBase>emptyList().iterator();

    private Iterator<Campaign> campaignIterator;
    private Iterator<CampaignCreativeGroup> ccgIterator = empty();
    private Iterator<CampaignCreative> creativeIterator = empty();
    private Iterator<CCGKeyword> keywordIterator = empty();

    public NaturalCampaignTreeIterator(Iterator<Campaign> campaignIterator) {
        this.campaignIterator = campaignIterator;
    }

    @Override
    public boolean hasNext() {
        return keywordIterator.hasNext() || creativeIterator.hasNext() || ccgIterator.hasNext() || campaignIterator.hasNext();
    }

    @Override
    public EntityBase next() {
        if (keywordIterator.hasNext()) {
            return keywordIterator.next();
        } else if (creativeIterator.hasNext()) {
            return creativeIterator.next();
        } else if (ccgIterator.hasNext()) {
            CampaignCreativeGroup ccg = ccgIterator.next();
            creativeIterator = ccg.getCampaignCreatives().iterator();
            keywordIterator = ccg.getCcgKeywords().iterator();
            return ccg;
        } else {
            Campaign campaign = campaignIterator.next();
            ccgIterator = campaign.getCreativeGroups().iterator();
            return campaign;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings({"unchecked"})
    private <T> Iterator<T> empty() {
        return (Iterator<T>) EMPTY;
    }
}
