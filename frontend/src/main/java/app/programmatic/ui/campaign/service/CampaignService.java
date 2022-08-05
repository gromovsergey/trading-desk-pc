package app.programmatic.ui.campaign.service;

import com.foros.rs.client.model.advertising.campaign.Campaign;
import app.programmatic.ui.campaign.dao.model.CampaignFlightPart;

import java.util.Collection;
import java.util.List;

public interface CampaignService {

    Campaign find(Long id);

    Long findId(Long accountId, String name);

    Long createOrUpdate(Campaign campaign);

    CampaignFlightPart findFlightPart(Long campaignId);

    List<CampaignFlightPart> findFlightPart(Collection<Long> campaignIds);
}
