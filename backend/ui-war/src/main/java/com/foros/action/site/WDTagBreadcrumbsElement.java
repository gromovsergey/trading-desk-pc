package com.foros.action.site;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.site.WDTag;

public class WDTagBreadcrumbsElement extends EntityBreadcrumbsElement {
    public WDTagBreadcrumbsElement(WDTag tag) {
        super("site.breadcrumbs.wdTag", tag.getId(), tag.getName(), "site/WDTag/view");
    }
}
