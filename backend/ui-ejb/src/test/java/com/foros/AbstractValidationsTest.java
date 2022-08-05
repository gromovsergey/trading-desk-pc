package com.foros;

import com.foros.model.EntityBase;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.ValidationUtil;

import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.commons.lang3.ObjectUtils;

public abstract class AbstractValidationsTest extends AbstractServiceBeanIntegrationTest {
    @EJB
    protected ValidationService validationService;

    protected Set<ConstraintViolation> violations;

    protected void validate(String validationName, Object... params) {
        violations = new HashSet<>(
                validationService.validate(validationName, params).getConstraintViolations()
        );
    }

    protected void assertHasNoViolations() {
        assertViolationsCount(0);
    }

    protected void assertHasNoViolation(String path) {
        assertFalse(hasViolation(path));
    }

    protected void assertHasViolation(String ... paths) {
        boolean hasError = false;
        for (String path : paths) {
            if (!hasViolation(path)) {
                hasError = true;
            }
        }

        if (hasError) {
            System.out.println("Found violations:");
            for (ConstraintViolation violation : violations) {
                System.out.println("  " + violation.getMessage() + " / " + violation.getPropertyPath().toString());
            }

            System.out.println("Expected violations:");
            for (String path : paths) {
                System.out.println("  " + path);
            }

            fail();
        }
    }

    private boolean hasViolation(String path) {
        boolean found = false;

        for (ConstraintViolation violation : violations) {
            if (ObjectUtils.equals(path, violation.getPropertyPath().toString())) {
                found = true;
            }
        }
        return found;
    }

    protected void assertViolationsCount(int count) {
        if (count != violations.size()) {
            System.out.println("Expected " + count + " but was " + violations.size());
            for (ConstraintViolation violation : violations) {
                System.out.println(violation.getPropertyPath() + " : " + violation.getMessage());
            }
            fail();
        }
    }

    protected ValidationContext createContext() {
        return ValidationUtil.validationContext().build();
    }

    protected ValidationContext createContext(EntityBase root) {
        return ValidationUtil.validationContext(root).build();
    }

    protected ValidationContext createContext(EntityBase root, ValidationMode validationMode) {
        return ValidationUtil.validationContext(root).withMode(validationMode).build();
    }

    protected ValidationContext createUpdateContext(EntityBase root) {
        return createContext(root, ValidationMode.UPDATE);
    }

    protected ValidationContext createCreateContext(EntityBase root) {
        return createContext(root, ValidationMode.CREATE);
    }
}
