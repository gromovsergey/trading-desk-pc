package com.foros.action.admin.option;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.template.OptionGroup;

public class OptionGroupBreadcrumbsElement extends EntityBreadcrumbsElement {

    public OptionGroupBreadcrumbsElement(OptionGroup optionGroup) {
        super("OptionGroup.breadcrumbs", optionGroup.getId(), optionGroup.getDefaultName(), "OptionGroup/view");
    }
}
