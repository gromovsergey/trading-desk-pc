package com.foros.persistence.hibernate;

import com.foros.cache.CacheHelper;
import com.foros.util.VersionCollisionException;

import org.hibernate.HibernateException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.def.DefaultFlushEventListener;

public class ExceptionManagementFlushEventListener extends DefaultFlushEventListener {

    @Override
    public void onFlush(FlushEvent event) throws HibernateException {
        try {
            super.onFlush(event);
        } catch (StaleObjectStateException e) {
            CacheHelper.evict(e.getIdentifier(), e.getEntityName(), false);
            throw new VersionCollisionException(e);
        } catch (HibernateException e) {
            throw e;
        }
    }
}
