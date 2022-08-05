package com.foros.changes;

import com.foros.audit.serialize.AuditChange;

import javax.ejb.Local;

/**
 * Author: Boris Vanin
 */
@Local
public interface ChangesService {

    /**
     * Initializes changes inspection mechanism for current transaction.
     * If method will be called twice, second call will be ignored.
     */
    void initialize();

    void addChange(AuditChange auditChange);

    void handleChanges();
}
