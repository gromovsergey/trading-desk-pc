package com.foros.session.admin.fraudConditions;

import static com.foros.model.admin.FraudCondition.CONDITIONS_MAX_COUNT;
import static com.foros.model.admin.FraudCondition.LIMIT_MAXIMUM_VALUE;
import static com.foros.model.admin.FraudCondition.LIMIT_MINIMUM_VALUE;
import static com.foros.model.admin.FraudCondition.PERIOD_MAXIMUM_VALUE;
import static com.foros.model.admin.FraudCondition.PERIOD_MINIMUM_VALUE;
import static com.foros.model.admin.FraudCondition.USER_INACTIVITY_TIMEOUT_MAXIMUM;
import static com.foros.model.admin.FraudCondition.USER_INACTIVITY_TIMEOUT_MINIMUM;

import com.foros.model.admin.FraudCondition;
import com.foros.model.admin.GlobalParam;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;
import org.apache.commons.lang.builder.EqualsBuilder;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@LocalBean
@Stateless
@Validations
public class FraudConditionValidations {
    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    private FraudConditionsService fraudConditionsService;

    @EJB
    private ValidationService validationService;

    @Validation
    public void validateUpdate(
            ValidationContext validationContext,
            @ValidateBean(ValidationMode.UPDATE) GlobalParam userInactivityTimeout,
            List<FraudCondition> fraudConditions) {

        ValidationContext context = validationContext.createSubContext(userInactivityTimeout);

        validateConditionsCount(context, fraudConditions);
        validateUserInactivityTimeout(context, userInactivityTimeout);

        List<FraudCondition> existingFraudConditions = fraudConditionsService.findAll();

        int index = 0;
        for (FraudCondition fc : fraudConditions) {
            ValidationContext subContext = context.createSubContext(fc, "fraudConditions", index++);
            String validationName = getValidationName(fc, existingFraudConditions);
            if (validationName != null) {
                validationService.validateWithContext(subContext, validationName, fc);
                validationService.validateWithContext(subContext, "FraudCondition.duplicate", fc, fraudConditions.subList(0, index));
            }
        }
    }

    // todo use generic bean validation should be used then message customization will be available
    @Validation
    public void validateUpdate(ValidationContext context, FraudCondition fraudCondition) {
        validateFraudCondition(context, fraudCondition);
    }

    @Validation
    public void validateCreate(ValidationContext context, FraudCondition fraudCondition) {
        validateFraudCondition(context, fraudCondition);
    }

    @Validation
    public void validateDuplicate(ValidationContext context, FraudCondition currentFc, List<FraudCondition> fraudConditions) {
        for (FraudCondition fc : fraudConditions) {
            if (currentFc != fc) { // do not compare with itself
                if (new EqualsBuilder()
                        .append(currentFc.getLimit(), fc.getLimit())
                        .append(currentFc.getPeriod(), fc.getPeriod())
                        .append(currentFc.getType(), fc.getType())
                        .isEquals()) {
                    context.addConstraintViolation("fraud.error.duplicateCondition").withPath("duplicate");
                    break;
                }
            }
        }
    }

    private void validateConditionsCount(ValidationContext context, List<FraudCondition> fraudConditions) {
        if (fraudConditions.size() > CONDITIONS_MAX_COUNT) {
            context.addConstraintViolation("fraud.error.conditionMaximum");
        }
    }

    private void validateUserInactivityTimeout(ValidationContext context, GlobalParam userInactivityTimeOut) {
        if (userInactivityTimeOut.getValue() == null) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("userInactivityTimeout");
            return;
        }
        long timeout = Long.valueOf(userInactivityTimeOut.getValue());

        if (timeout < USER_INACTIVITY_TIMEOUT_MINIMUM) {
            context.addConstraintViolation("errors.field.greater")
                    .withPath("userInactivityTimeout")
                    .withParameters(String.valueOf(USER_INACTIVITY_TIMEOUT_MINIMUM / 60) + " " + StringUtil.getLocalizedString("interval.minutes"));
            return;
        }
        if (timeout > USER_INACTIVITY_TIMEOUT_MAXIMUM) {
            context.addConstraintViolation("errors.field.notgreater")
                    .withPath("userInactivityTimeout")
                    .withParameters(String.valueOf(USER_INACTIVITY_TIMEOUT_MAXIMUM / 60) + " " + StringUtil.getLocalizedString("interval.minutes"));
        }
    }

    private void validateFraudCondition(ValidationContext context, FraudCondition fraudCondition) {
        if (context.props("period").reachableAndNoViolations()) {
            validatePeriod(context, fraudCondition);
        }
        if (context.props("limit").reachableAndNoViolations()) {
            validateLimit(context, fraudCondition);
        }
    }

    private void validateLimit(ValidationContext context, FraudCondition fraudCondition) {
        if (fraudCondition.getLimit() == null) {
            context.addConstraintViolation("errors.required")
                    .withPath("limit")
                    .withParameters("{fraud.limit}");
            return;
        }
        if (fraudCondition.getLimit() < LIMIT_MINIMUM_VALUE) {
            context.addConstraintViolation("errors.greater")
                    .withPath("limit")
                    .withParameters("{fraud.limit}", String.valueOf(LIMIT_MINIMUM_VALUE - 1));
            return;
        }
        if (fraudCondition.getLimit() > LIMIT_MAXIMUM_VALUE) {
            context.addConstraintViolation("errors.notgreater")
                    .withPath("limit")
                    .withParameters("{fraud.limit}", String.valueOf(LIMIT_MAXIMUM_VALUE));
        }
    }

    private void validatePeriod(ValidationContext context, FraudCondition fraudCondition) {
        if (fraudCondition.getPeriod() == null) {
            context.addConstraintViolation("errors.required")
                    .withPath("period")
                    .withParameters("{fraud.period}");
            return;
        }

        if (fraudCondition.getPeriod() < PERIOD_MINIMUM_VALUE) {
            context.addConstraintViolation("errors.greater")
                    .withPath("period")
                    .withParameters("{fraud.period}", String.valueOf(PERIOD_MINIMUM_VALUE - 1));
            return;
        }

        if (fraudCondition.getPeriod() > PERIOD_MAXIMUM_VALUE) {
            context.addConstraintViolation("errors.notgreater")
                    .withPath("period")
                    .withParameters("{fraud.period}", String.valueOf(PERIOD_MAXIMUM_VALUE / 60 / 60) + " " + StringUtil.getLocalizedString("interval.hours"));
        }
    }

    private String getValidationName(FraudCondition fraudCondition, List<FraudCondition> existingFraudConditions) {
        if (fraudCondition.getId() == null || fraudCondition.getId() == 0) {
            return "FraudCondition.create";
        }

        for (FraudCondition existingFc : existingFraudConditions) {
            if (existingFc.getId().equals(fraudCondition.getId())) {
                return "FraudCondition.update";
            }
        }
        return null;
    }
}
