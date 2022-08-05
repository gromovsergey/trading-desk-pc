package com.foros.session.admin.fraudConditions;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.admin.FraudCondition;
import com.foros.model.admin.GlobalParam;
import com.foros.test.factory.FraudConditionTestFactory;
import com.foros.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.foros.validation.constraint.violation.ConstraintViolation;
import java.util.Collection;
import java.util.List;

import group.Db;
import group.Validation;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Validation.class })
public class FraudConditionServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private FraudConditionsService fraudConditionsService;

    @Autowired
    private FraudConditionTestFactory fraudConditionTestFactory;

    @Autowired
    private ValidationService validationService;

    @Test
    public void testFindAll() {
        fraudConditionTestFactory.createConditions(3);

        List<FraudCondition> all = fraudConditionsService.findAll();
        assertEquals("JDBC query must show the same number of FraudCondition",
                     jdbcTemplate.queryForInt("SELECT COUNT(0) FROM FRAUDCONDITION"),
                     all.size());
    }

    @Test
    public void testUpdate() {
        GlobalParam userInactivityTimeout = fraudConditionsService.getUserInactivityTimeout();
        List<FraudCondition> conditions = fraudConditionTestFactory.createConditions(2);

        validationService.validate("FraudCondition.update", userInactivityTimeout, conditions);
        fraudConditionsService.update(userInactivityTimeout, conditions);
    }

    @Test
    public void testUpdateValidation() {
        GlobalParam userInactivityTimeout = fraudConditionsService.getUserInactivityTimeout();
        List<FraudCondition> conditions = fraudConditionTestFactory.createConditions(2);

        conditions.get(0).setLimit(-1L);
        conditions.get(1).setPeriod(100000L);

        Collection<ConstraintViolation> result  =
                validationService.validate("FraudCondition.update", userInactivityTimeout, conditions).getConstraintViolations();
        assertFalse(result.isEmpty());
    }

    @Test
    public void testValidation() {
        FraudCondition fc = fraudConditionTestFactory.createPersistent();
        fc.setLimit(100000L);
        assertFailedValidation("FraudCondition.update", fc);

        fc = fraudConditionTestFactory.createPersistent();
        fc.setLimit(0L);
        assertFailedValidation("FraudCondition.update", fc);

        fc = fraudConditionTestFactory.createPersistent();
        fc.setPeriod(0L);
        assertFailedValidation("FraudCondition.update", fc);

        fc = fraudConditionTestFactory.createPersistent();
        fc.setPeriod(100000L);
        assertFailedValidation("FraudCondition.update", fc);
    }

    @Test
    public void testDuplicatesValidation() {
        GlobalParam userInactivityTimeout = fraudConditionsService.getUserInactivityTimeout();
        List<FraudCondition> conditions = fraudConditionTestFactory.createConditions(3);
        conditions.get(2).setLimit(conditions.get(0).getLimit());
        conditions.get(2).setPeriod(conditions.get(0).getPeriod());
        conditions.get(2).setType(conditions.get(0).getType());

        Collection<ConstraintViolation> result  =
                validationService.validate("FraudCondition.update", userInactivityTimeout, conditions).getConstraintViolations();
        assertFalse(result.isEmpty());
    }

    private void assertFailedValidation(String validationName, FraudCondition... fraudConditions) {
        Collection<ConstraintViolation> result  =
                validationService.validate(validationName, fraudConditions).getConstraintViolations();
        assertFalse("FraudCondition validation '" + validationName + "'should have been fired", result.isEmpty());
    }
}
