package com.foros.action.colocation;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.isp.Colocation;

public class ColocationBreadcrumbsElement extends EntityBreadcrumbsElement {

    public ColocationBreadcrumbsElement(Colocation colocation) {
        super("colocation.breadcrumbs", colocation.getId(), colocation.getName(), "colocation/view");
    }
}
