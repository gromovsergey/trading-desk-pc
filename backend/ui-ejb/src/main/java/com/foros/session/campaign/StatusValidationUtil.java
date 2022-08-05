package com.foros.session.campaign;

import com.foros.model.EntityBase;
import com.foros.model.Status;
import com.foros.model.security.Statusable;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.validation.ValidationContext;

public class StatusValidationUtil {
    public static boolean validateStatus(ValidationContext context, Statusable entity, Statusable existing) {
        if (context.isReachable("status") && ((EntityBase)entity).isChanged("status")) {
            if (isInvalid(context, entity, existing)) {
                context.addConstraintViolation("errors.field.invalid")
                    .withPath("status")
                    .withValue(entity.getStatus());
                return false;
            }
        }
        return true;
    }

    private static boolean isInvalid(ValidationContext context, Statusable entity, Statusable existing) {
        if (!context.isReachable("status") || !((EntityBase) entity).isChanged("status")) {
            return false;
        }

        if (entity.getStatus() != Status.PENDING) {
            return false;
        }

        if (SecurityContext.isInternal()) {
            return false;
        }

        if (existing != null && existing.getStatus() == Status.PENDING) {
            return false;
        }

        return  true;
    }
}
