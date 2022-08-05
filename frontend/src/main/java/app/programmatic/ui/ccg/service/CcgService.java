package app.programmatic.ui.ccg.service;

import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroup;
import app.programmatic.ui.ccg.dao.model.CcgLineItemPart;

import java.util.Collection;
import java.util.List;

public interface CcgService {

    CampaignCreativeGroup find(Long id);

    List<CampaignCreativeGroup> findAll(List<Long> id);

    CcgLineItemPart findLineItemPart(Long ccgId);

    List<CcgLineItemPart> findLineItemPart(Collection<Long> ccgIds);

    List<CampaignCreativeGroup> findByCampaignId(Long campaignId);

    Long createOrUpdate(CampaignCreativeGroup ccg);

    List<Long> createOrUpdate(List<CampaignCreativeGroup> ccgs);
}
