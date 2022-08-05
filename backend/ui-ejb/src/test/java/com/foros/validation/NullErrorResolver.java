package com.foros.validation;

import com.foros.validation.code.BusinessErrors;
import com.foros.validation.code.ForosError;
import com.foros.validation.interpolator.ForosErrorResolver;
import com.foros.validation.interpolator.MessageTemplate;

public class NullErrorResolver implements ForosErrorResolver {
    public static final ForosErrorResolver INSTANCE = new NullErrorResolver();

    @Override
    public ForosError resolve(MessageTemplate template) {
        return BusinessErrors.GENERAL_ERROR;
    }

    @Override
    public ForosError resolve(String template) {
        return BusinessErrors.GENERAL_ERROR;
    }
}
