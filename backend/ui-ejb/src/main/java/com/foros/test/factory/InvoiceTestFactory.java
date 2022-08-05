package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.finance.FinanceStatus;
import com.foros.model.finance.Invoice;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Date;

@Stateless
@LocalBean
public class InvoiceTestFactory extends TestFactory<Invoice> {
    @EJB
    private AdvertiserAccountTestFactory advertiserAccounTF;

    @EJB
    private TextCampaignTestFactory campaignTF;

    public void populate(Invoice invoice) {
        invoice.setStatus(FinanceStatus.OPEN);
        invoice.setLineCount(1);
        invoice.setInvoiceDate(new Date());
        invoice.setTotalAmount(BigDecimal.ONE);
        invoice.setTotalAmountNet(BigDecimal.TEN);
        invoice.setPublisherAmountNet(BigDecimal.ONE);
        invoice.setOpenAmountNet(BigDecimal.ONE);
        invoice.setAdvertiserName(getTestEntityRandomName());
        invoice.setInvoiceEmailDate(new Date());
        invoice.setSoldToUserEmail(getTestEntityRandomName());
        invoice.setDeductFromPrepaidAmount(BigDecimal.ZERO);
        invoice.setTotalAmountDue(BigDecimal.TEN);
    }

    @Override
    public Invoice create() {
        AdvertiserAccount account = advertiserAccounTF.createPersistent();
        return create(account);
    }

    @Override
    public void persist(Invoice entity) {
        throw new IllegalStateException("There is no such function in a Service Bean");
    }

    @Override
    public void update(Invoice entity) {
        throw new IllegalStateException("There is no such function in a Service Bean");
    }

    public Invoice create(AdvertiserAccount account) {
        Invoice invoice = new Invoice();
        invoice.setAccount(account);
        Campaign campaign = campaignTF.createPersistent(account);
        invoice.setCampaign(campaign);
        populate(invoice);
        return invoice;
    }

    @Override
    public Invoice createPersistent() {
        Invoice invoice = create();
        return invoice;
    }

}
