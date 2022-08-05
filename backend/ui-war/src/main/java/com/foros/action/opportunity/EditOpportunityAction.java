package com.foros.action.opportunity;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.opportunity.Opportunity;
import com.foros.restriction.annotation.Restrict;

public class EditOpportunityAction extends EditOpportunityActionBase {

    private Long advertiserId;

    @ReadOnly
    @Restrict(restriction = "Opportunity.create", parameters = "find('AdvertiserAccount', #target.advertiserId)")
    public String create() {
        opportunity =  new Opportunity();
        opportunity.setAccount(new AdvertiserAccount(advertiserId));
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Opportunity.update", parameters="find('Opportunity',#target.model.id)")
    public String edit() {
        opportunity = opportunityService.view(opportunity.getId());
        loadExistingFiles();
        return SUCCESS;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (opportunity.getId() != null) {
            breadcrumbs = new Breadcrumbs().add(new OpportunityBreadcrumbsElement(opportunity)).add(ActionBreadcrumbs.EDIT);
        }
        return breadcrumbs;
    }
}
