package com.foros.validation.util;

import com.foros.model.EntityBase;
import com.foros.model.IdNameEntity;
import com.foros.model.Identifiable;
import com.foros.session.bulk.Operation;
import com.foros.util.UploadUtils;
import com.foros.util.bean.Filter;
import com.foros.validation.ValidationContext;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DuplicateChecker<T> {
    private Filter<T> filter;
    private IdentifierFetcher<T> identifierFetcher;
    private List<Duplicate<T>> duplicates = new LinkedList<Duplicate<T>>();
    private Set<Object> checkedIdentifiers = new HashSet<Object>();
    private String constraintViolationTemplate = "errors.duplicate.name";

    DuplicateChecker(IdentifierFetcher<T> identifierFetcher, Filter<T> filter) {
        this.identifierFetcher = identifierFetcher;
        this.filter = filter;
    }


    public DuplicateChecker<T> withTemplate(String template) {
        constraintViolationTemplate = template;
        return this;
    }

    public void populateIdentifiers(Set<T> entities) {
        for (T entity : entities) {
            checkedIdentifiers.add(identifierFetcher.fetch(entity));
        }
    }

    public boolean check(ValidationContext context, String path, T t) {
        boolean res = check(t);
        if (!res) {
            context.addConstraintViolation(constraintViolationTemplate).withPath(path);
        }
        return res;
    }

    public boolean check(T t) {
        return check(-1, t);
    }

    public static interface IdentifierFetcher<T> {
        Object fetch(T entity);
    }

    private boolean check(int index, T entity) {
        Object identifier = identifierFetcher.fetch(entity);

        if (identifier == null) {
            return true;
        }

        if (checkedIdentifiers.add(identifier)) {
            return true;
        }

        duplicates.add(new Duplicate<T>(index, entity, identifier));
        return false;
    }

    @Deprecated
    public DuplicateChecker<T> check(Collection<? extends T> entities) {
        int i = 0;
        for (T entity : entities) {
            if (filter == null || filter.accept(entity)) {
                check(i, entity);
            }
            i++;
        }
        return this;
    }

    @Deprecated
    public void createConstraintViolations(ValidationContext context, String collectionPathTemplate, String identifierProperty) {
        createConstraintViolations(context, collectionPathTemplate, identifierProperty, "errors.duplicate");
    }

    // Use check()
    @Deprecated
    public void createConstraintViolations(ValidationContext context, String collectionPathTemplate, String identifierProperty, String message) {
        for (Duplicate<T> duplicate : duplicates) {
            String collectionPath = MessageFormat.format(collectionPathTemplate, String.valueOf(duplicate.index));
            context
                    .addConstraintViolation("" + message + "")
                    .withParameters(duplicate.identifier)
                    .withValue(duplicate.identifier)
                    .withPath(collectionPath, identifierProperty);
        }
    }

    @Deprecated
    public void updateUploadStatus(String identifierProperty) {
        for (Duplicate<T> duplicate : duplicates) {
            UploadUtils.getUploadContext((EntityBase) duplicate.entity)
                    .addError(constraintViolationTemplate)
                    .withPath(identifierProperty);
        }
    }

    public static <T> DuplicateChecker<T> create(final Class<T> aClass) {
        return create(new IdentifierFetcher<T>() {

            @Override
            public Object fetch(T val) {
                return aClass.cast(val);
            }
        });
    }

    public static <E extends EntityBase & IdNameEntity> DuplicateChecker<E> createNameDuplicateChecker() {
        return create(new NameFetcher<E>());
    }

    public static <E extends EntityBase & Identifiable> DuplicateChecker<Operation<E>> createOperationDuplicateChecker() {
        return create(new OperationIdFetcher<E>());
    }

    public static <E> DuplicateChecker<E>  create(IdentifierFetcher<E> identifierFetcher) {
        return new DuplicateChecker<E>(identifierFetcher, null);
    }

    public static <E> DuplicateChecker<E>  create(IdentifierFetcher<E> identifierFetcher, Filter<E> filter) {
        return new DuplicateChecker<E>(identifierFetcher, filter);
    }

    public static class NameFetcher<T extends IdNameEntity> implements IdentifierFetcher<T> {
        @Override
        public String fetch(T entity) {
            return entity.getName();
        }
    }

    public static class OperationIdFetcher<T extends EntityBase & Identifiable> implements IdentifierFetcher<Operation<T>> {
        @Override
        public Long fetch(Operation<T> operation) {
            if (operation == null || operation.getEntity() == null) {
                return null;
            }
            return operation.getEntity().getId();
        }
    }

    /** @noinspection unchecked*/
    public <ID> Set<ID> getCheckedIdentifiers() {
        return (Set) checkedIdentifiers;
    }

    private static class Duplicate<T> {
        private int index;
        private T entity;
        private Object identifier;

        private Duplicate(int index, T entity, Object identifier) {
            this.index = index;
            this.entity = entity;
            this.identifier = identifier;
        }
    }

}
