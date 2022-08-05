package com.foros.session;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.ApplicationException;

/**
 *
 * @author alexey_koloskov
 */
@ApplicationException(rollback = true)
@Deprecated // Use com.foros.validation.constraint.violation.ConstraintViolationException
public class BusinessException extends RuntimeException {
    private List<PropertyError> propertyErrors = new ArrayList<PropertyError>();
    private List<String> entityErrors = new ArrayList<String>();

    public List<String> getEntityErrors() {
        return entityErrors;
    }

    public List<PropertyError> getPropertyErrors() {
        return propertyErrors;
    }

    protected BusinessException() {
    }

    public BusinessException(String propName, String errMsg) {
        super(errMsg);
        propertyErrors.add(new PropertyError(propName, errMsg));
    }

    public BusinessException(String errMsg) {
        super(errMsg);
        getEntityErrors().add(errMsg);
    }

    public BusinessException(String errMsg, Throwable cause) {
        super(errMsg, cause);
        getEntityErrors().add(errMsg);
    }

    public BusinessException(Throwable cause) {
        super(cause);
        getEntityErrors().add(cause.getMessage());
    }

    public BusinessException(List<String> errMsgs) {
        getEntityErrors().addAll(errMsgs);
    }

    public class PropertyError {
        private String name;
        private String message;

        public PropertyError(String name, String msg) {
            this.name = name;
            this.message = msg;
        }

        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }
    }
}
