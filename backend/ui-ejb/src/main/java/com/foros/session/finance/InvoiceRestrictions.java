package com.foros.session.finance;

import com.foros.model.finance.FinanceStatus;
import com.foros.model.finance.Invoice;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class InvoiceRestrictions {
    @EJB
    private AccountRestrictions accountRestrictions;
    
    @EJB
    private EntityRestrictions entityRestrictions;
    
    @EJB
    private CurrentUserService currentUserService;
    
    @Restriction
    public boolean canView(Invoice invoice) {
        if (!accountRestrictions.canView(invoice.getAccount())) {
            return false;
        }
        
        if (currentUserService.isExternal()) {
            return invoice.getStatus() == FinanceStatus.OPEN || invoice.getStatus() == FinanceStatus.CLOSED;
        }
        
        return true;
    }

    @Restriction
    public boolean canUpdate(Invoice invoice) {
        if (invoice.getStatus() != FinanceStatus.GENERATED && invoice.getStatus() != FinanceStatus.OPEN) {
            return false;
        }
        
        return entityRestrictions.canUpdate(invoice.getAccount()) && accountRestrictions.isUpdateFinanceGranted(invoice.getAccount()) && currentUserService.isInternal();
    }

    @Restriction
    public boolean canGenerate(Invoice invoice) {
        if (invoice.getStatus() != FinanceStatus.DUMMY) {
            return false;
        }
        
        return accountRestrictions.canUpdate(invoice.getAccount()) && accountRestrictions.isUpdateFinanceGranted(invoice.getAccount()) && currentUserService.isInternal();
    }
}
