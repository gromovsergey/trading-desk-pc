package com.foros.action.creative.display;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.creative.Creative;

public class CreativeBreadcrumbsElement extends EntityBreadcrumbsElement {
    public CreativeBreadcrumbsElement(Creative creative) {
        super("creative.breadcrumbs", creative.getId(), creative.getName(), "creative/view");
    }
}
