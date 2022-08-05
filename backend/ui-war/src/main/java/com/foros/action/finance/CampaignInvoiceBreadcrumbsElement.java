package com.foros.action.finance;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.finance.Invoice;

public class CampaignInvoiceBreadcrumbsElement extends EntityBreadcrumbsElement {
    public CampaignInvoiceBreadcrumbsElement(Invoice invoice) {
        super("campaign.invoice", invoice.getId(), String.valueOf(invoice.getId()), "campaign/invoiceView");
    }
}
