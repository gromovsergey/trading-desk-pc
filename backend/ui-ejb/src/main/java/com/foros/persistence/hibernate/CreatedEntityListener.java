package com.foros.persistence.hibernate;

import com.foros.model.Identifiable;
import com.foros.session.ServiceLocator;
import com.foros.tx.IdentityResetCallback;
import com.foros.tx.TransactionSupportService;
import org.hibernate.HibernateException;
import org.hibernate.event.PersistEvent;
import org.hibernate.event.PersistEventListener;

import java.util.Map;

public class CreatedEntityListener implements PersistEventListener {

    @Override
    public void onPersist(PersistEvent event) throws HibernateException {
        if (event.getObject() instanceof Identifiable) {
            ServiceLocator.getInstance().lookup(TransactionSupportService.class).onTransaction(new IdentityResetCallback((Identifiable) event.getObject()));
        }
    }

    @Override
    public void onPersist(PersistEvent event, Map createdAlready) throws HibernateException {
        onPersist(event);
    }
}
