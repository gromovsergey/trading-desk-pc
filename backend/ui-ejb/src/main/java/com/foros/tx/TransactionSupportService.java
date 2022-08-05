package com.foros.tx;

import javax.ejb.Local;

@Local
public interface TransactionSupportService {
    void onTransaction(TransactionCallback callback);
}
