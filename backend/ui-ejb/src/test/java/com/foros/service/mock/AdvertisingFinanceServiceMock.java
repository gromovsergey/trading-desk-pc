package com.foros.service.mock;

import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.finance.Invoice;
import com.foros.model.finance.InvoicingPeriod;
import com.foros.session.finance.AdvertisingFinanceServiceBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvertisingFinanceServiceMock extends AdvertisingFinanceServiceBean {

    private Map<Long, Invoice> registeredInvoices = new HashMap<Long, Invoice>();
    private boolean inMockMode = false;

    public void reset() {
        init(false);
    }

    public void init(boolean setMockMode) {
        inMockMode = setMockMode;
        registeredInvoices.clear();
    }

    public void registerInvoice(Invoice invoice) {
        if (invoice == null || invoice.getId() == null) {
            throw new RuntimeException("Can't register null invoice or invoice without id");
        }
        if (registeredInvoices.containsKey(invoice.getId())) {
            throw new RuntimeException("Invoice (id=" + invoice.getId() + ") already registered");
        }
        registeredInvoices.put(invoice.getId(), invoice);
    }

    @Override
    public void refreshInvoice(Long id) {
        if (!inMockMode) {
            super.refreshInvoice(id);
            return;
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public Invoice findInvoiceById(Long id) {
        if (!inMockMode) {
            return super.findInvoiceById(id);
        }
        return registeredInvoices.get(id);
    }

    @Override
    public Invoice viewInvoice(Long id) {
        if (!inMockMode) {
            return super.viewInvoice(id);
        }
        return registeredInvoices.get(id);
    }

    @Override
    public InvoicingPeriod findInvoicingPeriodByInvoiceId(Long id) {
        if (!inMockMode) {
            return super.findInvoicingPeriodByInvoiceId(id);
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public Invoice viewInvoiceWithWebData(Long Id) {
        if (!inMockMode) {
            return super.viewInvoiceWithWebData(Id);
        }
        return registeredInvoices.get(Id);
    }

    @Override
    public void updateInvoice(Invoice invoice) {
        if (!inMockMode) {
            super.updateInvoice(invoice);
            return;
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public List<Invoice> findInvoicesByAccount(Long id) {
        if (!inMockMode) {
            return super.findInvoicesByAccount(id);
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public List<Invoice> findInvoicesByCampaign(Long campaignId) {
        if (!inMockMode) {
            return super.findInvoicesByCampaign(campaignId);
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public void updateFinance(AdvertisingFinancialSettings financialSettings) {
        if (!inMockMode) {
            super.updateFinance(financialSettings);
            return;
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public AdvertisingFinancialSettings getFinancialSettings(long accountId) {
        if (!inMockMode) {
            return super.getFinancialSettings(accountId);
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public void generateInvoice(Long invoiceId) {
        if (!inMockMode) {
            super.generateInvoice(invoiceId);
            return;
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public boolean isCreditLimitValid(Long accountId, BigDecimal creditLimit) {
        if (!inMockMode) {
            return super.isCreditLimitValid(accountId, creditLimit);
        }
        throw new RuntimeException("Currently this method is not implemented");
    }

    @Override
    public BigDecimal getConvertedMaxCreditLimit(Long accountId) {
        if (!inMockMode) {
            return super.getConvertedMaxCreditLimit(accountId);
        }
        throw new RuntimeException("Currently this method is not implemented");
    }
}
