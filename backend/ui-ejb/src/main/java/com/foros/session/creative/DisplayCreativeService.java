package com.foros.session.creative;

import com.foros.model.account.Account;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.YandexCreativeTO;
import com.foros.model.template.TemplateFile;
import com.foros.session.EntityTO;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CreativeSelector;
import com.foros.session.campaign.bulk.YandexCreativeSelector;
import com.foros.session.status.Approvable;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

@Local
public interface DisplayCreativeService extends Approvable {
    Long create(Creative creative);

    void update(Creative creative);

    void delete(Long id);

    void undelete(Long id);

    void inactivate(Long id);

    void activate(Long id);

    void refresh(Long id);

    List<CreativeCategory> findCategoriesByType(CreativeCategoryType type);

    List<EntityTO> getCreativeSizeOrApplicationFormatLinkedCreatives(TemplateFile templateFile);

    Creative find(Long id);

    Creative findWithOptions(Long id);

    Creative view(Long id);

    Collection<EntityTO> findEntityTOByAdvertiser(Long accountId);

    Collection<EntityTO> findEntityTOByAdvertiser(Long accountId, boolean isOnlyTextAds);

    Collection<EntityTO> findEntityTOByAdvertiserAndTargetType(Long accountId, boolean isOnlyTextAds, TGTType targetType);

    Collection<EntityTO> findByCampaignId(Long campaignId);

    Collection<EntityTO> findByCampaignId(Long campaignId, boolean isOnlyTextAds);

    Collection<EntityTO> findByCampaignIdAndTargetType(Long campaignId, boolean isOnlyTextAds, TGTType targetType);

    Collection<EntityTO> findByCreativeGroupId(Long creativeGroupId);

    CreativeCategory findCategory(Long id);

    CreativeCategory findCategory(CreativeCategoryType type, String name, boolean showHold);

    List<CreativeCategory> searchCategory(CreativeCategoryType type, String name, boolean showHold, int maxResults);

    Collection<CampaignCreative> findCampaignCreatives(Long creativeId);

    List<EntityTO> findForLink(CampaignCreativeGroup campaignCreativeGroup, Long ccId);

    boolean hasCampaignCreative(Long creativeId, Long groupId);

    Result<Creative> get(CreativeSelector creativeSelector);

    OperationsResult perform(Operations<Creative> operations);

    ValidationResultTO validateAll(CreativeCsvReaderResult readerResult, Account account);

    void createOrUpdateAll(String validationResultId);

    public CreativeCsvReaderResult getValidatedResults(String validationResultId);

    void setClickUrl(List<Long> creativeIds, String url);

    void appendClickUrl(List<Long> creativeIds, String append);

    void findReplaceClickUrl(List<Long> creativeIds, String find, String replace);

    void validateAll(Long advertiserId, Collection<Creative> creatives);

    void createOrUpdateAll(Long accountId, List<Creative> creatives);

    Result<YandexCreativeTO> getYandexCreativeTO(YandexCreativeSelector selector);

    String calculateHash(Creative creative);
}
