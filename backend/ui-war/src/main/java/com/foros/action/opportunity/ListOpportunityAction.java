package com.foros.action.opportunity;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.opportunity.Opportunity;
import com.foros.session.opportunity.OpportunityService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;
import java.util.Collection;

public class ListOpportunityAction extends BaseActionSupport implements RequestContextsAware, AdvertiserSelfIdAware {

    @EJB
    private OpportunityService opportunityService;

    private Collection<Opportunity> opportunities;

    private Long advertiserId;

    @ReadOnly
    public String list() {
        opportunities = opportunityService.findOpportunitiesForAccount(advertiserId);
        return SUCCESS;
    }

    public Collection<Opportunity> getOpportunities() {
        return opportunities;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(advertiserId);
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

}
