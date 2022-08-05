package com.foros.action.finance;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.finance.Invoice;
import com.foros.model.finance.InvoiceData;
import com.foros.model.finance.InvoicingPeriod;
import com.foros.security.AccountRole;
import com.foros.session.admin.country.CountryService;
import com.foros.session.campaignAllocation.CampaignAllocationService;
import com.foros.session.campaignAllocation.InvoiceOpportunityTO;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewInvoiceAction extends InvoiceActionSupport implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    private CountryService countryService;

    @EJB
    private CampaignAllocationService campaignAllocationService;

    protected Long id;

    private InvoicingPeriod invoicingPeriod;

    protected Map<Long, Long> invoiceDataQuantityMap;

    private List<InvoiceOpportunityTO> opportunities;

    @ReadOnly
    public String view() {
        invoice = financeService.viewInvoiceWithWebData(id);

        if (invoice.getDeductFromPrepaidAmount() != null && invoice.getDeductFromPrepaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setDeductFromPrepaidAmount(null);
        }

        invoiceDataQuantityMap = new HashMap<Long, Long>(invoice.getInvoiceDatas().size());

        for (InvoiceData invoiceData : invoice.getInvoiceDatas()) {
            this.invoiceDataQuantityMap.put(invoiceData.getId(), invoiceData.getQuantity());
        }

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isGenPrintInvoiceAvailable() {
        return invoice.isPrintable();
    }

    public InvoicingPeriod getInvoicingPeriod() {
        if (invoicingPeriod != null) {
            return invoicingPeriod;
        }

        invoicingPeriod = financeService.findInvoicingPeriodByInvoiceId(id);

        return invoicingPeriod;
    }

    public Map<Long, Long> getInvoiceDataQuantityMap() {
        return invoiceDataQuantityMap;
    }

    public void setInvoiceDataQuantityMap(Map<Long, Long> invoiceDataQuantityMap) {
        this.invoiceDataQuantityMap = invoiceDataQuantityMap;
    }

    public boolean isAgencyFlag() {
        return invoice.getAccount().getAccountType().getAccountRole() == AccountRole.AGENCY;
    }

    public List<InvoiceOpportunityTO> getOpportunities() {
        if (opportunities != null) {
            return opportunities;
        }

        boolean perAdvInvoicing = !invoice.getAccount().getAccountType().isPerCampaignInvoicingFlag();
        opportunities = campaignAllocationService.findInvoiceOpportunities(id, perAdvInvoicing);

        return opportunities;
    }

    @Override
    public Invoice getExistingInvoice() {
        return invoice;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CampaignBreadcrumbsElement(invoice.getCampaign())).add(new CampaignInvoiceBreadcrumbsElement(invoice));
    }
}
