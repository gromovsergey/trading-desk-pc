package com.foros.action.admin.searchEngine;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.admin.SearchEngine;

public class SearchEngineBreadcrumbsElement extends EntityBreadcrumbsElement {
    public SearchEngineBreadcrumbsElement(SearchEngine searchEngine) {
        super("SearchEngine.entityName", searchEngine.getId(), searchEngine.getName(), "SearchEngine/view");
    }
}
