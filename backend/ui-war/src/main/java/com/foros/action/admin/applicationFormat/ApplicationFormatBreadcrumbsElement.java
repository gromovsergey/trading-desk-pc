package com.foros.action.admin.applicationFormat;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.template.ApplicationFormat;

public class ApplicationFormatBreadcrumbsElement extends EntityBreadcrumbsElement {
    public ApplicationFormatBreadcrumbsElement(ApplicationFormat entity) {
        super("ApplicationFormat.breadcrumbs", entity.getId(), entity.getName(), "ApplicationFormat/view");
    }
}
