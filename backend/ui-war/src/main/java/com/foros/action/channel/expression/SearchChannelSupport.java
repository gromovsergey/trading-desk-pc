package com.foros.action.channel.expression;

import com.foros.action.channel.SearchCriteria;
import com.foros.action.channel.behavioral.EditChannelSupport;
import com.foros.model.channel.ExpressionChannel;

public abstract class SearchChannelSupport extends EditChannelSupport<ExpressionChannel> {

    protected SearchCriteria searchCriteria = new SearchCriteria();

    public void setSearchCriteria(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

}
