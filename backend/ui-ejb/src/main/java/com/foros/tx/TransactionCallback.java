package com.foros.tx;

public interface TransactionCallback {
    void onBeforeCommit();

    void onCommit();

    void onRollback();
}
