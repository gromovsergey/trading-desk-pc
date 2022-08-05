package com.foros.session.campaign.bulk;

import com.foros.model.IdNameEntity;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.util.bean.Filter;

import java.util.HashSet;
import java.util.Set;

public class NameUniquenessFilter<T extends StatusEntityBase & IdNameEntity> implements Filter<Operation<T>> {
    private Set<T> ignored = new HashSet<T>();
    @Override
    public boolean accept(Operation<T> operation) {
        T entity = operation.getEntity();
        if (entity == null) {
            return false;
        }

        if (entity.getStatus() == Status.DELETED) {
            return false;
        }

        if (ignored.contains(entity)) {
            return false;
        }

        return (operation.getOperationType() == OperationType.CREATE || entity.isChanged("name")) && entity.getName() != null;
    }

    public void ignore(T t) {
        ignored.add(t);
    }
}
