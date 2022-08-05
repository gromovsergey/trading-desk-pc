package com.foros.rs.provider;

import com.foros.validation.code.ForosError;
import com.foros.validation.constraint.violation.ConstraintViolation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "constraintViolations")
@XmlAccessorType(XmlAccessType.NONE)
public class ConstraintViolationsBean {

    private Collection<ConstraintViolationBean> constraintViolations;

    public static ConstraintViolationsBean create(Collection<ConstraintViolation> constraintViolations) {
        ConstraintViolationsBean bean = new ConstraintViolationsBean();
        bean.constraintViolations = new ArrayList<ConstraintViolationBean>(constraintViolations.size());
        for (ConstraintViolation constraintViolation : constraintViolations) {
            ConstraintViolationBean constraintViolationBean = new ConstraintViolationBean(constraintViolation);
            bean.constraintViolations.add(constraintViolationBean);
        }
        return bean;
    }

    public static ConstraintViolationsBean create(ForosError error, String message) {
        return create(error, message, null);
    }

    public static ConstraintViolationsBean create(ForosError error, String message, String path) {
        ConstraintViolationsBean bean = new ConstraintViolationsBean();
        bean.constraintViolations = Collections.singleton(new ConstraintViolationBean(message, path, null, error));
        return bean;
    }

    @XmlElement(name = "constraintViolation")
    public Collection<ConstraintViolationBean> getConstraintViolations() {
        return constraintViolations;
    }
}
