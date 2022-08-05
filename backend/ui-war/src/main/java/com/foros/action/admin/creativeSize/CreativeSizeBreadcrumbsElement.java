package com.foros.action.admin.creativeSize;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.creative.CreativeSize;

public class CreativeSizeBreadcrumbsElement extends EntityBreadcrumbsElement {
    public CreativeSizeBreadcrumbsElement(CreativeSize size) {
        super("CreativeSize.breadcrumbs", size.getId(), size.getDefaultName(), "CreativeSize/view");
    }
}
