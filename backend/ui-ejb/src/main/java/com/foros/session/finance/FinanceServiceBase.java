package com.foros.session.finance;

import com.foros.model.account.AccountFinancialSettings;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.account.AccountService;
import com.foros.session.cache.CacheService;
import com.foros.session.security.AuditService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.DecimalUtil;

import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class FinanceServiceBase {
    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    protected AccountService accountService;

    @EJB
    protected AccountRestrictions accountRestrictions;

    @EJB
    protected AuditService auditService;

    @EJB
    protected CacheService cacheService;

    @EJB
    protected DisplayStatusService displayStatusService;

    protected void updateCommissionForZeroOrNull(AccountFinancialSettings settings) {
        if (DecimalUtil.isZeroOrNull(settings.getCommission())) {
            settings.setCommission(BigDecimal.ZERO);
        }
    }
}
