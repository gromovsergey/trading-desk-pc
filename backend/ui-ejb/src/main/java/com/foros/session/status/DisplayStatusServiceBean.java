package com.foros.session.status;

import com.foros.model.Identifiable;
import com.foros.model.account.Account;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.util.PersistenceUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.impl.SessionImpl;

@Stateless(name = "DisplayStatusService")
@Interceptors({PersistenceExceptionInterceptor.class})
public class DisplayStatusServiceBean implements DisplayStatusService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    public void update(Identifiable entity) {
        update(entity, entity.getId());
    }

    @Override
    public void update(Object entity, Long id) {
        updateStatus(entity, id);
    }

    protected void updateStatus(Object entity, Long id) {
        // record to interceptor
        SessionImpl sessionImpl = getHibernateSession();
        DisplayStatusHandler interceptor = getDisplayStatusHandler(sessionImpl);
        interceptor.initialize(jdbcTemplate, sessionImpl);
        interceptor.registerEntity(entity, id);
    }

    @Override
    public void scheduleStatusEvictionOnCampaignCreditChange(Account account) {
        jdbcTemplate.execute("select campaign_util.on_credit_changed(?::int)", account.getId());
        jdbcTemplate.scheduleEviction();
    }

    private SessionImpl getHibernateSession() {
        return (SessionImpl) PersistenceUtils.getHibernateSession(em);
    }

    private DisplayStatusHandler getDisplayStatusHandler(SessionImpl sessionImpl) {
        return ((HibernateInterceptor) sessionImpl.getInterceptor()).getDisplayStatusHibernateInterceptor();
    }
}
