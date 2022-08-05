package com.foros.validation.constraint.validator;

import com.foros.model.EntityBase;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.validation.ValidationException;

import org.apache.commons.lang.ObjectUtils;

public class LinkValidator<E extends EntityBase> extends AbstractPersistentContextSupportValidator<E, LinkValidator<E>> {

    private E entity = null;
    private E existing = null;

    private Class<E> entityClass;

    private boolean required;
    private boolean checkDeleted;

    public LinkValidator<E> withClass(Class<E> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    public LinkValidator<E> withRequired(boolean required) {
        this.required = required;
        return this;
    }

    public LinkValidator<E> withCheckDeleted(E existing) {
        this.existing = existing;
        this.checkDeleted = true;
        return this;
    }

    @Override
    protected void validateContext() {
        super.validateContext();

        if (entityClass == null) {
            throw new ValidationException("Link validator without entity class!!");
        }
    }

    @Override
    protected void validateValue(E identifiable) {
        ValidatorUtils.IdInfo idInfo = ValidatorUtils.getEntityId(entityClass, identifiable);

        if (identifiable == null) {
            if (required) {
                addConstraintViolation("errors.field.required")
                    .withValue(null);
            }
        } else if (idInfo.getId() == null) {
            addConstraintViolation("errors.field.required")
                .withValue(null)
                .withPath(path(), idInfo.getFieldName());
        } else {
            if (entityClass == null) {
                entityClass = (Class<E>) identifiable.getClass();
            }
            entity = em().find(entityClass, idInfo.getId());
            if (entity == null) {
                addConstraintViolation("errors.entity.notFound")
                    .withValue(idInfo.getId())
                    .withPath(path(), idInfo.getFieldName());
            } else {
                if (checkDeleted) {
                    if (entity instanceof StatusEntityBase) {
                        Status status = ((StatusEntityBase) entity).getStatus();
                        if (status == Status.DELETED && !ObjectUtils.equals(existing, entity)) {
                            addConstraintViolation("errors.entity.deleted")
                                .withValue(identifiable);
                        }
                    } else {
                        throw new ValidationException("Can't check deleted for entity without status!");
                    }

                }
            }
        }
    }

    public E getEntity() {
        return entity;
    }

    @SuppressWarnings({ "unchecked", "UnusedParameters" })
    public static <P extends EntityBase> Class<LinkValidator<P>> forClass(Class<P> fakeParam) {
        Class clazz = LinkValidator.class;
        return (Class<LinkValidator<P>>) clazz;
    }

}
