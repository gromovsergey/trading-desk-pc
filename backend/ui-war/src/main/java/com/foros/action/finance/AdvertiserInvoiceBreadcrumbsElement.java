package com.foros.action.finance;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.finance.Invoice;

public class AdvertiserInvoiceBreadcrumbsElement extends EntityBreadcrumbsElement {
    public AdvertiserInvoiceBreadcrumbsElement(Invoice invoice) {
        super("campaign.invoice", invoice.getId(), String.valueOf(invoice.getId()), "advertiser/account/invoiceView");
    }
}
