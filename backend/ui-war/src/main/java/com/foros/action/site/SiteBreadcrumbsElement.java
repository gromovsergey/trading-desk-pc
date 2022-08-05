package com.foros.action.site;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.site.Site;

public class SiteBreadcrumbsElement extends EntityBreadcrumbsElement {
    public SiteBreadcrumbsElement(Site site) {
        super("site.breadcrumbs", site.getId(), site.getName(), "site/view");
    }
}
