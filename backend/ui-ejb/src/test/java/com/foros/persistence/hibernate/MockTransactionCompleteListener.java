package com.foros.persistence.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.event.EventSource;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.FlushEventListener;

/**
 * This listener needs to use changes functionality on transaction completion event.
 */
public class MockTransactionCompleteListener implements FlushEventListener {
    private boolean processing = false;

    @Override
    public void onFlush(FlushEvent event) throws HibernateException {
        if (processing) {
            return;
        }
        try {
            processing = true;
            EventSource source = event.getSession();
            HibernateInterceptor interceptor = (HibernateInterceptor)source.getInterceptor();
            interceptor.processBeforeTransactionCompletion();
        } finally {
            processing = false;
        }
    }
}
