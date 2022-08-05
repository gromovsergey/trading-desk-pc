package com.foros.validation.validator;

import com.foros.validation.constraint.FractionDigitsConstraint;

import group.Db;
import group.Validation;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Validation.class })
public class FractionDigitsConstraintValidatorTest extends AbstractConstraintValidatorTest {

    @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
    @FractionDigitsConstraint(2)
    private BigDecimal digits;

    @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
    @FractionDigitsConstraint(0)
    private BigDecimal digitsZero;

    @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
    @FractionDigitsConstraint(-2)
    private BigDecimal digitsNegative;

    @Test
    public void test() {
        checkViolations(0, validate());

        digits = new BigDecimal("1230");
        checkViolations(0, validate());

        digits = new BigDecimal("1234.12");
        checkViolations(0, validate());

        digits = new BigDecimal("1234.1200");
        checkViolations(0, validate());

        digits = new BigDecimal("1234.123");
        checkViolations(1, validate());
    }

    @Test
    public void testZero() {
        digitsZero = new BigDecimal("10");
        checkViolations(0, validate());

        digitsZero = new BigDecimal("1");
        checkViolations(0, validate());

        digitsZero = new BigDecimal("1.1");
        checkViolations(1, validate());
    }

    @Test
    public void testNegative() {
        digitsNegative = new BigDecimal("100");
        checkViolations(0, validate());

        digitsNegative = new BigDecimal("1000");
        checkViolations(0, validate());

        digitsNegative = new BigDecimal("10");
        checkViolations(1, validate());

        digitsNegative = new BigDecimal("10.33");
        checkViolations(1, validate());
    }

}
