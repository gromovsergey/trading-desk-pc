package com.foros.session.campaign.bulk;

import com.foros.model.creative.YandexCreativeTO;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;

import java.util.List;

public class YandexCreativeSelector implements Selector<YandexCreativeTO> {

    private List<Long> creatives;
    private Paging paging;

    public List<Long> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<Long> creatives) {
        this.creatives = creatives;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
