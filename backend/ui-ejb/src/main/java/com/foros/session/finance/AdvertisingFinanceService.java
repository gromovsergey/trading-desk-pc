package com.foros.session.finance;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.finance.Invoice;
import com.foros.model.finance.InvoicingPeriod;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Local;

@Local
public interface AdvertisingFinanceService {

    void refreshInvoice(Long id);

    Invoice findInvoiceById(Long id);

    Invoice viewInvoice(Long id);
    
    InvoicingPeriod findInvoicingPeriodByInvoiceId(Long id);

    Invoice viewInvoiceWithWebData(Long Id);

    void updateInvoice(Invoice invoice);

    List<Invoice> findInvoicesByAccount(Long id);

    List<Invoice> findInvoicesByCampaign(Long campaignId);

    void updateFinance(AdvertisingFinancialSettings financialSettings);

    AdvertisingFinancialSettings getFinancialSettings(long accountId);

    void generateInvoice(Long invoiceId);

    void generateInvoicesByAccount(Account account);

    boolean isCreditLimitValid(Long accountId, BigDecimal creditLimit);

    BigDecimal getConvertedMaxCreditLimit(Long accountId);

    BigDecimal getCreditBalance(Long id);
}
