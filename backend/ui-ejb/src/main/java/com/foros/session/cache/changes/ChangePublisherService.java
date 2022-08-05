package com.foros.session.cache.changes;

import com.foros.audit.changes.ChangeRecord;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface ChangePublisherService {
    void publishChanges(Collection<ChangeRecord> changes);
}
