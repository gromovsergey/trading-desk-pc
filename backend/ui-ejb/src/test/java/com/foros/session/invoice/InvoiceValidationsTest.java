package com.foros.session.invoice;

import com.foros.AbstractValidationsTest;
import com.foros.model.finance.FinanceStatus;
import com.foros.model.finance.Invoice;
import com.foros.service.mock.AdvertisingFinanceServiceMock;
import com.foros.test.factory.InvoiceTestFactory;
import com.foros.validation.strategy.ValidationStrategies;

import group.Db;
import group.Validation;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class InvoiceValidationsTest extends AbstractValidationsTest {
    @Autowired
    public InvoiceTestFactory invoiceTestFactory;

    @Autowired
    public AdvertisingFinanceServiceMock financeService;

    @Before
    public void initFinanceService() throws Exception {
        financeService.init(true);
    }

    @Test
    public void testDuplicateLegalNumber() {
        Invoice invoice1 = createInvoicePersistent();
        Invoice invoice2 = invoiceTestFactory.create(invoice1.getAccount());
        invoice2.setId(invoice1.getId()+1);
        financeService.registerInvoice(invoice2);
        invoice2.setInvoiceLegalNumber(invoice1.getInvoiceLegalNumber());
        validate("Invoice.update", invoice2);
        assertHasViolation("invoiceLegalNumber");
    }

    @Test
    public void testWrongDeductFromPrepaidAmount(){
        Invoice existing = invoiceTestFactory.create();
        existing.setId(1l);
        existing.setDeductFromPrepaidAmount(BigDecimal.ZERO);
        financeService.registerInvoice(existing);

        existing.getAccount().getFinancialSettings().getData().setPrepaidAmount(BigDecimal.ONE);
        entityManager.merge(existing.getAccount().getFinancialSettings());
        entityManager.flush();

        Invoice invoice = new Invoice(existing.getId());
        invoice.setDeductFromPrepaidAmount(BigDecimal.TEN);
        invoice.setTotalAmountDue(existing.getTotalAmountDue());
        validate("Invoice.update", invoice);
        assertHasViolation("deductFromPrepaidAmount");
    }

    @Test
    public void testNullClosedDate(){
        Invoice existing = createInvoicePersistent();
        Invoice invoice = new Invoice(existing.getId());
        invoice.setDeductFromPrepaidAmount(null);
        invoice.setTotalAmountDue(null);
        invoice.setStatus(FinanceStatus.CLOSED);
        invoice.setClosedDate(null);
        validate("Invoice.update", invoice);
        assertHasViolation("closedDate");
    }

    @Test
    public void testClosedDateWithNullDeductFromPrepaidAmount(){
        Invoice existing = invoiceTestFactory.create();
        existing.setId(1l);
        existing.setPaidAmount(BigDecimal.ONE);
        financeService.registerInvoice(existing);

        existing.getAccount().getFinancialSettings().getData().setPrepaidAmount(BigDecimal.ZERO);
        entityManager.merge(existing.getAccount().getFinancialSettings());
        entityManager.flush();

        Invoice invoice = new Invoice(existing.getId());
        invoice.setTotalAmountDue(existing.getTotalAmountDue());
        invoice.setPaidAmount(BigDecimal.TEN);
        invoice.setCreditSettlement(BigDecimal.TEN);
        invoice.setStatus(FinanceStatus.CLOSED);
        invoice.setClosedDate(new Date());

        violations = new HashSet<>(
                validationService.validate(ValidationStrategies.update(), "Invoice.update", invoice).getConstraintViolations()
        );
        assertHasViolation("status");
    }

    @Test
    public void testCloseInvoiceAmountPayableDoesNotMatch(){
        Invoice existing = createInvoicePersistent();
        Invoice invoice = new Invoice(existing.getId());
        invoice.setDeductFromPrepaidAmount(null);
        invoice.setTotalAmountDue(BigDecimal.ONE);
        invoice.setStatus(FinanceStatus.CLOSED);
        invoice.setClosedDate(new Date());
        invoice.setTotalAmountDue(existing.getTotalAmountDue().add(BigDecimal.ONE));

        validate("Invoice.update", invoice);
        assertHasViolation("totalAmountDue");
    }

    @Test
    public void testCloseCalculationCheck(){
        Invoice existing = invoiceTestFactory.create();
        existing.setId(1l);
        existing.setDeductFromPrepaidAmount(BigDecimal.ONE);
        existing.setTotalAmountDue(BigDecimal.TEN);
        financeService.registerInvoice(existing);

        existing.getAccount().getFinancialSettings().getData().setPrepaidAmount(BigDecimal.TEN);
        entityManager.merge(existing.getAccount().getFinancialSettings());
        entityManager.flush();

        Invoice invoice = new Invoice(existing.getId());

        invoice.setStatus(FinanceStatus.CLOSED);
        invoice.setClosedDate(new Date());
        invoice.setTotalAmountDue(existing.getTotalAmountDue());

        invoice.setPaidAmount(BigDecimal.valueOf(4));
        invoice.setDeductFromPrepaidAmount(BigDecimal.ONE);
        invoice.setCreditSettlement(BigDecimal.valueOf(5));
        validate("Invoice.update", invoice);
        assertHasNoViolation("status");

        invoice.setPaidAmount(BigDecimal.valueOf(5));
        invoice.setDeductFromPrepaidAmount(BigDecimal.ONE);
        invoice.setCreditSettlement(BigDecimal.valueOf(5));
        validate("Invoice.update", invoice);
        assertHasViolation("status");

        invoice.setPaidAmount(BigDecimal.valueOf(5));
        invoice.setDeductFromPrepaidAmount(null);
        invoice.unregisterChange("deductFromPrepaidAmount");
        invoice.setCreditSettlement(BigDecimal.valueOf(5));
        validate("Invoice.update", invoice);
        assertHasNoViolation("status");
    }

    private Invoice createInvoicePersistent() {
        Invoice invoice = invoiceTestFactory.create();
        invoice.setId(1l);
        financeService.registerInvoice(invoice);

        return invoice;
    }
}
