package com.foros.session.campaign;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CreativeLinkSelector;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

@Local
public interface CampaignCreativeService {
    Long create(CampaignCreative campaignCreative);

    void update(CampaignCreative campaignCreative);

    void delete(Long id);

    void undelete(Long id);

    void inactivate(Long id);

    void activate(Long id);

    void activateAll(Long ccgId, Collection<Long> ids);

    void inactivateAll(Long ccgId, Collection<Long> ids);

    void deleteAll(Long ccgId, Collection<Long> ids);

    void undeleteAll(Long ccgId, Collection<Long> ids);

    CampaignCreative view(Long id);

    CampaignCreative find(Long id);

    void refresh(Long id);

    Long createCreativeWithLinks(Creative creative, CampaignCreative campaignCreative, Collection<Long> ccgIds) throws IOException;

    List<TreeFilterElementTO> searchCreatives(Long ccgId);

    List<TreeFilterElementTO> searchCreativesBySizeType(Long ccgId, Long sizeTypeId);

    void moveCreativesToExistingSet(Long ccgId, List<Long> creativeIds, Timestamp maxVersion, Long setNumber);

    void moveCreativesToNewSet(Long ccgId, List<Long> creativeIds, Timestamp maxVersion, Long setNumber);

    public Timestamp getCreativesMaxVersionByCcgId(Long ccgId);

    int getCreativeSetCountByCcgId(Long ccgId);

    boolean isBatchActionPossible(Collection<Long> ids, String action);

    void createAll(Long advertiserId, Collection<Long> creativeIds, Collection<Long> groupIds, boolean isDisplay);

    Result<CampaignCreative> get(CreativeLinkSelector creativeLinkSelector);

    OperationsResult perform(Operations<CampaignCreative> creativeLinkOperations);

    void createOrUpdateAll(Long ccgId, Collection<CampaignCreative> campaignCreatives);

    void validateAll(CampaignCreativeGroup ccg, Collection<CampaignCreative> campaignCreatives, TGTType tgtType);

    List<Long> findCreativeIdsForBulkUpdate(Long campaignId, Collection<Long> ccgIds);

    Collection<SizeType> findSizeTypesForEdit(Long ccgId);

    Collection<SizeType> findSizeTypesForEditByAccountId(Long accountId);

    Set<CreativeSize> getEffectiveTagSizes(Creative creative, AdvertiserAccount account);

    Set<CreativeSize> getEffectiveTagSizes(Creative creative);

    void updateImagePreview(AdvertiserAccount account, String imagePath);

    void updateAllImagePreviews();

}

