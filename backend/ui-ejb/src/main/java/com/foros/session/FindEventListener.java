package com.foros.session;

import org.hibernate.event.LoadEventListener;
import org.hibernate.event.LoadEvent;
import org.hibernate.HibernateException;

/**
 * Author: Boris Vanin
 */
public class FindEventListener implements LoadEventListener  {

    public void onLoad(LoadEvent loadEvent, LoadType loadType) throws HibernateException {
        Class<?> c = findClass(loadEvent.getEntityClassName());
        Object result = loadEvent.getResult();
        if (result != null && !c.isAssignableFrom(result.getClass())) {
            loadEvent.setResult(null);
        }
    }

    private Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new HibernateException(e);
        }
    }

}
