package com.foros.validation.strategy;

import com.foros.model.EntityBase;
import com.foros.validation.ValidationContext;

public class UpdateValidationStrategy implements ValidationStrategy {

    @Override
    public boolean isReachable(ValidationContext context, String fieldName) {
        Object bean = context.getBean();

        if (bean instanceof EntityBase) {
            EntityBase entityBase = (EntityBase) bean;
            return entityBase.isChanged(fieldName);
        }

        return true;
    }

}
