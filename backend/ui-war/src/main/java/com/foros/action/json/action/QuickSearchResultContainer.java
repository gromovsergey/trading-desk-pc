package com.foros.action.json.action;

import com.foros.model.quicksearch.QuickSearchResultItem;
import com.foros.model.quicksearch.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class QuickSearchResultContainer {
    private String query;
    private Map<Type, Collection<QuickSearchResultItem>> results;

    public QuickSearchResultContainer(String query, Map<Type, Collection<QuickSearchResultItem>> results) {
        this.query = query;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public Map<Type, Collection<QuickSearchResultItem>> getResults() {
        return Collections.unmodifiableMap(results);
    }
}
