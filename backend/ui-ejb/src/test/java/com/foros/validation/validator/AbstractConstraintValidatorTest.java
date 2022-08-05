package com.foros.validation.validator;

import com.foros.AbstractServiceBeanIntegrationTest;

import com.foros.validation.ValidationContext;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.util.ValidationUtil;
import java.util.Set;
import com.foros.validation.constraint.violation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractConstraintValidatorTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private BeansValidationService beansValidationService;

    protected void checkViolations(int size, Set<ConstraintViolation> violations) {
        for (ConstraintViolation violation : violations) {
            assertNotNull(violation.getMessage());
            assertFalse(violation.getMessage().isEmpty());
        }
        assertEquals(size, violations.size());
    }

    protected Set<ConstraintViolation> validate() {
        ValidationContext validationContext = ValidationUtil.validationContext(this).build();
        beansValidationService.validate(validationContext);
        return validationContext.getConstraintViolations();
    }
}
