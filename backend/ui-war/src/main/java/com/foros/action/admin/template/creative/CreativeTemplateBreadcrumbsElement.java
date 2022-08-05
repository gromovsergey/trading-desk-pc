package com.foros.action.admin.template.creative;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.template.Template;

public class CreativeTemplateBreadcrumbsElement extends EntityBreadcrumbsElement {

    public CreativeTemplateBreadcrumbsElement(Template template) {
        super("CreativeTemplate.breadcrumbs", template.getId(), template.getDefaultName(), "CreativeTemplate/view");
    }
}
