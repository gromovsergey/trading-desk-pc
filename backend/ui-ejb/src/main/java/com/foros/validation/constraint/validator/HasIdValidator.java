package com.foros.validation.constraint.validator;

import com.foros.model.EntityBase;
import com.foros.util.StringUtil;

public class HasIdValidator extends AbstractModesSupportValidator<EntityBase, HasIdValidator> {

    @Override
    protected void validateWithModes(EntityBase value) {
        if ( value != null ) {
            ValidatorUtils.IdInfo idInfo = ValidatorUtils.getEntityId(value.getClass(), value);

            if (isEmpty(idInfo)) {
                addConstraintViolation("errors.field.required")
                        .withPath(path())
                        .withValue(idInfo.getId());
            }
        }
    }

    private boolean isEmpty(ValidatorUtils.IdInfo idInfo) {
        return idInfo.getId() == null
                || (idInfo.getId() instanceof String && StringUtil.isPropertyEmpty((String) idInfo.getId()));
    }

}
