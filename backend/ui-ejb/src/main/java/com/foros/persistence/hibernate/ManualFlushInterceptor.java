package com.foros.persistence.hibernate;

import com.foros.util.PersistenceUtils;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.FlushMode;
import org.hibernate.Session;

public class ManualFlushInterceptor {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        Session session = PersistenceUtils.getHibernateSession(em);
        FlushMode oldMode = session.getFlushMode();
        session.setFlushMode(FlushMode.MANUAL);
        try {
            return context.proceed();
        } finally {
            session.setFlushMode(oldMode);
        }
    }
}
