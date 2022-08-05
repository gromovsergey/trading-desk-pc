package com.foros.session.campaign.bulk;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;

import javax.persistence.EntityManager;

public class BulkUtil {
    public static AdvertiserAccount findAccount(EntityManager em, Campaign campaign) {
        if (campaign.getId() != null) {
            return em.find(Campaign.class, campaign.getId()).getAccount();
        }

        if (campaign.getAccount() != null && campaign.getAccount().getId() != null) {
            return em.find(AdvertiserAccount.class, campaign.getAccount().getId());
        }

        return null;
    }

    public static AdvertiserAccount findAccount(EntityManager em, CampaignCreativeGroup ccg) {
        if (ccg.getId() != null) {
            return em.find(CampaignCreativeGroup.class, ccg.getId()).getAccount();
        }

        if (ccg.getCampaign() != null) {
            return findAccount(em, ccg.getCampaign());
        }

        return null;
    }

    public static AdvertiserAccount findAccount(EntityManager em, CCGKeyword keyword) {
        if (keyword.getId() != null) {
            return em.find(CCGKeyword.class, keyword.getId()).getCreativeGroup().getAccount();
        }

        if (keyword.getCreativeGroup() != null) {
            return findAccount(em, keyword.getCreativeGroup());
        }

        return null;
    }
}
