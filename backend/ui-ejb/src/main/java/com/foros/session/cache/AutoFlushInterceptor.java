package com.foros.session.cache;

import com.foros.util.command.HibernateWork;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class AutoFlushInterceptor {
    @EJB
    private HibernateWorkExecutorService executorService;

    @AroundInvoke
    public Object someInterceptMethod(InvocationContext inv) throws Exception {
        Object result = inv.proceed();
        executorService.execute(new FlushHibernateWork());
        return result;
    }

    // Flush data if it is needed
    private static class FlushHibernateWork implements HibernateWork<Void> {
        @Override
        public Void execute(Session session) {
            try {
                if (session.isDirty()) {
                    session.flush();
                }
            } catch (HibernateException e) {
                throw new PersistenceException(e);
            }

            return null;
        }
    }
}
