package com.foros.validation;

import com.foros.validation.strategy.ValidationStrategy;
import java.lang.reflect.Method;
import javax.ejb.Local;

@Local
/**
 * Main validation functionality
 */
public interface ValidationService {

    /**
     * <p>Validate in new context using validation aspect <code>validationName</code> with passes parameters</p>
     *
     * @param validationName validation aspect name
     * @param params parameters for validation
     * @return validation results
     * @throws com.foros.validation.ValidationException if validation can not be processed
     */
    ValidationContext validate(String validationName, Object... params);

    /**
     * <p>Validate in new context using validation aspect <code>validationName</code>
     * with passes parameters and additional reachable rules</p>
     *
     * @param additionalStrategy additional reachable rules
     * @param validationName validation aspect name
     * @param params parameters for validation
     * @return validation results
     * @throws com.foros.validation.ValidationException if validation can not be processed
     */
    ValidationContext validate(ValidationStrategy additionalStrategy, String validationName, Object... params);

    /**
     * <p>Same that {@link ValidationService#validate(String, Object...)}, but in new transaction.</p>
     */
    ValidationContext validateInNewTransaction(String validationName, Object... params);

    /**
     * <p>Validate method parameters. See {@link com.foros.validation.bean.BeansValidationService}
     * for more bean validation information.</p>
     *
     * @param method method
     * @param params method parameters
     * @return validation results
     * @throws com.foros.validation.ValidationException if validation can not be processed
     */
    ValidationContext validateParameters(Method method, Object[] params);

    /**
     * <p>Validate in with context using validation aspect <code>validationName</code> with passes parameters</p>
     *
     * @param context context for validation
     * @param validationName validation aspect name
     * @param params parameters for validation
     * @throws com.foros.validation.ValidationException if validation can not be processed
     */
    void validateWithContext(ValidationContext context, String validationName, Object... params);

}
