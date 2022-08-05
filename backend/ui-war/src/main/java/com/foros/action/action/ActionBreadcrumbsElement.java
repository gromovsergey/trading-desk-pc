package com.foros.action.action;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.action.Action;

public class ActionBreadcrumbsElement extends EntityBreadcrumbsElement {
    public ActionBreadcrumbsElement(Action action) {
        super("Action.entityName", action.getId(), action.getName(), "Action/view");
    }
}
