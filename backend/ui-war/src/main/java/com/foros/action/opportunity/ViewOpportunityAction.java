package com.foros.action.opportunity;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.action.finance.AdvertiserInvoiceBreadcrumbsElement;
import com.foros.action.finance.CampaignInvoiceBreadcrumbsElement;
import com.foros.action.finance.MyInvoiceBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocationSumTO;
import com.foros.model.finance.Invoice;
import com.foros.session.campaign.CampaignService;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.util.context.RequestContexts;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;

public class ViewOpportunityAction extends OpportunitySupportAction implements RequestContextsAware,  BreadcrumbsSupport {

    @EJB
    protected CampaignService campaignService;

    @EJB
    protected AdvertisingFinanceService financeService;

    private List<CampaignAllocationSumTO> campaignAllocations = null;

    private Campaign campaign;
    private Invoice invoice;

    private Long campaignId;
    private Long invoiceId;

    @ReadOnly
    public String view() {
        opportunity = opportunityService.view(opportunity.getId());
        return SUCCESS;
    }

    @ReadOnly
    public String viewIO() {
        opportunity = opportunityService.viewIO(opportunity.getId());
        return SUCCESS;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(opportunity.getAccount());
    }

    public List<CampaignAllocationSumTO> getCampaignAssociations(){
        if (campaignAllocations == null) {
            campaignAllocations = opportunityService.findCampaignAllocationSum(opportunity.getId());
        }
        return campaignAllocations;
    }

    public BigDecimal getSpentAmount() {
        BigDecimal spentAmount = BigDecimal.ZERO;
        for (CampaignAllocationSumTO allocation : getCampaignAssociations()) {
            spentAmount = spentAmount.add(allocation.getUtilizedAmount());
        }
        return spentAmount;
    }

    public BigDecimal getAvailableAmount() {
        BigDecimal availableAmount = getModel().getAmount().subtract(getSpentAmount());
        availableAmount = availableAmount.max(BigDecimal.ZERO);
        return availableAmount;
    }

    public BigDecimal getUnallocatedAmount() {
        BigDecimal allocatedAmount = BigDecimal.ZERO;
        for (CampaignAllocationSumTO allocation : getCampaignAssociations()) {
            allocatedAmount = allocatedAmount.add(allocation.getAmount());
        }
        BigDecimal unallocatedAmount = getModel().getAmount().subtract(allocatedAmount);
        unallocatedAmount = unallocatedAmount.max(BigDecimal.ZERO);
        return unallocatedAmount;
    }

    public Campaign getCampaign() {
        if (campaign == null && campaignId != null) {
            campaign = campaignService.find(campaignId);
        }
        return campaign;
    }

    public Invoice getInvoice() {
        if (invoice == null && invoiceId != null) {
            invoice = financeService.findInvoiceById(invoiceId);
        }
        return invoice;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public boolean isAgencyAdvertiserAccountRequest() {
        return getInvoice() != null && getInvoice().getAccount().isInAgencyAdvertiser();
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();

        Campaign campaign = getCampaign();
        if (campaign != null) {
            breadcrumbs.add(new CampaignBreadcrumbsElement(campaign)).add(new CampaignInvoiceBreadcrumbsElement(getInvoice()));
        } else {
            breadcrumbs.add(isInternal() ? new AdvertiserInvoiceBreadcrumbsElement(getInvoice()) : new MyInvoiceBreadcrumbsElement(getInvoice()));
        }
        breadcrumbs.add(new OpportunityBreadcrumbsElement(getModel()));

        return breadcrumbs;
    }
}
