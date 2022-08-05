package com.foros.action.opportunity;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.opportunity.Opportunity;

public class OpportunityBreadcrumbsElement extends EntityBreadcrumbsElement {

    public OpportunityBreadcrumbsElement(Opportunity o) {
        super("opportunity.breadcrumbs.opportunity", o.getId(), o.getName(), "opportunity/view");
    }
}
