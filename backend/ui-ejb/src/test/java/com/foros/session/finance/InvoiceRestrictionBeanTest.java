package com.foros.session.finance;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.finance.FinanceStatus;
import com.foros.model.finance.Invoice;
import com.foros.model.finance.InvoiceData;
import com.foros.test.UserDefinition;
import com.foros.test.factory.InvoiceTestFactory;

import java.math.BigDecimal;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class InvoiceRestrictionBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private InvoiceRestrictions invoiceRestrictions;

    @Autowired
    private InvoiceTestFactory invoiceTF;

    @Test
    public void testCanView() throws Exception {
        final Invoice invoice = invoiceTF.create();
        invoice.setAccount((AdvertiserAccount) advertiserAllAccess1.getUser().getAccount());

        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return invoiceRestrictions.canView(invoice);
            }
        };

        verifyAllFalseExpectationExceptInternal(callable, invoice, FinanceStatus.CANCELLED, false);
        verifyAllFalseExpectationExceptNonAdvertiser(callable, invoice, FinanceStatus.CLOSED);
        verifyAllFalseExpectationExceptInternal(callable, invoice, FinanceStatus.DUMMY, true);
        verifyAllFalseExpectationExceptInternal(callable, invoice, FinanceStatus.GENERATED, true);
        verifyAllFalseExpectationExceptNonAdvertiser(callable, invoice, FinanceStatus.OPEN);
        verifyAllFalseExpectationExceptInternal(callable, invoice, FinanceStatus.PAID, true);
    }

    @Test
    public void testCanUpdate() throws Exception {
        final Invoice invoice = invoiceTF.create();
        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return invoiceRestrictions.canUpdate(invoice);
            }
        };


        AdvertiserAccount account = advertiserAccountTF.create();
        account.setAccountManager(advertiserManagerAllAccess1.getUser());
        account.setInternalAccount((InternalAccount) createInternalAccount(advertiserManagerAllAccess1));
        advertiserAccountTF.persist(account);
        invoice.setAccount(account);

        verifyAllFalseExpectation(callable, invoice, FinanceStatus.CANCELLED, false);
        verifyAllFalseExpectation(callable, invoice, FinanceStatus.CLOSED, true);
        verifyAllFalseExpectation(callable, invoice, FinanceStatus.DUMMY, true);
        verifyAllFalseExpectationExceptInternal(callable, invoice, FinanceStatus.GENERATED, true);
        verifyAllFalseExpectationExceptInternal(callable, invoice, FinanceStatus.OPEN, true);
        verifyAllFalseExpectation(callable, invoice, FinanceStatus.PAID, true);
    }

    @Test
    public void testCanGenerate() throws Exception {
        final Invoice invoice = invoiceTF.create();
        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return invoiceRestrictions.canGenerate(invoice);
            }
        };

        invoice.setAccount((AdvertiserAccount) advertiserAllAccess1.getUser().getAccount());

        verifyAllFalseExpectation(callable, invoice, FinanceStatus.CANCELLED, false);
        verifyAllFalseExpectation(callable, invoice, FinanceStatus.CLOSED, true);
        verifyAllFalseExpectationExceptInternal(callable, invoice, FinanceStatus.DUMMY, true);
        verifyAllFalseExpectation(callable, invoice, FinanceStatus.GENERATED, true);
        verifyAllFalseExpectation(callable, invoice, FinanceStatus.OPEN, true);
        verifyAllFalseExpectation(callable, invoice, FinanceStatus.PAID, true);
    }

    private void setUpNonAdvFalseExpectations() {
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(ispManagerAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(advertiserManagerNoAccess, false);
    }

    private void setUpAllFalseExpectations() {
        expectResult(internalAllAccess, false);
        expectResult(advertiserManagerAllAccess1, false);

        setUpNonInternalFalseExpectations();
        setUpNonAdvFalseExpectations();
    }

    private void setUpNonInternalFalseExpectations() {
        expectResult(advertiserAllAccess1, false);
        expectResult(agencyAllAccess1, false);

        setUpNonAdvFalseExpectations();
    }

    private void setUpInternalTrueExpectations() {
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
    }

    private Account createInternalAccount(UserDefinition ud) {
        InternalAccount internalAccount = (InternalAccount) ud.getUser().getAccount();
        internalAccount.setFlags(0);
        if (internalAccount.getId() == null) {
            internalAccountTF.persist(internalAccount);
        }
        return internalAccount;
    }

    private void verifyAllFalseExpectation(Callable callable, Invoice invoice, FinanceStatus status, boolean resetAll) {
        if (resetAll) {
            resetExpectationsToDefault();
        }

        invoice.setStatus(status);

        setUpAllFalseExpectations();
        doCheck(callable);
    }

    private void verifyAllFalseExpectationExceptInternal(Callable callable, Invoice invoice, FinanceStatus status, boolean resetAll) {
        if (resetAll) {
            resetExpectationsToDefault();
        }

        invoice.setStatus(status);

        setUpInternalTrueExpectations();
        setUpNonInternalFalseExpectations();
        doCheck(callable);
    }

    private void verifyAllFalseExpectationExceptNonAdvertiser(Callable callable, Invoice invoice, FinanceStatus status) {
        resetExpectationsToDefault();

        invoice.setStatus(status);

        setUpInternalTrueExpectations();
        expectResult(advertiserAllAccess1, true);
        expectResult(agencyAllAccess1, false);
        setUpNonAdvFalseExpectations();

        doCheck(callable);
    }
}
