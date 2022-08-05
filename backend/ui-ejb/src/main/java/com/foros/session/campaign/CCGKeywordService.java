package com.foros.session.campaign;

import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CCGKeywordSelector;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

@Local
public interface CCGKeywordService {
    void update(Collection<CCGKeyword> keywords, Long ccgId, Timestamp ccgVersion);

    void activate(Collection<Long> ids, Long ccgId);

    void inactivate(Collection<Long> ids, Long ccgId);

    void delete(Collection<Long> ids, Long ccgId);

    void undelete(Collection<Long> ids, Long ccgId);

    Collection<CCGKeyword> findAll(Long ccgId);

    void validateAll(CampaignCreativeGroup ccg, Collection<CCGKeyword> ccgKeywords, TGTType tgtType);

    void createOrUpdateAll(Long id, Set<CCGKeyword> ccgKeywords);

    Result<CCGKeyword> get(CCGKeywordSelector keywordSelector);

    OperationsResult perform(Operations<CCGKeyword> operations);

    CCGKeyword find(Long id);

    List<EditCCGKeywordTO> findCCGKeywords(Long ccgId);
}
