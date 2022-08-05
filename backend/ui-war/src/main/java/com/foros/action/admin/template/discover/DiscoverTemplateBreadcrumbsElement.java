package com.foros.action.admin.template.discover;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.template.Template;

public class DiscoverTemplateBreadcrumbsElement extends EntityBreadcrumbsElement {

    public DiscoverTemplateBreadcrumbsElement(Template template) {
        super("DiscoverTemplate.breadcrumbs", template.getId(), template.getDefaultName(), "DiscoverTemplate/view");
    }
}
