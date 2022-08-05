package com.foros.cache.generic.interceptor;

import com.foros.cache.generic.Cache;
import com.foros.cache.generic.CacheProviderService;
import com.foros.persistence.hibernate.EvictCacheHibernateInterceptor;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.util.command.executor.HibernateWorkExecutorService;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;
import org.hibernate.Session;

public class CacheInterceptor {

    @EJB
    private CacheProviderService cacheProviderService;

    @EJB
    private HibernateWorkExecutorService executorService;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @AroundInvoke
    public Object invoke(InvocationContext context) throws Exception {
        if (transactionSynchronizationRegistry.getTransactionStatus() != Status.STATUS_NO_TRANSACTION) {
            Cache cache = cacheProviderService.getCache();
            initCacheEvictHibernateInterceptor(cache);
        }
        return context.proceed();
    }

    private void initCacheEvictHibernateInterceptor(final Cache cache) {
        executorService.execute(new HibernateInterceptor.AbstractHibernateInterceptorWork() {
            @Override
            protected void process(Session session, HibernateInterceptor interceptor) {
                EvictCacheHibernateInterceptor evictCacheInterceptor = interceptor.getEvictCacheInterceptor();
                if (!evictCacheInterceptor.isInitialized()) {
                    evictCacheInterceptor.initialize(session, cache);
                }
            }
        });
    }

}
