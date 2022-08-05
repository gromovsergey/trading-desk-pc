package com.foros.util.command.executor;

import com.foros.util.PersistenceUtils;
import com.foros.util.command.HibernateWork;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.ExcludeDefaultInterceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ExcludeDefaultInterceptors
@Stateless(name = "HibernateWorkExecutorService")
public class HibernateWorkExecutorServiceBean implements HibernateWorkExecutorService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T> T execute(HibernateWork<T> work) {
        return executeImpl(work);
    }

    private <T> T executeImpl(HibernateWork<T> work) {
        return PersistenceUtils.execute(PersistenceUtils.getHibernateSession(em), work);
    }
}
