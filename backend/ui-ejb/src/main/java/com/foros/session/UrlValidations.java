package com.foros.session;

import com.foros.util.unixcommons.ExternalValidation;
import com.foros.util.url.TriggerURLValidator;
import com.foros.util.url.URLValidator;
import com.foros.util.url.UrlErrorHandler;
import com.foros.validation.ValidationContext;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
public class UrlValidations {

    public static final String[] NO_SCHEMA = {null};

    public void validateTriggerUrl(ValidationContext context, String url, String path) {
        UrlErrorHandler errorHandler = new UrlErrorHandlerImpl(context, url, path);
        TriggerURLValidator.isValid(errorHandler, url);
    }

    public void validateUrl(ValidationContext context, String url, String path, boolean checkNormalization) {
        UrlErrorHandler errorHandler = new UrlErrorHandlerImpl(context, url, path);
        URLValidator.isValid(errorHandler, url);
        if (url != null && checkNormalization && !context.hasViolation(path)) {
            if (!ExternalValidation.validateUrl(url)) {
                errorHandler.invalidURL();
            }
        }
    }

    public void validateUrl(ValidationContext context, String url, String path, String[] schemas, boolean checkNormalization) {
        UrlErrorHandler errorHandler = new UrlErrorHandlerImpl(context, url, path);
        URLValidator.isValid(errorHandler, url, schemas);
        if (url != null && checkNormalization && !context.hasViolation(path)) {
            if (!ExternalValidation.validateUrl(url)) {
                errorHandler.invalidURL();
            }
        }
    }
}
