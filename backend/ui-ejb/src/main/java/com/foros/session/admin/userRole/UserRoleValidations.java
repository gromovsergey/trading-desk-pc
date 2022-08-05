package com.foros.session.admin.userRole;

import com.foros.model.account.InternalAccount;
import com.foros.model.security.InternalAccessType;
import com.foros.model.security.UserRole;
import com.foros.security.AccountRole;
import com.foros.session.BeanValidations;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class UserRoleValidations {

    @EJB
    private BeanValidations beanValidations;
    
    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) UserRole userRole) {
        validateInternalAccessType(context, userRole);
    }
    
    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) UserRole userRole) {
        validateInternalAccessType(context, userRole);
    }

    private void validateInternalAccessType(ValidationContext context, UserRole userRole) {
        if (AccountRole.INTERNAL == userRole.getAccountRole() && userRole.getInternalAccessType() == null) {
            context.addConstraintViolation("errors.field.required")
            .withPath("internalAccessType");
        }

        if (InternalAccessType.MULTIPLE_ACCOUNTS == userRole.getInternalAccessType()) {
            if (userRole.getAccessAccountIds().isEmpty()) {
                context.addConstraintViolation("errors.field.required")
                .withPath("accessAccountIds");

            } else {
                for (Long accountId : userRole.getAccessAccountIds()) {
                    beanValidations.linkValidator(context, InternalAccount.class)
                    .withCheckDeleted(null)
                    .withPath("accessAccountIds")
                    .validate(new InternalAccount(accountId));
                }
            }
        }
    }
}
