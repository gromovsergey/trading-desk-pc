package com.foros.audit.changes;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface DatabaseChangesService {
    Collection<ChangeRecord> readChanges();
    Collection<ChangeRecord> readPersistentChanges();
}
