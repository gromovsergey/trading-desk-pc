package com.foros.action.finance;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.finance.Invoice;

public class MyInvoiceBreadcrumbsElement extends EntityBreadcrumbsElement {
    public MyInvoiceBreadcrumbsElement(Invoice invoice) {
        super("campaign.invoice", invoice.getId(), String.valueOf(invoice.getId()), "myAccount/invoiceView");
    }
}
