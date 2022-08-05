package com.foros.tx;

import java.util.Set;

public class TransactionSupportServiceMock extends TransactionSupportServiceBean {

    public void doCommit() {
        Set<TransactionCallback> transactionCallbacks = CALLBACKS_PER_TX.get(getTransaction());

        if  (transactionCallbacks == null) return;

        for (TransactionCallback transactionCallback : transactionCallbacks) {
            transactionCallback.onCommit();
        }

        transactionCallbacks.clear();
    }

    public void doBeforeCompletion() {
        Set<TransactionCallback> transactionCallbacks = CALLBACKS_PER_TX.get(getTransaction());

        if  (transactionCallbacks == null) return;

        for (TransactionCallback callback : transactionCallbacks) {
            callback.onBeforeCommit();
        }
    }
}
