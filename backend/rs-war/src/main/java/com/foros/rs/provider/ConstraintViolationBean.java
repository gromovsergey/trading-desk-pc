package com.foros.rs.provider;

import com.foros.validation.code.ForosError;
import com.foros.validation.constraint.violation.ConstraintViolation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ConstraintViolation", propOrder = {"code", "message", "path", "value"})
@XmlAccessorType(XmlAccessType.NONE)
public class ConstraintViolationBean {

    private String message;
    private String value;
    private String path;
    private Integer code;

    public ConstraintViolationBean() {
    }

    public ConstraintViolationBean(ConstraintViolation constraintViolation) {
        this(
            constraintViolation.getMessage(),
            constraintViolation.getPropertyPath().toString(),
            constraintViolation.getInvalidValue() != null ? constraintViolation.getInvalidValue().toString() : null,
            constraintViolation.getError()
        );
    }

    public ConstraintViolationBean(String message, String path, String value, ForosError error) {
        this.message = message;
        this.path = path;
        this.value = value;
        this.code = error == null ? null : error.getCode();
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    @XmlElement
    public String getValue() {
        return value;
    }

    @XmlElement
    public String getPath() {
        return path;
    }

    @XmlElement
    public Integer getCode() {
        return code;
    }

}
