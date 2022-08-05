package com.foros.validation.validator;

import com.foros.validation.ValidationContext;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.validator.RangeValidator;

import com.foros.validation.util.ValidationUtil;

import group.Db;
import group.Validation;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Validation.class })
public class RangeConstraintValidatorTest extends AbstractConstraintValidatorTest {

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @RangeConstraint(min = "7" , max = "88")
    private BigDecimal range;

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @RangeConstraint(min = "7" , max = "7")
    private BigDecimal minEqMax;

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @RangeConstraint(min = "7")
    private BigDecimal less;

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @RangeConstraint(max = "88")
    private BigDecimal greater;

    @Test
    public void testRange() {
        checkViolations(0, validate());

        range = new BigDecimal("7");
        checkViolations(0, validate());

        range = new BigDecimal("88");
        checkViolations(0, validate());

        range = new BigDecimal("6");
        checkViolations(1, validate());

        range = new BigDecimal("89");
        checkViolations(1, validate());
    }

    @Test
    public void testLess() {
        checkViolations(0, validate());

        less = new BigDecimal("7");
        checkViolations(0, validate());


        less = new BigDecimal("6");
        checkViolations(1, validate());
    }

    @Test
    public void testGreater() {
        checkViolations(0, validate());

        greater = new BigDecimal("88");
        checkViolations(0, validate());

        greater = new BigDecimal("89");
        checkViolations(1, validate());
    }

    @Test
    public void testMaxEqMin() {
        checkViolations(0, validate());

        minEqMax = new BigDecimal("7");
        checkViolations(0, validate());

        minEqMax = new BigDecimal("8");
        checkViolations(1, validate());

        minEqMax = new BigDecimal("6");
        checkViolations(1, validate());
    }

    @Test
    public void testCustom() {
        ValidationContext context = ValidationUtil.createContext();

        RangeValidator range = context.validator(RangeValidator.class)
                .withMax(new BigDecimal(7))
                .withMax(new BigDecimal(88));

        range.validate(new BigDecimal(6));
        range.validate(new BigDecimal(89));
    }

}
