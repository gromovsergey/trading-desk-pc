package com.foros.session;

import com.foros.session.cache.CacheService;
import com.foros.util.VersionCollisionException;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.OptimisticLockException;

public class PersistenceExceptionInterceptor {
    @EJB
    private CacheService cacheService;

    @AroundInvoke
    public Object intercept(InvocationContext inv) throws Exception {
        try {
            return inv.proceed();
        } catch (OptimisticLockException e) {
            if (e.getEntity() != null) {
                cacheService.evictNonTransactional(e.getEntity());
            }
            throw new VersionCollisionException(e);
        } catch (Exception e) {
            throw PersistenceExceptionHelper.handle(e);
        }
    }

}
