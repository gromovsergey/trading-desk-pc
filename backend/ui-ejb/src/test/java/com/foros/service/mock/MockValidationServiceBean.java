package com.foros.service.mock;

import com.foros.session.LoggingJdbcTemplate;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.ValidationServiceBean;
import com.foros.validation.strategy.ValidationStrategy;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;

public class MockValidationServiceBean implements ValidationService {

    private static final String SAVE_POINT = "SAVE_POINT";
    @Autowired
    private ValidationServiceBean validationService;

    @Autowired
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    public ValidationContext validate(String validationName, Object... params) {
        return validationService.validate(validationName, params);
    }

    @Override
    public ValidationContext validate(ValidationStrategy additionalStrategy, String validationName, Object... params) {
        return validationService.validate(additionalStrategy, validationName, params);
    }

    @Override
    public ValidationContext validateInNewTransaction(String validationName, Object... params) {
        jdbcTemplate.execute("rollback to " + SAVE_POINT);
        return validationService.validateInNewTransaction(validationName, params);
    }

    @Override
    public ValidationContext validateParameters(Method method, Object[] params) {
        return validationService.validateParameters(method, params);
    }

    @Override
    public void validateWithContext(ValidationContext context, String validationName, Object... params) {
        validationService.validateWithContext(context, validationName, params);

    }

    public void savePoint() {
        jdbcTemplate.execute("savepoint " + SAVE_POINT);
    }

}
