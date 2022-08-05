package com.foros.session.regularchecks;

import com.foros.model.Identifiable;
import com.foros.model.RegularCheckable;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class RegularReviewValidations {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Validation
    public <T extends RegularCheckable & Identifiable> void validateUpdateCheck(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) T entity) {
        RegularCheckable existing = em.find(entity.getClass(), entity.getId());

        // validate interval
        if (context.isReachable("interval")) {
            Integer interval = entity.getInterval();

            if (interval == null) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("interval");
                return;
            }

            if (interval <= 0 || interval > 3) {
                context.addConstraintViolation("errors.field.range")
                        .withParameters(1, 3)
                        .withPath("interval")
                        .withValue(interval);
                return;
            }

            if (existing.getInterval() != null && interval - existing.getInterval() > 1) {
                context.addConstraintViolation("checks.errors.interval")
                        .withPath("interval")
                        .withValue(interval);
            }
        }
    }
}
