package com.foros.action.finance;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.model.finance.FinanceStatus;
import com.foros.model.finance.Invoice;

public class EditInvoiceAction extends EditSaveInvoiceActionBase {
    private Long id;

    @ReadOnly
    public String edit() {
        invoice = financeService.viewInvoice(id);

        prepareDatesForEdit();

        invoice.setTotalAmountDue(null);
        invoice.setStatus(FinanceStatus.OPEN);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Invoice getExistingInvoice() {
        return invoice;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (isCampaignContext()) {
            breadcrumbs.add(new CampaignBreadcrumbsElement(invoice.getCampaign())).add(new CampaignInvoiceBreadcrumbsElement(invoice)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs.add(new AdvertiserInvoiceBreadcrumbsElement(invoice)).add(ActionBreadcrumbs.EDIT);

        }
        return breadcrumbs;
    }
}
