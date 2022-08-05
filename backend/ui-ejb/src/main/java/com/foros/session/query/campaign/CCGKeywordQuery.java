package com.foros.session.query.campaign;

import com.foros.model.campaign.CCGKeyword;
import com.foros.session.query.AdvertiserEntityQuery;

import java.util.Collection;
import java.util.List;

public interface CCGKeywordQuery extends AdvertiserEntityQuery<CCGKeywordQuery> {

    CCGKeywordQuery campaigns(Collection<Long> ids);

    CCGKeywordQuery creativeGroups(Collection<Long> ids);

    CCGKeywordQuery keywords(Collection<Long> ids);

    CCGKeywordQuery existingByKeyword(List<CCGKeyword> keywords);
}
