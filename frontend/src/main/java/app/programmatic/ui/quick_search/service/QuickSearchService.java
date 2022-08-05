package app.programmatic.ui.quick_search.service;

import app.programmatic.ui.quick_search.model.QuickSearchResult;

public interface QuickSearchService {
    QuickSearchResult search(String text);
}
