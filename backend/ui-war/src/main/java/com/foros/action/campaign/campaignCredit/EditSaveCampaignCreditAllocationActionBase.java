package com.foros.action.campaign.campaignCredit;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.campaignCredit.CampaignCreditAllocationService;
import com.foros.session.campaignCredit.CampaignCreditService;
import com.foros.util.context.RequestContexts;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public abstract class EditSaveCampaignCreditAllocationActionBase extends BaseActionSupport
        implements ModelDriven<CampaignCreditAllocation>, RequestContextsAware, BreadcrumbsSupport {
    @EJB
    protected CampaignCreditAllocationService campaignCreditAllocationService;

    @EJB
    protected CampaignCreditService campaignCreditService;

    @EJB
    protected CampaignService campaignService;

    @EJB
    protected AccountService accountService;

    protected CampaignCreditAllocation campaignCreditAllocation;

    private CampaignCredit existingCampaignCredit;
    private Campaign existingCampaign;
    private Boolean allowSelectAdvertiser;
    private Long advertiserId;
    private Collection<EntityTO> advertisers;
    private Collection<EntityTO> campaigns;

    public CampaignCredit getExistingCampaignCredit() {
        if (existingCampaignCredit == null) {
            existingCampaignCredit = campaignCreditService.find(campaignCreditAllocation.getCampaignCredit().getId());
        }

        return existingCampaignCredit;
    }

    public Campaign getExistingCampaign() {
        if (existingCampaign == null && campaignCreditAllocation.getCampaign() != null) {
            existingCampaign = campaignService.find(campaignCreditAllocation.getCampaign().getId());
        }

        return existingCampaign;
    }

    public Boolean isAllowSelectAdvertiser() {
        if (allowSelectAdvertiser == null) {
            Account account = getExistingCampaignCredit().getAccount();
            allowSelectAdvertiser = AccountRole.AGENCY.equals(account.getRole());
        }

        return allowSelectAdvertiser;
    }

    public Long getAdvertiserId() {
        if (advertiserId == null) {
            if (!isAllowSelectAdvertiser()) {
                advertiserId = getExistingCampaignCredit().getAccount().getId();
            } else if (getExistingCampaignCredit().getAdvertiser() != null) {
                advertiserId = getExistingCampaignCredit().getAdvertiser().getId();
            }
        }
        return advertiserId;
    }

    public Collection<EntityTO> getAdvertisers() {
        if (advertisers == null) {
            advertisers = (Collection<EntityTO>) accountService.findAdvertisersTOByAgency(getExistingCampaignCredit().getAccount().getId());
        }

        return advertisers;
    }

    public Collection<EntityTO> getCampaigns() {
        if (campaigns == null) {
            if (getAdvertiserId() != null) {
                campaigns = campaignCreditService.getCampaignsForCreditAllocation(getAdvertiserId());
            } else {
                campaigns = Collections.emptyList();
            }
        }

        return campaigns;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    @Override
    public CampaignCreditAllocation getModel() {
        return campaignCreditAllocation;
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getAdvertiserContext().switchTo(getExistingCampaignCredit().getAccount().getId());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs().add(new ManageCampaignCreditBreadcrumbsElement(campaignCreditAllocation.getCampaignCredit()));
        if (campaignCreditAllocation.getId() != null) {
            breadcrumbs.add(new CampaignCreditAllocationBreadcrumbsElement(campaignCreditAllocation));
        } else {
            breadcrumbs.add("CampaignCreditAllocation.breadcrumbs.create");
        }
        return breadcrumbs;
    }
}
