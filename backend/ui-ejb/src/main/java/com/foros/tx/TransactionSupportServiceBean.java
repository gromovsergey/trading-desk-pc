package com.foros.tx;

import com.foros.util.command.HibernateWork;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless(name = "TransactionSupportService")
public class TransactionSupportServiceBean implements TransactionSupportService {
    private static final Logger logger = Logger.getLogger(TransactionSupportServiceBean.class.getName());
    protected static final ConcurrentMap<Transaction, Set<TransactionCallback>> CALLBACKS_PER_TX = new ConcurrentHashMap<Transaction, Set<TransactionCallback>>();

    @EJB
    private HibernateWorkExecutorService executorService;

    @Override
    public void onTransaction(TransactionCallback callback) {
        Transaction tx = getTransaction();

        if (tx != null) {
            Set<TransactionCallback> callbacks = CALLBACKS_PER_TX.get(tx);

            if (callbacks == null) {
                callbacks = new LinkedHashSet<TransactionCallback>();

                CALLBACKS_PER_TX.putIfAbsent(tx, callbacks);

                tx.registerSynchronization(new SynchronizationHandler(tx));
                callbacks.add(callback);
                logger.log(Level.FINE, "Registered transaction and first handler:" + tx.hashCode());
            } else {
                callbacks.add(callback);
                logger.log(Level.FINE, "Registered callback in transaction: " + tx.hashCode() + ". Active callback size:" + callbacks.size());
            }
        }
    }


    protected Transaction getTransaction() {
        return executorService.execute(new HibernateWork<Transaction>() {
                @Override
                public Transaction execute(Session session) {
                    return session.getTransaction();
                }
            });
    }

    private static class SynchronizationHandler implements Synchronization {
        private Transaction tx;

        public SynchronizationHandler(Transaction tx) {
            this.tx = tx;
        }

        @Override
        public void beforeCompletion() {
            for (TransactionCallback callback : CALLBACKS_PER_TX.get(tx)) {
                callback.onBeforeCommit();
            }
        }

        @Override
        public void afterCompletion(int status) {
            try {
                switch (status) {
                    case Status.STATUS_ROLLEDBACK:
                        for (TransactionCallback callback : CALLBACKS_PER_TX.get(tx)) {
                            callback.onRollback();
                        }
                        break;
                    case Status.STATUS_COMMITTED:
                        for (TransactionCallback callback : CALLBACKS_PER_TX.get(tx)) {
                            callback.onCommit();
                        }
                        break;
                }
            } finally {
                CALLBACKS_PER_TX.remove(tx);
            }
            logger.log(Level.FINE, "Executed callbacks for transaction:" + tx.hashCode());
        }
    }
}
