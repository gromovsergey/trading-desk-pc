package com.foros.action.finance;

import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.TimeZoneAware;
import com.foros.model.finance.FinanceStatus;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class EditSaveInvoiceActionBase extends InvoiceActionSupport implements TimeZoneAware, BreadcrumbsSupport {
    private static final FinanceStatus[] FINANCE_STATUSES = new FinanceStatus[] {
        FinanceStatus.OPEN, FinanceStatus.CLOSED
    };

    private String invoiceEmailDateDisplay;
    private String invoiceEmailTimeDisplay;

    private String dueDateDisplay;

    private String closedDateDisplay;
    private String closedTimeDisplay;

    private TimeZone timeZone = null;

    protected boolean campaignContext;

    protected void prepareDatesForEdit() {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        TimeZone timeZone = getTimeZone();

        invoiceEmailDateDisplay = DateHelper.formatDate(invoice.getInvoiceEmailDate(), DateFormat.SHORT, timeZone, locale);
        invoiceEmailTimeDisplay = DateHelper.formatTime(invoice.getInvoiceEmailDate(), DateFormat.SHORT, timeZone, locale);

        if (invoice.getDueDate() != null) {
            dueDateDisplay = DateHelper.formatDate(invoice.getDueDate(), DateFormat.SHORT, timeZone, locale);
        } else {
            TimeZone accountTimeZone = TimeZone.getTimeZone(invoice.getAccount().getTimezone().getKey());
            String paymentTerms;

            if (invoice.getAccount().isFinancialFieldsPresent()) {
                paymentTerms = invoice.getAccount().getFinancialSettings().getPaymentTerms();
            } else {
                paymentTerms = invoice.getAccount().getAgency().getFinancialSettings().getPaymentTerms();
            }

            int intPaymentTerms;

            if (StringUtil.isPropertyEmpty(paymentTerms)) {
                intPaymentTerms = invoice.getAccount().getCountry().getDefaultPaymentTerms().intValue();
            } else {
                intPaymentTerms = Integer.parseInt(paymentTerms);
            }

            Calendar c = Calendar.getInstance(accountTimeZone, locale);
            c.setTime(new Date());
            c.add(Calendar.DATE, intPaymentTerms);
            Date dueDate = c.getTime();
            dueDateDisplay = DateHelper.formatDate(dueDate, DateFormat.SHORT, accountTimeZone, locale);
        }

        closedDateDisplay = DateHelper.formatDate(invoice.getClosedDate(), DateFormat.SHORT, timeZone, locale);
        closedTimeDisplay = DateHelper.formatTime(invoice.getClosedDate(), DateFormat.SHORT, timeZone, locale);
    }

    public String getInvoiceEmailDateDisplay() {
        return invoiceEmailDateDisplay;
    }

    public void setInvoiceEmailDateDisplay(String invoiceEmailDateDisplay) {
        this.invoiceEmailDateDisplay = invoiceEmailDateDisplay;
    }

    public String getInvoiceEmailTimeDisplay() {
        return invoiceEmailTimeDisplay;
    }

    public void setInvoiceEmailTimeDisplay(String invoiceEmailTimeDisplay) {
        this.invoiceEmailTimeDisplay = invoiceEmailTimeDisplay;
    }

    public String getDueDateDisplay() {
        return dueDateDisplay;
    }

    public void setDueDateDisplay(String dueDateDisplay) {
        this.dueDateDisplay = dueDateDisplay;
    }

    public String getClosedDateDisplay() {
        return closedDateDisplay;
    }

    public void setClosedDateDisplay(String closedDateDisplay) {
        this.closedDateDisplay = closedDateDisplay;
    }

    public String getClosedTimeDisplay() {
        return closedTimeDisplay;
    }

    public void setClosedTimeDisplay(String closedTimeDisplay) {
        this.closedTimeDisplay = closedTimeDisplay;
    }

    public FinanceStatus[] getAvailableStatuses() {
        return FINANCE_STATUSES;
    }

    @Override
    public TimeZone getTimeZone() {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        return timeZone;
    }

    public boolean isCampaignContext() {
        return campaignContext;
    }

    public void setCampaignContext(boolean campaignContext) {
        this.campaignContext = campaignContext;
    }
}
