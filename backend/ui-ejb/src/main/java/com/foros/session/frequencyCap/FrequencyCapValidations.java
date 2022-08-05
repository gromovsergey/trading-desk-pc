package com.foros.session.frequencyCap;

import com.foros.model.FrequencyCap;
import com.foros.model.time.TimeSpan;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.validator.RangeValidator;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class FrequencyCapValidations {
    private static final long MAX_SECONDS = Integer.MAX_VALUE;

    @EJB
    private BeansValidationService beanValidationService;

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean FrequencyCap frequencyCap) {
        doValidate(context, frequencyCap);
    }

    @Validation
    public void validateUpdateWDFrequencyCaps(ValidationContext context,
                                              FrequencyCap eventsFrequencyCap,
                                              FrequencyCap categoriesFrequencyCap,
                                              FrequencyCap channelsFrequencyCap) {
        validateWDFrequencyCap(context, eventsFrequencyCap, "eventsFrequencyCap");
        validateWDFrequencyCap(context, categoriesFrequencyCap, "categoriesFrequencyCap");
        validateWDFrequencyCap(context, channelsFrequencyCap, "channelsFrequencyCap");

    }

    private void validateWDFrequencyCap(ValidationContext context, FrequencyCap frequencyCap, String fcFieldName) {
        ValidationContext subContext = context.createSubContext(frequencyCap, fcFieldName);
        beanValidationService.validate(subContext);
        doValidate(subContext, frequencyCap);
    }

    private void doValidate(ValidationContext context, FrequencyCap frequencyCap) {
        validateTimeSpan(context, "periodSpan", frequencyCap.getPeriodSpan());
        validateTimeSpan(context, "windowLengthSpan", frequencyCap.getWindowLengthSpan());

        if (frequencyCap.getWindowLength() != null) {
            if (frequencyCap.getWindowCount() == null) {
                context.addConstraintViolation("errors.required")
                        .withPath("windowCount")
                        .withParameters("{frequency.window.limit}");
            }
        } else {
            if (frequencyCap.getWindowCount() != null) {
                context.addConstraintViolation("errors.required")
                        .withPath("windowLengthSpan")
                        .withParameters("{frequency.window.length}");
            }
        }
    }

    private void validateTimeSpan(ValidationContext context, String property, TimeSpan timeSpan) {
        if (timeSpan == null || timeSpan.getValueInSeconds() == null) {
            return;
        }

        context.validator(RangeValidator.class)
                .withMin(BigDecimal.ONE)
                .withMax(BigDecimal.valueOf(timeSpan.getUnit().convertToUnits(MAX_SECONDS)))
                .withPath(property)
                .validate(timeSpan.getValue());
    }
}
