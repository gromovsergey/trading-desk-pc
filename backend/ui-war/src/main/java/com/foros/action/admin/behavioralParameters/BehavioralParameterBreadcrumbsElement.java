package com.foros.action.admin.behavioralParameters;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.channel.BehavioralParametersList;

public class BehavioralParameterBreadcrumbsElement extends EntityBreadcrumbsElement {
    public BehavioralParameterBreadcrumbsElement(BehavioralParametersList behavioralParametersList) {
        super("channel.params", behavioralParametersList.getId(), behavioralParametersList.getName(), "behavioralParameters/view");
    }
}
