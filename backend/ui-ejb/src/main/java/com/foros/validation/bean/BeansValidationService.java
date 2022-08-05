package com.foros.validation.bean;

import com.foros.validation.ValidationContext;
import javax.ejb.Local;

@Local
/**
 * Bean validation service
 */
public interface BeansValidationService {

    /**
     * <p>Validate validation context value <code>context.getBean()</code> using validation aspects.
     * See {@link com.foros.validation.annotation.Validate} and {@link com.foros.validation.annotation.Validator}
     * annotations</p>
     *
     * @param context context for validation and source of validating bean
     */
    void validate(ValidationContext context);

}
