package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignCreditAllocationTO;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.campaignAllocation.CampaignAllocationService;
import com.foros.session.campaignAllocation.OpportunityTO;
import com.foros.session.campaignCredit.CampaignCreditAllocationService;
import com.foros.session.campaignCredit.CampaignCreditService;
import com.foros.session.campaignCredit.CampaignCreditStatsTO;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

public abstract class EditSaveAllocationsActionBase extends BaseActionSupport implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    protected CampaignService campaignService;

    @EJB
    protected AccountService accountService;

    @EJB
    private CampaignCreditService campaignCreditService;

    @EJB
    protected CampaignAllocationService campaignAllocationService;

    @EJB
    private CampaignCreditAllocationService campaignCreditAllocationService;

    protected Long id;
    protected Campaign campaign = new Campaign();
    protected AdvertisingAccountBase existingAccount;

    private CampaignCreditStatsTO campaignCredit;
    private CampaignCreditAllocationTO campaignCreditAllocation;
    protected List<CampaignAllocation> campaignAllocations = new ArrayList<CampaignAllocation>();

    private List<OpportunityTO> availableOpportunities;
    private Map<Long, OpportunityTO> opportunitiesMap;

    protected Integer currentAllocationIndex;

    private Collection<Long> removableAllocationIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public AdvertisingAccountBase getExistingAccount() {
        if (existingAccount == null) {
            existingAccount = (AdvertisingAccountBase) accountService.find(campaign.getAccount().getId());
        }
        return existingAccount;
    }

    public CampaignCreditAllocationTO getCampaignCreditAllocation() {
        if (campaignCreditAllocation == null) {
            campaignCreditAllocation = campaignCreditAllocationService.findCreditAllocationForCampaign(id);
        }

        return campaignCreditAllocation;
    }

    public CampaignCreditStatsTO getCampaignCredit() {
        if (campaignCredit == null) {
            Long ccId = getCampaignCreditAllocation().getCampaignCredit().getId();
            campaignCredit = campaignCreditService.getStats(ccId);
        }
        return campaignCredit;
    }

    public List<CampaignAllocation> getCampaignAllocations() {
        return campaignAllocations;
    }

    public void setCampaignAllocations(List<CampaignAllocation> campaignAllocations) {
        this.campaignAllocations = campaignAllocations;
    }

    public List<OpportunityTO> getAvailableOpportunities() {
        if (availableOpportunities == null) {
            availableOpportunities = campaignAllocationService.getAvailableOpportunities(getExistingAccount().getId());
        }
        return availableOpportunities;
    }

    public Map<Long, OpportunityTO> getOpportunitiesMap() {
        if (opportunitiesMap == null) {
            opportunitiesMap = campaignAllocationService.getOpportunitiesMap(getExistingAccount().getId());
        }
        return opportunitiesMap;
    }

    public Integer getCurrentAllocationIndex() {
        return currentAllocationIndex;
    }

    public void setCurrentAllocationIndex(Integer currentAllocationIndex) {
        this.currentAllocationIndex = currentAllocationIndex;
    }

    public Collection<Long> getRemovableAllocationIds() {
        if (removableAllocationIds == null) {
            removableAllocationIds = campaignAllocationService.findRemovableAllocationIds(id);
        }
        return removableAllocationIds;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getExistingAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CampaignBreadcrumbsElement(campaign)).add("campaignAllocation.edit");
    }
}