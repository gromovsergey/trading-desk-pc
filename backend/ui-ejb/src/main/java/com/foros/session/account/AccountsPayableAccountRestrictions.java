package com.foros.session.account;

import com.foros.model.account.AccountsPayableAccountBase;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@LocalBean
@Stateless
@Restrictions
public class AccountsPayableAccountRestrictions {
    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private EntityRestrictions entityRestrictions;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Restriction
    public boolean canUpdateFinance(AccountsPayableAccountBase account) {
        return (accountRestrictions.canUpdate(account) || canUpdateRestrictedFinanceFields(account)) && hasUsers(account);
    }

    @Restriction
    public boolean canUpdateFinanceNoUsers(AccountsPayableAccountBase account) {
        return accountRestrictions.canUpdate(account);
    }

    private boolean hasUsers(AccountsPayableAccountBase account) {
        Query query = em.createQuery("select 1 from User u where u.account.id = :accountId and u.status <> 'D'");
        query.setMaxResults(1);
        query.setParameter("accountId", account.getId());
        return !query.getResultList().isEmpty();
    }

    private boolean canUpdateRestrictedFinanceFields(AccountsPayableAccountBase account) {
        return entityRestrictions.canUpdate(account) && accountRestrictions.isUpdateFinanceGranted(account);
    }
}
