package com.foros.action.admin.option;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.template.Option;

public class OptionBreadcrumbsElement extends EntityBreadcrumbsElement {
    public OptionBreadcrumbsElement(Option option) {
        super("Option.breadcrumbs", option.getId(), option.getDefaultName(), "Option/view");
    }
}
