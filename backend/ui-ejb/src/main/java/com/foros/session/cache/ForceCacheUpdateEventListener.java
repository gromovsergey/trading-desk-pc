package com.foros.session.cache;

import com.foros.persistence.hibernate.EvictCacheHibernateInterceptor;
import com.foros.util.PersistenceUtils;

import org.hibernate.HibernateException;
import org.hibernate.event.EventSource;
import org.hibernate.event.LockEvent;
import org.hibernate.event.LockEventListener;

/**
 * Author: Boris Vanin
 */
public class ForceCacheUpdateEventListener implements LockEventListener {

    public void onLock(LockEvent event) throws HibernateException {
        EventSource session = event.getSession();
        Object object = event.getObject();

        HibernateCacheHelper helper = new HibernateCacheHelper(session, event.getLockMode(), object);

        if (helper.needCacheUpdate()) {
            helper.update();
        }

        EvictCacheHibernateInterceptor interceptor = PersistenceUtils.getInterceptor(session).getEvictCacheInterceptor();
        interceptor.touchTagByEntity(object);
    }


}
