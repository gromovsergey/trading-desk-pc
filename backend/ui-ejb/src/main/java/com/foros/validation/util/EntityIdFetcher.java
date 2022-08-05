package com.foros.validation.util;

import com.foros.model.Identifiable;

public class EntityIdFetcher<T extends Identifiable> implements DuplicateChecker.IdentifierFetcher<T> {
    @Override
    public Object fetch(T entity) {
        return entity.getId();
    }
}
