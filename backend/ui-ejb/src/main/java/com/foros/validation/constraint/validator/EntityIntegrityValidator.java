package com.foros.validation.constraint.validator;

import com.foros.model.EntityBase;

public class EntityIntegrityValidator<T extends EntityBase> extends AbstractValidator<Object, EntityIntegrityValidator<T>> {
    protected Class<T> clazz;

    public EntityIntegrityValidator(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void validateValue(Object entity) {
        if (entity == null) {
            addConstraintViolation("errors.field.required");
            return;
        }
        if (!clazz.isAssignableFrom(entity.getClass())) {
            addConstraintViolation("errors.invalidInput");
        }
    }
}
