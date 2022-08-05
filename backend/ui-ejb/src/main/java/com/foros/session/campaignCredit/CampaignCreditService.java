package com.foros.session.campaignCredit;

import com.foros.model.campaign.CampaignCredit;
import com.foros.session.EntityTO;

import javax.ejb.Local;
import java.math.BigDecimal;
import java.util.List;

@Local
public interface CampaignCreditService {

    Long create(CampaignCredit campaignCredit);

    CampaignCredit update(CampaignCredit campaignCredit);

    void delete(Long id);

    CampaignCredit view(Long id);

    CampaignCredit find(Long id);

    CampaignCreditStatsTO getStats(Long campaignCreditId);

    List<Long> getAllocationsAdvertiserIds(Long campaignCreditId);

    List<CampaignCreditTO> findCampaignCredits(Long accountId);

    boolean hasCampaignCredits(Long accountId);

    List<EntityTO> getCampaignsForCreditAllocation(Long advertiserId);
}
