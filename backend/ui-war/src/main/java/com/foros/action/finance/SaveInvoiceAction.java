package com.foros.action.finance;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.finance.Invoice;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

public class SaveInvoiceAction extends EditSaveInvoiceActionBase {
    private Invoice existingInvoice;

    public SaveInvoiceAction() {
        invoice = new Invoice();
    }

    @Validations(
        conversionErrorFields = {
            @ConversionErrorFieldValidator(fieldName = "paidAmount", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "deductFromPrepaidAmount", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "creditSettlement", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "totalAmountDue", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "dueDate", key = "errors.field.date"),
            @ConversionErrorFieldValidator(fieldName = "invoiceEmailDate", key = "errors.field.date"),
            @ConversionErrorFieldValidator(fieldName = "closedDate", key = "errors.field.date")
        }
    )
    @Validate(validation = "Invoice.update", parameters = "#target.model")
    public String update() {
        financeService.updateInvoice(invoice);
        return SUCCESS;
    }

    @Override
    public Invoice getExistingInvoice() {
        if (existingInvoice != null) {
            return existingInvoice;
        }

        existingInvoice = financeService.viewInvoice(invoice.getId());

        return existingInvoice;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (invoice.getId() != null) {
            final Invoice persistent = financeService.viewInvoice(invoice.getId());
            if (isCampaignContext()) {
                breadcrumbs = new Breadcrumbs().add(new CampaignBreadcrumbsElement(persistent.getCampaign())).add(new CampaignInvoiceBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
            } else {
                breadcrumbs = new Breadcrumbs().add(new AdvertiserInvoiceBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
            }
        }
        return breadcrumbs;
    }
}
